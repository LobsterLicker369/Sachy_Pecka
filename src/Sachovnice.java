import javax.swing.*;
import java.awt.*;

public class Sachovnice {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Chess Game");

            // Vytvori sachovnici a log
            GameLog gameLog = new GameLog();
            ChessBoardPanel chessBoard = new ChessBoardPanel(gameLog);

            // Rozvrzeni vedle sebe
            JPanel panel = new JPanel(new BorderLayout());
            panel.add(chessBoard, BorderLayout.CENTER);
            panel.add(gameLog, BorderLayout.EAST);

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(panel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            frame.setResizable(false);
        });
    }
}