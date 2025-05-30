import javax.swing.*;
import java.awt.*;

public class GameLog extends JPanel {


    private JTextArea logArea;
    private int moveNumber = 1;

    public GameLog() {
        setLayout(new BorderLayout());
        logArea = new JTextArea(20, 20);
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        add(scrollPane, BorderLayout.CENTER);
    }


    // Prida jeden tah do logu
    public void registerMove(int fromRow, int fromCol, int toRow, int toCol, String piece, String captured,
                             String[][] board, boolean whiteMove) {
        String move = buildNotation(fromRow, fromCol, toRow, toCol, piece, captured, board, whiteMove);

        if (whiteMove) {
            logArea.append(moveNumber + ". " + move + " , ");
        } else {
            logArea.append(move + "\n");
            moveNumber++;
        }
    }

    // Sestavi notaci pro dany tah
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

        // Check detection
        SpecialMoves sm = new SpecialMoves(board, whiteMove, -1, -1);
        if (sm.isKingInCheck(!whiteMove)) {
            notation += "+";
        }

        return notation;
    }

    // Vymaze vsechny tahy a zacne od zacatku
    public void clearLog() {
        logArea.setText("");
        moveNumber = 1;
    }

    public String getAllMoves() {
        return logArea.getText();
    }

}
