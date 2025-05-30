
import javax.swing.*;
import java.awt.*;

public class Timer extends JPanel {

    // Popisek pro zobrazovani casu
    private JLabel timeLabel;

    // Cas odkdy bezi timer
    private long startTime;

    // Swingovy timer ktery se spousti pravidelne
    private javax.swing.Timer swingTimer;

    private static Timer instance;

    public Timer() {
        // Rozlozeni panelu
        setLayout(new BorderLayout());

        // Vytvori a nastavi textovy label
        timeLabel = new JLabel("00:00:00:000", SwingConstants.CENTER);
        timeLabel.setFont(new Font("Font", Font.BOLD, 20));
        add(timeLabel, BorderLayout.CENTER);
        setPreferredSize(new Dimension(150, 50));

        // Nastavi cas startu a spusti aktualizace kazdych 50 ms
        startTime = System.currentTimeMillis();
        swingTimer = new javax.swing.Timer(50, e -> updateTime());
        swingTimer.start();
        instance = this;

    }


    // Vypocita a zobrazi aktualni cas
    private void updateTime() {
        long now = System.currentTimeMillis();
        long elapsed = now - startTime;

        long hours = elapsed / (1000 * 60 * 60);
        long minutes = (elapsed / (1000 * 60)) % 60;
        long seconds = (elapsed / 1000) % 60;
        long millis = elapsed % 1000;

        String text = String.format("%02d:%02d:%02d:%03d", hours, minutes, seconds, millis);
        timeLabel.setText(text);
    }

    // Zastavi timer
    public void stop() {
        swingTimer.stop();
    }

    public static void stopStatic() {
        if (instance != null) {
            instance.stop();
        }
    }

    // Restartuje timer
    public void reset() {
        startTime = System.currentTimeMillis();
    }

    public static String getFormattedTime() {
        long now = System.currentTimeMillis();
        long elapsed = now - startTimeStatic;

        long hours = elapsed / (1000 * 60 * 60);
        long minutes = (elapsed / (1000 * 60)) % 60;
        long seconds = (elapsed / 1000) % 60;
        long millis = elapsed % 1000;

        return String.format("%02d:%02d:%02d:%03d", hours, minutes, seconds, millis);
    }

    private static long startTimeStatic = System.currentTimeMillis();

}

