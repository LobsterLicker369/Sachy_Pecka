import javax.swing.*;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SaveGame {

    private static final String HISTORY_FILE = "history.txt";
    //savene staty hry
    public static void saveToHistory(String result, GameLog gameLog) {
        try (FileWriter writer = new FileWriter(HISTORY_FILE, true)) {
            writer.write("=== Game " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " ===\n");
            writer.write("Result: " + result + "\n");
            writer.write("Duration: " + Timer.getFormattedTime() + "\n");
            writer.write("Moves:\n" + gameLog.getAllMoves() + "\n\n");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to save game history.");
        }
    }
    //ukaze staty hry
    public static void showStatsAfterGame() {
        int whiteWins = 0, blackWins = 0, draws = 0;

        try {
            String content = Files.readString(Paths.get(HISTORY_FILE));
            String[] games = content.split("=== Game ");

            for (String game : games) {
                if (game.contains("Result: White wins")) whiteWins++;
                else if (game.contains("Result: Black wins")) blackWins++;
                else if (game.contains("Result: Draw")) draws++;
            }

            String message = "📊 Celkové skóre:\n"
                    + "Bílé výhry: " + whiteWins + "\n"
                    + "Černé výhry: " + blackWins + "\n"
                    + "Remízy: " + draws;

            JOptionPane.showMessageDialog(null, message, "Skóre", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Nelze načíst historii pro statistiky.");
        }
    }
}
