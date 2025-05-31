import java.util.ArrayList;
import java.util.List;

/**
 * Controls the overall flow of the chess game, including move history,
 * undo/redo functionality, and notation logging.
 */
public class GameController {
    private static GameController instance;

    private final ChessBoardPanel board;
    private final GameLog gameLog;
    private final List<String[][]> history = new ArrayList<>();
    private int historyIndex = -1;
    private boolean loggingEnabled = true;

    private final List<String> moveNotations = new ArrayList<>(); // To store move notations

    /**
     * Constructs the GameController and saves the initial board state.
     *
     * @param board the chessboard panel
     */
    public GameController(ChessBoardPanel board) {
        this.board = board;
        this.gameLog = board.getGameLog(); // Access to the game log
        saveState(); // Initial state
        instance = this;
    }

    /**
     * Returns the singleton instance of the controller.
     *
     * @return the current GameController instance
     */
    public static GameController getInstance() {
        return instance;
    }

    /**
     * Indicates whether move logging is enabled.
     *
     * @return true if logging is enabled
     */
    public boolean isLoggingEnabled() {
        return loggingEnabled;
    }

    /**
     * Saves the current state of the board to the history list.
     * Also records the position for threefold repetition detection.
     */
    public void saveState() {
        while (history.size() > historyIndex + 1) {
            history.remove(history.size() - 1);
            moveNotations.remove(moveNotations.size() - 1);
        }
        history.add(SpecialMoves.copyBoard(board.getBoard()));
        historyIndex++;

        boolean whiteToMove = historyIndex % 2 == 0;
        DrawRules.recordPosition(board.getBoard(), whiteToMove);
    }

    /**
     * Reverts the last move if possible.
     */
    public void undo() {
        if (historyIndex > 0) {
            loggingEnabled = false;
            historyIndex--;
            board.setBoard(SpecialMoves.copyBoard(history.get(historyIndex)));
            board.switchPlayer();
            gameLog.removeLastMove();
            board.redraw();
            loggingEnabled = true;
        }
    }

    /**
     * Reapplies a previously undone move if possible.
     */
    public void redo() {
        if (historyIndex < history.size() - 1) {
            loggingEnabled = false;
            historyIndex++;
            board.setBoard(SpecialMoves.copyBoard(history.get(historyIndex)));
            board.switchPlayer();
            board.redraw();

            if (historyIndex < moveNotations.size()) {
                String notation = moveNotations.get(historyIndex);
                gameLog.addNotationDirectly(notation);
            }

            loggingEnabled = true;
        }
    }

    /**
     * Saves a move notation to the notation history.
     *
     * @param notation the algebraic notation of the move
     */
    public void saveNotation(String notation) {
        while (moveNotations.size() > historyIndex) {
            moveNotations.remove(moveNotations.size() - 1);
        }
        moveNotations.add(notation);
    }

    /**
     * Returns the index of the current board state in the history.
     *
     * @return the current history index
     */
    public int getHistoryIndex() {
        return historyIndex;
    }
}