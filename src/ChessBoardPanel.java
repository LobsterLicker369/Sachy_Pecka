import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;

public class ChessBoardPanel extends JPanel {

    private final int rows = 9;
    private final int cols = 9;
    private final Color lightColor = new Color(240, 217, 181);
    private final Color darkColor = new Color(181, 136, 99);

    private String[][] board = new String[8][8];
    private Set<Point> legalMoves = new HashSet<>();
    private int selectedRow = -1;
    private int selectedCol = -1;
    private boolean whiteToMove = true;
    private int enPassantRow = -1;
    private int enPassantCol = -1;

    public ChessBoardPanel() {
        setLayout(new GridLayout(rows, cols));
        initBoard();
        drawBoard();
    }

    // Inicializuje startovni postaveni figur
    private void initBoard() {
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                board[r][c] = null;

        for (int c = 0; c < 8; c++) {
            board[1][c] = "p";
            board[6][c] = "P";
        }

        board[0][0] = "r"; board[0][1] = "n"; board[0][2] = "b"; board[0][3] = "q";
        board[0][4] = "k"; board[0][5] = "b"; board[0][6] = "n"; board[0][7] = "r";

        board[7][0] = "R"; board[7][1] = "N"; board[7][2] = "B"; board[7][3] = "Q";
        board[7][4] = "K"; board[7][5] = "B"; board[7][6] = "N"; board[7][7] = "R";
    }

