import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

/**
 * Handles special chess rules like castling, en passant, and check detection.
 */
public class SpecialMoves {
    private String[][] board;
    private boolean whiteToMove;
    private int enPassantRow;
    private int enPassantCol;

    public SpecialMoves(String[][] board, boolean whiteToMove, int enPassantRow, int enPassantCol) {
        this.board = copyBoard(board);
        this.whiteToMove = whiteToMove;
        this.enPassantRow = enPassantRow;
        this.enPassantCol = enPassantCol;
    }

    /**
     * Returns a deep copy of the given board.
     */
    public static String[][] copyBoard(String[][] source) {
        String[][] copy = new String[8][8];
        for (int i = 0; i < 8; i++)
            System.arraycopy(source[i], 0, copy[i], 0, 8);
        return copy;
    }

    /**
     * Returns true if the king of the given color is in check.
     */
    public boolean isKingInCheck(boolean white) {
        Point kingPos = findKing(white);
        if (kingPos == null) return true; // No king found, consider it checkmate

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                String piece = board[r][c];
                if (piece == null) continue;
                if (isWhite(piece) != white) {
                    Set<Point> moves = getPseudoLegalMoves(r, c, false);
                    if (moves.contains(kingPos)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Returns true if the current player has no legal moves.
     */
    public boolean hasNoLegalMoves(boolean white) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                String piece = board[r][c];
                if (piece != null && isWhite(piece) == white) {
                    Set<Point> moves = getLegalMoves(r, c);
                    if (!moves.isEmpty()) return false;
                }
            }
        }
        return true;
    }

    /**
     * Returns the set of legal moves for the piece at the given position.
     */
    public Set<Point> getLegalMoves(int row, int col) {
        Set<Point> legal = new HashSet<>();
        String piece = board[row][col];
        if (piece == null) return legal;
        if (isWhite(piece) != whiteToMove) return legal;

        Set<Point> pseudoMoves = getPseudoLegalMoves(row, col, true);

        for (Point target : pseudoMoves) {
            String[][] boardCopy = copyBoard(board);
            boardCopy[target.x][target.y] = piece;
            boardCopy[row][col] = null;

            SpecialMoves test = new SpecialMoves(boardCopy, whiteToMove, enPassantRow, enPassantCol);
            if (!test.isKingInCheck(whiteToMove)) {
                legal.add(target);
            }
        }

        return legal;
    }

