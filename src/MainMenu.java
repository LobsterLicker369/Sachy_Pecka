import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Main menu for the chess application.
 * Provides options to start a new game, view history, view statistics,
 * delete history, or exit the application.
 */
public class MainMenu {

    /**
     * Displays the main menu dialog with options.
     */
    public static void show() {
        String[] options = {"New Game", "Show History", "Statistics", "Delete History", "Exit"};
        int choice = JOptionPane.showOptionDialog(
                null,
                "Chess",
                "Main Menu",
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

    /**
     * Determines if it's White's turn to move based on the game's history index.
     * @param controller the current game controller
     * @return true if it's White's turn, false otherwise
     */
    private static boolean getWhiteToMoveFromBoard(GameController controller) {
        return controller != null && controller.getHistoryIndex() % 2 == 0;
    }

    /**
     * Starts a new game by initializing UI components and game logic.
     */
    private static void startGame() {
        DrawRules.reset();
        MoveAnimations.clearLastMove();
        MoveAnimations.clearCheckHighlight();

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Chess Game");

            // Status label at the top
            JLabel statusLabel = new JLabel("White to move", SwingConstants.CENTER);
            statusLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
            statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

            GameLog gameLog = new GameLog();
            ChessBoardPanel chessBoard = new ChessBoardPanel(gameLog);
            Timer timer = new Timer();

            final GameController[] controller = new GameController[1];
            controller[0] = new GameController(chessBoard);

            // Swing Timer to update the status label regularly
            new javax.swing.Timer(300, e -> {
                String[][] board = chessBoard.getBoard();
                boolean whiteToMove = getWhiteToMoveFromBoard(controller[0]);
                SpecialMoves sm = new SpecialMoves(board, whiteToMove, -1, -1);
                boolean check = sm.isKingInCheck(whiteToMove);

                if (DrawRules.isThreefoldRepetition(board, whiteToMove)) {
                    statusLabel.setText("Threefold repetition - Draw");

                    statusLabel.setText("Threefold repetition - Draw");
                } else if (DrawRules.isFiftyMoveRuleDraw()) {
                    statusLabel.setText("Fifty-move rule - Draw");
                } else if (check) {
                    statusLabel.setText((whiteToMove ? "White" : "Black") + " is in check");
                } else {
                    statusLabel.setText(whiteToMove ? "White to move" : "Black to move");
                }
            }).start();

            JButton drawButton = new JButton("Draw");
            JButton undoButton = new JButton("Undo");
            JButton redoButton = new JButton("Redo");

            undoButton.addActionListener(e -> controller[0].undo());
            redoButton.addActionListener(e -> controller[0].redo());
            drawButton.addActionListener(e -> {
                int option = JOptionPane.showConfirmDialog(null, "Do you agree to a draw?", "Draw", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    Timer.stopStatic();
                    SaveGame.saveToHistory("Draw", gameLog);
                    JOptionPane.showMessageDialog(null, "The game ended in a draw.");
                    SaveGame.showStatsAfterGame();
                    frame.dispose();
                    MainMenu.show();
                }
                MoveAnimations.clearLastMove();
                MoveAnimations.clearCheckHighlight();
            });

            JPanel bottomPanel = new JPanel();
            bottomPanel.add(drawButton);
            bottomPanel.add(undoButton);
            bottomPanel.add(redoButton);

            JPanel panel = new JPanel(new BorderLayout());
            panel.add(statusLabel, BorderLayout.NORTH);
            panel.add(chessBoard, BorderLayout.CENTER);

            JPanel rightPanel = new JPanel(new BorderLayout());
            rightPanel.add(gameLog, BorderLayout.CENTER);
            rightPanel.add(timer, BorderLayout.SOUTH);

            panel.add(rightPanel, BorderLayout.EAST);
            panel.add(bottomPanel, BorderLayout.SOUTH);

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(panel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            frame.setResizable(false);
        });
    }

    /**
     * Shows the content of the game history file in a scrollable dialog.
     */
    private static void showHistory() {
        try {
            String content = Files.readString(Paths.get("history.txt"));
            JTextArea textArea = new JTextArea(content);
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);

            JOptionPane.showMessageDialog(null, scrollPane, "Game History", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "History file not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        show(); // Return to menu
    }

    /**
     * Calculates and displays overall game statistics from the history file.
     */
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
                    String duration = game.substring(i + 10, i + 22); // format 00:00:00:000
                    totalMillis += parseTime(duration);
                }
            }

            String msg = "📊 Game Statistics:\n"
                    + "Total games: " + totalGames + "\n"
                    + "White wins: " + whiteWins + "\n"
                    + "Black wins: " + blackWins + "\n"
                    + "Draws: " + draws + "\n"
                    + "Total play time: " + formatMillis(totalMillis);

            JOptionPane.showMessageDialog(null, msg, "Statistics", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "History file not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        show(); // Return to menu
    }

    /**
     * Deletes the history file after user confirmation.
     */
    private static void deleteHistory() {
        int confirm = JOptionPane.showConfirmDialog(null,
                "Are you sure you want to delete the entire game history?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Files.deleteIfExists(Paths.get("history.txt"));
                JOptionPane.showMessageDialog(null, "History deleted.");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error deleting the file.");
            }
        }
        show(); // Back to menu
    }

    /**
     * Parses a duration string formatted as "HH:mm:ss:SSS" into milliseconds.
     * @param text the duration string
     * @return the duration in milliseconds
     */
    private static long parseTime(String text) {
        String[] p = text.split(":");
        if (p.length != 4) return 0;
        long h = Long.parseLong(p[0]);
        long m = Long.parseLong(p[1]);
        long s = Long.parseLong(p[2]);
        long ms = Long.parseLong(p[3]);
        return h * 3_600_000 + m * 60_000 + s * 1_000 + ms;
    }

    /**
     * Formats milliseconds into a string formatted as "HH:mm:ss".
     * @param ms milliseconds to format
     * @return formatted time string
     */
    private static String formatMillis(long ms) {
        long h = ms / 3_600_000;
        long m = (ms / 60_000) % 60;
        long s = (ms / 1_000) % 60;
        return String.format("%02d:%02d:%02d", h, m, s);
    }

}
