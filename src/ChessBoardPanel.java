import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;

public class ChessBoardPanel extends JPanel {

    private final int rows = 9;
    private final int cols = 9;
    private final Color lightColor = new Color(240, 217, 181);
    private final Color darkColor = new Color(181, 136, 99);

    // Hraci pole 8x8, indexy od 0 do 7 (bez popisku)
    private String[][] board = new String[8][8];

    // Ulozeni moznych tahu pro aktualne vybranou figurku
    private Set<Point> legalMoves = new HashSet<>();

    // Vybrany ctverecek (pohyb figurky)
    private int selectedRow = -1;
    private int selectedCol = -1;

    // Kdo je na tahu (true = bily, false = cerny)
    private boolean whiteToMove = true;

    // En passant souradnice (pokud existuji), jinak -1
    private int enPassantRow = -1;
    private int enPassantCol = -1;

    public ChessBoardPanel() {
        setLayout(new GridLayout(rows, cols));
        initBoard();
        drawBoard();
    }

    // Inicializace sachovnice (vychozi pozice)
    private void initBoard() {
        // Prazdna pola
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                board[r][c] = null;

        // Pesi
        for (int c = 0; c < 8; c++) {
            board[1][c] = "p"; // cerny pesec
            board[6][c] = "P"; // bily pesec
        }

        // Cerne figury (dolni rada cernych)
        board[0][0] = "r"; board[0][1] = "n"; board[0][2] = "b"; board[0][3] = "q";
        board[0][4] = "k"; board[0][5] = "b"; board[0][6] = "n"; board[0][7] = "r";

        // Bile figury (horni rada bilych)
        board[7][0] = "R"; board[7][1] = "N"; board[7][2] = "B"; board[7][3] = "Q";
        board[7][4] = "K"; board[7][5] = "B"; board[7][6] = "N"; board[7][7] = "R";
    }

