import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MainMenu {

    //ukaze tab s menu
    public static void show() {
        String[] options = {"Nová hra", "Zobrazit historii", "Statistiky", "Smazat historii", "Konec"};
        int choice = JOptionPane.showOptionDialog(
                null,
                "Šachy",
                "Hlavní menu",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]);

        switch (choice) {
            case 0 -> startGame();
            case 1 -> showHistory();
            case 2 -> showStatistics();
            case 3 -> deleteHistory();
            default -> System.exit(0);
        }
    }

    //zapne hru
    private static void startGame() {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Chess Game");

            GameLog gameLog = new GameLog();
            ChessBoardPanel chessBoard = new ChessBoardPanel(gameLog);
            Timer timer = new Timer();

            JPanel panel = new JPanel(new BorderLayout());
            panel.add(chessBoard, BorderLayout.CENTER);

            JPanel rightPanel = new JPanel(new BorderLayout());
            rightPanel.add(gameLog, BorderLayout.CENTER);
            rightPanel.add(timer, BorderLayout.SOUTH);

            panel.add(rightPanel, BorderLayout.EAST);

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(panel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            frame.setResizable(false);
        });
    }


    private static void showHistory() {
        try {
            String content = Files.readString(Paths.get("history.txt"));
            JTextArea textArea = new JTextArea(content);
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);

            JOptionPane.showMessageDialog(null, scrollPane, "Historie her", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Soubor s historií nenalezen.", "Chyba", JOptionPane.ERROR_MESSAGE);
        }
        show(); // návrat do menu
    }


    private static void showStatistics() {
        try {
            String content = Files.readString(Paths.get("history.txt"));
            String[] games = content.split("=== Game ");

            int totalGames = 0, whiteWins = 0, blackWins = 0, draws = 0;
            long totalMillis = 0;

            for (String game : games) {
                if (game.trim().isEmpty()) continue;
                totalGames++;

                if (game.contains("Result: White wins")) whiteWins++;
                else if (game.contains("Result: Black wins")) blackWins++;
                else if (game.contains("Result: Draw")) draws++;

                int i = game.indexOf("Duration: ");
                if (i != -1) {
                    String duration = game.substring(i + 10, i + 22); // 00:00:00:000
                    totalMillis += parseTime(duration);
                }
            }

            String msg = "📊 Statistiky her:\n"
                    + "Celkem her: " + totalGames + "\n"
                    + "Bílé výhry: " + whiteWins + "\n"
                    + "Černé výhry: " + blackWins + "\n"
                    + "Remízy: " + draws + "\n"
                    + "Celkový čas hraní: " + formatMillis(totalMillis);

            JOptionPane.showMessageDialog(null, msg, "Statistiky", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Soubor s historií nenalezen.", "Chyba", JOptionPane.ERROR_MESSAGE);
        }
        show(); // návrat do menu
    }


    private static void deleteHistory() {
        int confirm = JOptionPane.showConfirmDialog(null,
                "Opravdu chceš vymazat celou historii her?",
                "Potvrzení",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Files.deleteIfExists(Paths.get("history.txt"));
                JOptionPane.showMessageDialog(null, "Historie vymazána.");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Chyba při mazání souboru.");
            }
        }
        show(); // zpatky do menu
    }


    private static long parseTime(String text) {
        String[] p = text.split(":");
        if (p.length != 4) return 0;
        long h = Long.parseLong(p[0]);
        long m = Long.parseLong(p[1]);
        long s = Long.parseLong(p[2]);
        long ms = Long.parseLong(p[3]);
        return h * 3_600_000 + m * 60_000 + s * 1_000 + ms;
    }

    private static String formatMillis(long ms) {
        long h = ms / 3_600_000;
        long m = (ms / 60_000) % 60;
        long s = (ms / 1_000) % 60;
        return String.format("%02d:%02d:%02d", h, m, s);
    }
}
