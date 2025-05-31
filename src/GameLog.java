import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A JPanel component that maintains and displays the game log for chess moves.
 * Automatically formats moves into algebraic notation and supports undo/redo.
 */
public class GameLog extends JPanel {
    private JTextArea logArea;
    private int moveNumber = 1;
    private List<String> moves = new ArrayList<>();

    /**
     * Constructs the GameLog panel with a non-editable text area inside a scroll pane.
     */
    public GameLog() {
        setLayout(new BorderLayout());
        logArea = new JTextArea(20, 20);
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Registers a new move in the game log.
     * @param fromRow starting row of the piece
     * @param fromCol starting column of the piece
     * @param toRow destination row of the piece
     * @param toCol destination column of the piece
     * @param piece piece type as a string (e.g., "P", "N")
     * @param captured piece captured on the destination square, if any
     * @param board current board state
     * @param whiteMove true if it's white's move, false if black's
     */
    public void registerMove(int fromRow, int fromCol, int toRow, int toCol, String piece, String captured,
                             String[][] board, boolean whiteMove) {
        String move = buildNotation(fromRow, fromCol, toRow, toCol, piece, captured, board, whiteMove);
        if (whiteMove) {
            moves.add(moveNumber + ". " + move);
        } else {
            if (moves.isEmpty()) {
                // White move was undone, black move exists alone
                moves.add(moveNumber + ". ... , " + move);
            } else {
                String last = moves.remove(moves.size() - 1);
                moves.add(last + " , " + move);
            }
            moveNumber++;
        }
        updateText();
    }


    /**
     * Updates the displayed text area with the current move list.
     */
    private void updateText() {
        logArea.setText("");
        for (String move : moves) {
            logArea.append(move + "\n");
        }
    }

    /**
     * Removes the last move from the log.
     * If it was black's move, removes only that half-move.
     * If it was white's move, removes the entire move entry.
     */
    public void removeLastMove() {
        if (moves.isEmpty()) return;
        String last = moves.get(moves.size() - 1);
        if (last.contains(",")) {
            String whitePart = last.substring(0, last.indexOf(",")).trim();
            moves.set(moves.size() - 1, whitePart);
        } else {
            moves.remove(moves.size() - 1);
            moveNumber--;
        }
        updateText();
    }

    /**
     * Clears the entire game log and resets move numbering.
     */
    public void clearLog() {
        logArea.setText("");
        moveNumber = 1;
        moves.clear();
    }

    /**
     * Returns all recorded moves as a single string, separated by new lines.
     * @return all moves in the log
     */
    public String getAllMoves() {
        return String.join("\n", moves);
    }

    /**
     * Builds the algebraic notation for a given move.
     * @param fromRow starting row of the piece
     * @param fromCol starting column of the piece
     * @param toRow destination row of the piece
     * @param toCol destination column of the piece
     * @param piece piece type
     * @param captured piece captured on destination square, if any
     * @param board current board state
     * @param whiteMove true if it's white's move
     * @return algebraic notation string of the move
     */
    private String buildNotation(int fromRow, int fromCol, int toRow, int toCol, String piece, String captured,
                                 String[][] board, boolean whiteMove) {
        String from = "" + (char) ('a' + fromCol) + (8 - fromRow);
        String to = "" + (char) ('a' + toCol) + (8 - toRow);
        String notation;

        boolean isPawn = piece.equalsIgnoreCase("p");
        boolean capture = captured != null;

        if (isPawn) {
            notation = capture ? (char) ('a' + fromCol) + "x" + to : to;
        } else {
            notation = piece.toUpperCase() + (capture ? "x" : "") + to;
        }

        SpecialMoves sm = new SpecialMoves(board, whiteMove, -1, -1);
        if (sm.isKingInCheck(!whiteMove)) {
            notation += "+";
        }

        return notation;
    }

    /**
     * Returns a preview of the algebraic notation for a move without adding it to the log.
     * @param fromRow starting row of the piece
     * @param fromCol starting column of the piece
     * @param toRow destination row of the piece
     * @param toCol destination column of the piece
     * @param piece piece type
     * @param captured piece captured on destination square, if any
     * @param board current board state
     * @param whiteMove true if it's white's move
     * @return algebraic notation string preview of the move
     */
    public String getLastNotationPreview(int fromRow, int fromCol, int toRow, int toCol, String piece, String captured,
                                         String[][] board, boolean whiteMove) {
        return buildNotation(fromRow, fromCol, toRow, toCol, piece, captured, board, whiteMove);
    }

    /**
     * Sets the current move number (used when restoring from history).
     * @param moveNumber the move number to set
     */
    public void setMoveNumber(int moveNumber) {
        this.moveNumber = moveNumber;
    }
    /**
     * Adds a move notation directly to the log.
     * If the notation starts with a move number (e.g., "1. e4"), it creates a new line.
     * Otherwise, appends it to the last move as a black move.
     * @param notation algebraic notation string to add
     */
    public void addNotationDirectly(String notation) {
        if (notation.matches("^\\d+\\.\\s.*")) {
            moves.add(notation);
            moveNumber++;
        } else {
            if (moves.isEmpty()) return;
            String last = moves.remove(moves.size() - 1);
            moves.add(last + " , " + notation);
        }
        updateText();
    }


}