    private JLabel createCoordLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        return label;
    }

    private void drawBoard() {
        removeAll();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (row == 8 && col == 0) {
                    add(new JLabel("")); // levy dolni roh
                } else if (row == 8) {
                    // popisky sloupcu a-h
                    char label = (char) ('a' + col - 1);
                    add(createCoordLabel(String.valueOf(label)));
                } else if (col == 0) {
                    // popisky radku 8-1
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

                    // Oznamit mozne tahy sede kulate
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

    // Prevadi znak figurky na Unicode znak
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

    // Osetreni kliknuti na pole
    private void handleClick(int row, int col) {
        String piece = board[row][col];
        if (selectedRow == -1) {
            // Vyber figurky
            if (piece != null && isWhite(piece) == whiteToMove) {
                selectedRow = row;
                selectedCol = col;
                legalMoves = calculateLegalMoves(row, col);
            }
        } else {
            // Jestli kliknul na legalni tah, proved pohyb
            Point target = new Point(row, col);
            if (legalMoves.contains(target)) {
                makeMove(selectedRow, selectedCol, row, col);
                whiteToMove = !whiteToMove;
            }
            // Reset vyberu a tahu
            selectedRow = -1;
            selectedCol = -1;
            legalMoves.clear();
        }
        drawBoard();
    }

    private boolean isWhite(String piece) {
        if (piece == null) return false;
        return piece.equals(piece.toUpperCase());
    }

    // Provede tah a resi en passant (zakladni)
    private void makeMove(int fromRow, int fromCol, int toRow, int toCol) {
        String movingPiece = board[fromRow][fromCol];

        // En passant capture
        if (movingPiece.equals("P") || movingPiece.equals("p")) {
            if (toRow == enPassantRow && toCol == enPassantCol) {
                // Sejmeme pesce za pescem
                if (movingPiece.equals("P")) board[toRow + 1][toCol] = null;
                else board[toRow - 1][toCol] = null;
            }
        }

        // Aktualizace en passant pozice (kdyz pesec jde o 2)
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

    // Vypocita vsechny legalni tahy pro figurku na pozici row, col
    private Set<Point> calculateLegalMoves(int row, int col) {
        Set<Point> moves = new HashSet<>();
        String piece = board[row][col];
        if (piece == null) return moves;

        switch (piece.toUpperCase()) {
            case "P" -> moves.addAll(pawnMoves(row, col, piece));
            case "R" -> moves.addAll(straightLineMoves(row, col, piece, new int[][]{{1,0}, {-1,0}, {0,1}, {0,-1}}));
            case "B" -> moves.addAll(straightLineMoves(row, col, piece, new int[][]{{1,1}, {1,-1}, {-1,1}, {-1,-1}}));
            case "Q" -> {
                moves.addAll(straightLineMoves(row, col, piece, new int[][]{{1,0}, {-1,0}, {0,1}, {0,-1}}));
                moves.addAll(straightLineMoves(row, col, piece, new int[][]{{1,1}, {1,-1}, {-1,1}, {-1,-1}}));
            }
            case "N" -> moves.addAll(knightMoves(row, col, piece));
            case "K" -> moves.addAll(kingMoves(row, col, piece));
        }
        return moves;
    }

    // Tahy pesce ( en passant)
    private Set<Point> pawnMoves(int row, int col, String piece) {
        Set<Point> moves = new HashSet<>();
        int dir = isWhite(piece) ? -1 : 1; // bily jde nahoru (-1), cerny dolu (+1)
        int startRow = isWhite(piece) ? 6 : 1;

        // Posun vpred o 1
        if (isInBounds(row + dir, col) && board[row + dir][col] == null) {
            moves.add(new Point(row + dir, col));
            // Posun vpred o 2 pokud na startu
            if (row == startRow && board[row + 2 * dir][col] == null) {
                moves.add(new Point(row + 2 * dir, col));
            }
        }

        // Jizda na diagonale (sebrani)
        int[] sideCols = {col - 1, col + 1};
        for (int c2 : sideCols) {
            if (isInBounds(row + dir, c2)) {
                String target = board[row + dir][c2];
                if (target != null && isWhite(target) != isWhite(piece)) {
                    moves.add(new Point(row + dir, c2));
                }
                // En passant
                if (row + dir == enPassantRow && c2 == enPassantCol) {
                    moves.add(new Point(enPassantRow, enPassantCol));
                }
            }
        }

        return moves;
    }

    // Tahy veze, strelce, damy (pohyb v primkach)
    private Set<Point> straightLineMoves(int row, int col, String piece, int[][] directions) {
        Set<Point> moves = new HashSet<>();
        boolean isWhite = isWhite(piece);

        for (int[] d : directions) {
            int r = row + d[0];
            int c = col + d[1];
            while (isInBounds(r, c)) {
                String target = board[r][c];
                if (target == null) {
                    moves.add(new Point(r, c));
                } else {
                    if (isWhite != isWhite(target)) {
                        moves.add(new Point(r, c));
                    }
                    break;
                }
                r += d[0];
                c += d[1];
            }
        }
        return moves;
    }

    // Tahy jezdce
    private Set<Point> knightMoves(int row, int col, String piece) {
        Set<Point> moves = new HashSet<>();
        boolean isWhite = isWhite(piece);
        int[][] jumps = {{-2,-1}, {-2,1}, {-1,-2}, {-1,2}, {1,-2}, {1,2}, {2,-1}, {2,1}};
        for (int[] j : jumps) {
            int r = row + j[0];
            int c = col + j[1];
            if (isInBounds(r, c)) {
                String target = board[r][c];
                if (target == null || isWhite != isWhite(target)) {
                    moves.add(new Point(r, c));
                }
            }
        }
        return moves;
    }

    // Tahy krale (bez rosady)
    private Set<Point> kingMoves(int row, int col, String piece) {
        Set<Point> moves = new HashSet<>();
        boolean isWhite = isWhite(piece);
        for (int r = row - 1; r <= row + 1; r++) {
            for (int c = col - 1; c <= col + 1; c++) {
                if (isInBounds(r, c) && !(r == row && c == col)) {
                    String target = board[r][c];
                    if (target == null || isWhite != isWhite(target)) {
                        moves.add(new Point(r, c));
                    }
                }
            }
        }
        return moves;
    }

    private boolean isInBounds(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    // Vnitrni trida pro jedno pole sachovnice
    private static class SquarePanel extends JPanel {
        private final int row;
        private final int col;
        private final ChessBoardPanel parent;
        private boolean legalMove = false;

        public SquarePanel(int row, int col, ChessBoardPanel parent) {
            this.row = row;
            this.col = col;
            this.parent = parent;
            setLayout(new BorderLayout());
        }

        public void setLegalMove(boolean legal) {
            this.legalMove = legal;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (legalMove) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(new Color(128, 128, 128, 150)); // siva poloprzezracna
                int diameter = Math.min(getWidth(), getHeight()) / 3;
                g2d.fillOval((getWidth() - diameter) / 2, (getHeight() - diameter) / 2, diameter, diameter);
            }
        }
    }
}
