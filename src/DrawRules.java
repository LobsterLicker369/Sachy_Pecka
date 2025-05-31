import java.util.*;

public class DrawRules {
    private static final Map<String, Integer> repetitionMap = new HashMap<>();
    private static int halfmoveClock = 0;

    /**
     * Records a board position including the player to move.
     * This is important for correct threefold repetition detection.
     *
     * @param board        the current board state
     * @param whiteToMove  true if it's white's turn, false otherwise
     */
    public static void recordPosition(String[][] board, boolean whiteToMove) {
        String key = Arrays.deepToString(board) + (whiteToMove ? "W" : "B");
        repetitionMap.put(key, repetitionMap.getOrDefault(key, 0) + 1);
    }

    /**
     * Checks if the current board state has occurred three times, considering the player to move.
     *
     * @param board        the current board state
     * @param whiteToMove  true if it's white's turn, false otherwise
     * @return true if the same position has occurred three times
     */
    public static boolean isThreefoldRepetition(String[][] board, boolean whiteToMove) {
        String key = Arrays.deepToString(board) + (whiteToMove ? "W" : "B");
        return repetitionMap.getOrDefault(key, 0) >= 3;
    }

    /**
     * Resets the internal repetition map and halfmove clock.
     */
    public static void reset() {
        repetitionMap.clear();
        halfmoveClock = 0;
    }

    /**
     * Updates the halfmove clock based on the type of move played.
     *
     * @param pawnMoved  true if a pawn was moved
     * @param captured   true if a piece was captured
     */
    public static void updateHalfmoveClock(boolean pawnMoved, boolean captured) {
        if (pawnMoved || captured) halfmoveClock = 0;
        else halfmoveClock++;
    }

    /**
     * Checks if the fifty-move rule applies.
     *
     * @return true if 50 moves (100 ply) have occurred without pawn move or capture
     */
    public static boolean isFiftyMoveRuleDraw() {
        return halfmoveClock >= 100;
    }
}