    /**
     * Returns all pseudo-legal moves (ignoring checks) for the piece at the given position.
     */
    private Set<Point> getPseudoLegalMoves(int row, int col, boolean includeSpecial) {
        Set<Point> moves = new HashSet<>();
        String piece = board[row][col];
        if (piece == null) return moves;
        boolean white = isWhite(piece);
        int dir = white ? -1 : 1;

        switch (piece.toLowerCase()) {
            case "p" -> { // Pawn
                int nextRow = row + dir;
                if (inBounds(nextRow, col) && board[nextRow][col] == null) {
                    moves.add(new Point(nextRow, col));
                    if ((white && row == 6 || !white && row == 1) && board[nextRow + dir][col] == null) {
                        moves.add(new Point(nextRow + dir, col));
                    }
                }
                for (int dc = -1; dc <= 1; dc += 2) {
                    int nr = row + dir;
                    int nc = col + dc;
                    if (inBounds(nr, nc)) {
                        String target = board[nr][nc];
                        if (target != null && isWhite(target) != white) {
                            moves.add(new Point(nr, nc));
                        }
                        if (includeSpecial && nr == enPassantRow && nc == enPassantCol) {
                            moves.add(new Point(nr, nc));
                        }
                    }
                }
            }
            case "n" -> { // Knight
                int[][] knightMoves = {{-2,-1}, {-2,1}, {-1,-2}, {-1,2}, {1,-2}, {1,2}, {2,-1}, {2,1}};
                for (int[] m : knightMoves) {
                    int nr = row + m[0];
                    int nc = col + m[1];
                    if (inBounds(nr, nc) && (board[nr][nc] == null || isWhite(board[nr][nc]) != white)) {
                        moves.add(new Point(nr, nc));
                    }
                }
            }
            case "b" -> moves.addAll(slidingMoves(row, col, white, new int[][]{{1,1}, {1,-1}, {-1,1}, {-1,-1}})); // Bishop
            case "r" -> moves.addAll(slidingMoves(row, col, white, new int[][]{{1,0}, {-1,0}, {0,1}, {0,-1}})); // Rook
            case "q" -> moves.addAll(slidingMoves(row, col, white, new int[][]{{1,0}, {-1,0}, {0,1}, {0,-1}, {1,1}, {1,-1}, {-1,1}, {-1,-1}})); // Queen
            case "k" -> { // King
                for (int dr = -1; dr <= 1; dr++) {
                    for (int dc = -1; dc <= 1; dc++) {
                        if (dr == 0 && dc == 0) continue;
                        int nr = row + dr;
                        int nc = col + dc;
                        if (inBounds(nr, nc) && (board[nr][nc] == null || isWhite(board[nr][nc]) != white)) {
                            moves.add(new Point(nr, nc));
                        }
                    }
                }

                // Castling
                if (includeSpecial) {
                    if (white && row == 7 && col == 4) {
                        // White king-side
                        if ("R".equals(board[7][7]) &&
                                board[7][5] == null && board[7][6] == null &&
                                !isKingInCheck(true) &&
                                !isSquareAttacked(7, 5, false) &&
                                !isSquareAttacked(7, 6, false)) {
                            moves.add(new Point(7, 6));
                        }

                        // White queen-side
                        if ("R".equals(board[7][0]) &&
                                board[7][1] == null && board[7][2] == null && board[7][3] == null &&
                                !isKingInCheck(true) &&
                                !isSquareAttacked(7, 3, false) &&
                                !isSquareAttacked(7, 2, false)) {
                            moves.add(new Point(7, 2));
                        }
                    }

                    if (!white && row == 0 && col == 4) {
                        // Black king-side
                        if ("r".equals(board[0][7]) &&
                                board[0][5] == null && board[0][6] == null &&
                                !isKingInCheck(false) &&
                                !isSquareAttacked(0, 5, true) &&
                                !isSquareAttacked(0, 6, true)) {
                            moves.add(new Point(0, 6));
                        }

                        // Black queen-side
                        if ("r".equals(board[0][0]) &&
                                board[0][1] == null && board[0][2] == null && board[0][3] == null &&
                                !isKingInCheck(false) &&
                                !isSquareAttacked(0, 3, true) &&
                                !isSquareAttacked(0, 2, true)) {
                            moves.add(new Point(0, 2));
                        }
                    }
                }
            }
        }

        return moves;
    }

    /**
     * Returns valid moves for sliding pieces like rook, bishop, queen.
     */
    private Set<Point> slidingMoves(int row, int col, boolean white, int[][] directions) {
        Set<Point> moves = new HashSet<>();
        for (int[] d : directions) {
            int nr = row + d[0];
            int nc = col + d[1];
            while (inBounds(nr, nc)) {
                if (board[nr][nc] == null) {
                    moves.add(new Point(nr, nc));
                } else {
                    if (isWhite(board[nr][nc]) != white) {
                        moves.add(new Point(nr, nc));
                    }
                    break;
                }
                nr += d[0];
                nc += d[1];
            }
        }
        return moves;
    }

    /**
     * Finds the position of the king of the given color.
     */
    private Point findKing(boolean white) {
        String king = white ? "K" : "k";
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                if (king.equals(board[r][c]))
                    return new Point(r, c);
        return null;
    }

    /**
     * Checks if the given coordinates are on the board.
     */
    private boolean inBounds(int r, int c) {
        return r >= 0 && r < 8 && c >= 0 && c < 8;
    }

    /**
     * Returns true if the given piece is white.
     */
    private boolean isWhite(String piece) {
        return piece != null && piece.equals(piece.toUpperCase());
    }

    /**
     * Returns true if the given square is attacked by the specified side.
     */
    private boolean isSquareAttacked(int row, int col, boolean byWhite) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                String piece = board[r][c];
                if (piece == null || isWhite(piece) != byWhite) continue;

                Set<Point> moves = getPseudoLegalMoves(r, c, false);
                if (moves.contains(new Point(row, col))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Public version of findKing for external access.
     */
    public Point findKingPublic(boolean white) {
        return findKing(white);
    }

    /**
     * Finds the piece attacking the given king position.
     */
    public Point findAttackerTo(Point kingPos, boolean attackerWhite) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                String piece = board[r][c];
                if (piece == null || isWhite(piece) != attackerWhite) continue;
                Set<Point> moves = getPseudoLegalMoves(r, c, false);
                if (moves.contains(kingPos)) return new Point(r, c);
            }
        }
        return null;
    }
}
