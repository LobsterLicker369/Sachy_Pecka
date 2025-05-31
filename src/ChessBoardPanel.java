import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;

/**
 * Panel representing the graphical chessboard.
 */
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
    private GameLog gameLog;
    private Color boardOverlay = null;

    /**
     * Constructor for ChessBoardPanel.
     * @param gameLog game log to store move history
     */
    public ChessBoardPanel(GameLog gameLog) {
        this.gameLog = gameLog;
        setLayout(new GridLayout(rows, cols));
        initBoard();
        drawBoard();
    }

    /**
     * Initializes the board with starting positions of all pieces.
     */
    private void initBoard() {
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                board[r][c] = null;

        for (int c = 0; c < 8; c++) {
            board[1][c] = "p";
            board[6][c] = "P";
        }

        board[0][0] = "r";
        board[0][1] = "n";
        board[0][2] = "b";
        board[0][3] = "q";
        board[0][4] = "k";
        board[0][5] = "b";
        board[0][6] = "n";
        board[0][7] = "r";

        board[7][0] = "R";
        board[7][1] = "N";
        board[7][2] = "B";
        board[7][3] = "Q";
        board[7][4] = "K";
        board[7][5] = "B";
        board[7][6] = "N";
        board[7][7] = "R";
    }

    /**
     * Creates a coordinate label for row or column.
     * @param text label text
     * @return JLabel for coordinate
     */
    private JLabel createCoordLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        return label;
    }

    /**
     * Draws the chessboard including pieces and highlights.
     */
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

                    if (MoveAnimations.isLastMoveSquare(boardRow, boardCol))
                        square.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));
                    if (MoveAnimations.isCheckHighlight(boardRow, boardCol))
                        square.setBorder(BorderFactory.createLineBorder(Color.RED, 3));

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
        if (boardOverlay != null) setBackground(boardOverlay);
        else setBackground(null);

        revalidate();
        repaint();
    }

    /**
     * Returns the corresponding Unicode symbol for a piece.
     * @param piece piece code
     * @return Unicode symbol
     */
    private String pieceToUnicode(String piece) {
        if (piece == null) return "";
        return switch (piece) {
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

    /**
     * Handles a mouse click on a square.
     * @param row clicked row
     * @param col clicked column
     */
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
                    JOptionPane.showMessageDialog(this, "Illegal move, king would be in check!");
                }
            }
            selectedRow = -1;
            selectedCol = -1;
            legalMoves.clear();
        }
        drawBoard();
    }

    /**
     * Checks if a piece is white.
     * @param piece piece code
     * @return true if white, false otherwise
     */
    private boolean isWhite(String piece) {
        if (piece == null) return false;
        return piece.equals(piece.toUpperCase());
    }

    /**
     * Tries to make a move, checking for legality.
     * @return true if move is legal
     */
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
        if (special.isKingInCheck(whiteToMove)) return false;

        makeMove(fromRow, fromCol, toRow, toCol);

        if (movingPiece.equals("P") && toRow == 0) promotePawn(toRow, toCol, true);
        else if (movingPiece.equals("p") && toRow == 7) promotePawn(toRow, toCol, false);

        return true;
    }

    /**
     * Makes a move on the board and updates state.
     */
    private void makeMove(int fromRow, int fromCol, int toRow, int toCol) {
        String movingPiece = board[fromRow][fromCol];
        String capturedPiece = board[toRow][toCol];

        if (movingPiece.equalsIgnoreCase("p") && toRow == enPassantRow && toCol == enPassantCol) {
            if (movingPiece.equals("P")) {
                capturedPiece = board[toRow + 1][toCol];
                board[toRow + 1][toCol] = null;
            } else {
                capturedPiece = board[toRow - 1][toCol];
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

        GameController.getInstance().saveState();
        if (gameLog != null && GameController.getInstance().isLoggingEnabled()) {
            String notation = gameLog.getLastNotationPreview(fromRow, fromCol, toRow, toCol, movingPiece, capturedPiece, board, whiteToMove);
            GameController.getInstance().saveNotation(notation);
            gameLog.registerMove(fromRow, fromCol, toRow, toCol, movingPiece, capturedPiece, board, whiteToMove);
        }
        MoveAnimations.saveLastMove(fromRow, fromCol, toRow, toCol);
        SpecialMoves sm = new SpecialMoves(board, whiteToMove, enPassantRow, enPassantCol);
        if (sm.isKingInCheck(!whiteToMove)) {
            Point k = sm.findKingPublic(!whiteToMove), a = sm.findAttackerTo(k, whiteToMove);
            MoveAnimations.setCheckHighlight(k, a);
        } else MoveAnimations.clearCheckHighlight();
        boolean pawn = movingPiece.equalsIgnoreCase("p");
        boolean captured = capturedPiece != null;
        DrawRules.updateHalfmoveClock(pawn, captured);
        DrawRules.recordPosition(board, whiteToMove);

    }

    /**
     * Promotes a pawn to the selected piece.
     */
    private void promotePawn(int row, int col, boolean white) {
        String[] options = {"Queen", "Rook", "Bishop", "Knight"};
        String choice = (String) JOptionPane.showInputDialog(this, "Promote pawn to:", "Pawn Promotion",
                JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
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

    /**
     * Calculates legal moves for the selected piece.
     * @return set of legal move points
     */
    private Set<Point> calculateLegalMoves(int row, int col) {
        String piece = board[row][col];
        if (piece == null) return new HashSet<>();
        SpecialMoves special = new SpecialMoves(board, whiteToMove, enPassantRow, enPassantCol);
        return special.getLegalMoves(row, col);
    }

    /**
     * Checks if the game has ended by checkmate, stalemate or draw.
     */
    private void checkEndGame() {
        SpecialMoves special = new SpecialMoves(board, whiteToMove, enPassantRow, enPassantCol);
        boolean isCheck = special.isKingInCheck(whiteToMove);
        boolean noMoves = special.hasNoLegalMoves(whiteToMove);
        if ((isCheck || !isCheck) && noMoves) {
            String result = isCheck ? (whiteToMove ? "Black wins" : "White wins") : "Draw";
            boardOverlay = isCheck ? Color.GREEN : Color.BLUE;
            Timer.stopStatic();
            SaveGame.saveToHistory(result, gameLog);
            JOptionPane.showMessageDialog(this, result);
            SaveGame.showStatsAfterGame();
            SwingUtilities.getWindowAncestor(this).dispose();
            MainMenu.show();
        } else if (isCheck) {
            boardOverlay = Color.RED;
        } else {
            boardOverlay = null;
        }
        if (DrawRules.isThreefoldRepetition(board, whiteToMove) || DrawRules.isFiftyMoveRuleDraw()) {

            boardOverlay = Color.BLUE;
            Timer.stopStatic();
            SaveGame.saveToHistory("Draw", gameLog);
            JOptionPane.showMessageDialog(this, "Draw: repetition or 50 moves.");
            SaveGame.showStatsAfterGame();
            SwingUtilities.getWindowAncestor(this).dispose();
            MainMenu.show();
        }
    }

    public String[][] getBoard() {
        return board;
    }

    public void setBoard(String[][] newBoard) {
        this.board = newBoard;
    }

    public void redraw() {
        drawBoard();
    }

    public void switchPlayer() {
        whiteToMove = !whiteToMove;
    }

    public GameLog getGameLog() {
        return gameLog;
    }
}