    // Vytvari graficky popisek pro souradnici
    private JLabel createCoordLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        return label;
    }

    // Zobrazuje sachovnici a vykresluje figurky a legalni tahy
    private void drawBoard() {
        removeAll();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (row == 8 && col == 0) {
                    add(new JLabel(""));
                } else if (row == 8) {
                    char label = (char) ('a' + col - 1);
                    add(createCoordLabel(String.valueOf(label)));
                } else if (col == 0) {
                    int label = 8 - row;
                    add(createCoordLabel(String.valueOf(label)));
                } else {
                    int boardRow = row;
                    int boardCol = col - 1;

                    SquarePanel square = new SquarePanel(boardRow, boardCol, this);
                    boolean isLight = (boardRow + boardCol) % 2 == 0;
                    square.setBackground(isLight ? lightColor : darkColor);

                    String piece = board[boardRow][boardCol];
                    JLabel pieceLabel = new JLabel(pieceToUnicode(piece), SwingConstants.CENTER);
                    pieceLabel.setFont(new Font("SansSerif", Font.PLAIN, 32));
                    square.add(pieceLabel);

                    square.setLegalMove(legalMoves.contains(new Point(boardRow, boardCol)));

                    square.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            handleClick(boardRow, boardCol);
                        }
                    });

                    add(square);
                }
            }
        }

        revalidate();
        repaint();
    }

    // Vraci unicode znak podle typu figurky
    private String pieceToUnicode(String piece) {
        if (piece == null) return "";
        return switch(piece) {
            case "K" -> "♔";
            case "Q" -> "♕";
            case "R" -> "♖";
            case "B" -> "♗";
            case "N" -> "♘";
            case "P" -> "♙";
            case "k" -> "♚";
            case "q" -> "♛";
            case "r" -> "♜";
            case "b" -> "♝";
            case "n" -> "♞";
            case "p" -> "♟";
            default -> "";
        };
    }

    // Zpracovava kliknuti na pole
    private void handleClick(int row, int col) {
        String piece = board[row][col];
        if (selectedRow == -1) {
            if (piece != null && isWhite(piece) == whiteToMove) {
                selectedRow = row;
                selectedCol = col;
                legalMoves = calculateLegalMoves(row, col);
            }
        } else {
            Point target = new Point(row, col);
            if (legalMoves.contains(target)) {
                if (tryMakeMove(selectedRow, selectedCol, row, col)) {
                    whiteToMove = !whiteToMove;
                    checkEndGame();
                } else {
                    JOptionPane.showMessageDialog(this, "Nelze provést tah, král by byl v šachu!");
                }
            }
            selectedRow = -1;
            selectedCol = -1;
            legalMoves.clear();
        }
        drawBoard();
    }

    // Vraci true pokud je figurka bila
    private boolean isWhite(String piece) {
        if (piece == null) return false;
        return piece.equals(piece.toUpperCase());
    }

    // Zkousi provest tah a overuje jestli nezpusobi sach
    private boolean tryMakeMove(int fromRow, int fromCol, int toRow, int toCol) {
        String movingPiece = board[fromRow][fromCol];

        String[][] boardCopy = SpecialMoves.copyBoard(board);

        int enPassantRowCopy = enPassantRow;
        int enPassantColCopy = enPassantCol;

        if (movingPiece.equalsIgnoreCase("p")) {
            if (toRow == enPassantRowCopy && toCol == enPassantColCopy) {
                if (movingPiece.equals("P"))
                    boardCopy[toRow + 1][toCol] = null;
                else
                    boardCopy[toRow - 1][toCol] = null;
            }
        }

        if (movingPiece.equals("P") && fromRow == 6 && toRow == 4) {
            enPassantRowCopy = 5;
            enPassantColCopy = fromCol;
        } else if (movingPiece.equals("p") && fromRow == 1 && toRow == 3) {
            enPassantRowCopy = 2;
            enPassantColCopy = fromCol;
        } else {
            enPassantRowCopy = -1;
            enPassantColCopy = -1;
        }

        boardCopy[toRow][toCol] = movingPiece;
        boardCopy[fromRow][fromCol] = null;

        SpecialMoves special = new SpecialMoves(boardCopy, whiteToMove, enPassantRowCopy, enPassantColCopy);
        if (special.isKingInCheck(whiteToMove)) {
            return false;
        }

        makeMove(fromRow, fromCol, toRow, toCol);

        if (movingPiece.equals("P") && toRow == 0) {
            promotePawn(toRow, toCol, true);
        } else if (movingPiece.equals("p") && toRow == 7) {
            promotePawn(toRow, toCol, false);
        }

        return true;
    }

    // Provadi tah na desce a aktualizuje en passant
    private void makeMove(int fromRow, int fromCol, int toRow, int toCol) {
        String movingPiece = board[fromRow][fromCol];

        if (movingPiece.equalsIgnoreCase("p")) {
            if (toRow == enPassantRow && toCol == enPassantCol) {
                if (movingPiece.equals("P"))
                    board[toRow + 1][toCol] = null;
                else
                    board[toRow - 1][toCol] = null;
            }
        }

        if (movingPiece.equals("P") && fromRow == 6 && toRow == 4) {
            enPassantRow = 5;
            enPassantCol = fromCol;
        } else if (movingPiece.equals("p") && fromRow == 1 && toRow == 3) {
            enPassantRow = 2;
            enPassantCol = fromCol;
        } else {
            enPassantRow = -1;
            enPassantCol = -1;
        }

        board[toRow][toCol] = movingPiece;
        board[fromRow][fromCol] = null;
    }

    // Provadi promocni volbu pesce
    private void promotePawn(int row, int col, boolean white) {
        String[] options = {"Queen", "Rook", "Bishop", "Knight"};
        String choice = (String) JOptionPane.showInputDialog(this,
                "Promote pawn to:",
                "Pawn Promotion",
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]);

        if (choice == null) choice = "Queen";

        String promotedPiece = switch (choice) {
            case "Queen" -> white ? "Q" : "q";
            case "Rook" -> white ? "R" : "r";
            case "Bishop" -> white ? "B" : "b";
            case "Knight" -> white ? "N" : "n";
            default -> white ? "Q" : "q";
        };

        board[row][col] = promotedPiece;
    }

    // Vraci mnozinu legalnich tahu pro zvolenou figurku
    private Set<Point> calculateLegalMoves(int row, int col) {
        String piece = board[row][col];
        if (piece == null) return new HashSet<>();
        SpecialMoves special = new SpecialMoves(board, whiteToMove, enPassantRow, enPassantCol);
        return special.getLegalMoves(row, col);
    }

    // Kontroluje jestli hra skoncila s matem nebo patem
    private void checkEndGame() {
        SpecialMoves special = new SpecialMoves(board, whiteToMove, enPassantRow, enPassantCol);

        if (special.isKingInCheck(whiteToMove)) {
            if (special.hasNoLegalMoves(whiteToMove)) {
                JOptionPane.showMessageDialog(this, "Checkmate! " + (whiteToMove ? "Black" : "White") + " wins.");
                System.exit(0);
            } else {
                JOptionPane.showMessageDialog(this, "Check!");
            }
        } else {
            if (special.hasNoLegalMoves(whiteToMove)) {
                JOptionPane.showMessageDialog(this, "Stalemate! It's a draw.");
                System.exit(0);
            }
        }
    }
}
