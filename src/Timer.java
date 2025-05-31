import javax.swing.*;
import java.awt.*;

/**
 * A panel that displays an elapsed timer using Swing.
 * The timer starts immediately upon construction and updates every 50 milliseconds.
 */
public class Timer extends JPanel {

    // Label for displaying the time
    private JLabel timeLabel;

    // The time when the timer started
    private long startTime;

    // A Swing timer that updates periodically
    private javax.swing.Timer swingTimer;

    private static Timer instance;

    /**
     * Constructs the Timer panel and starts the timer.
     */
    public Timer() {
        // Layout of the panel
        setLayout(new BorderLayout());

        // Create and configure the text label
        timeLabel = new JLabel("00:00:00:000", SwingConstants.CENTER);
        timeLabel.setFont(new Font("Font", Font.BOLD, 20));
        add(timeLabel, BorderLayout.CENTER);
        setPreferredSize(new Dimension(150, 50));

        // Set the start time and launch updates every 50 ms
        startTime = System.currentTimeMillis();
        swingTimer = new javax.swing.Timer(50, e -> updateTime());
        swingTimer.start();
        instance = this;
    }

    /**
     * Calculates and displays the current elapsed time.
     */
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

    /**
     * Stops the timer.
     */
    public void stop() {
        swingTimer.stop();
    }

    /**
     * Static method to stop the current timer instance.
     */
    public static void stopStatic() {
        if (instance != null) {
            instance.stop();
        }
    }

    /**
     * Resets the timer to the current system time.
     */
    public void reset() {
        startTime = System.currentTimeMillis();
    }

    /**
     * Returns the formatted elapsed time since static start time.
     * @return A string in format HH:mm:ss:SSS
     */
    public static String getFormattedTime() {
        long now = System.currentTimeMillis();
        long elapsed = now - startTimeStatic;

        long hours = elapsed / (1000 * 60 * 60);
        long minutes = (elapsed / (1000 * 60)) % 60;
        long seconds = (elapsed / 1000) % 60;
        long millis = elapsed % 1000;

        return String.format("%02d:%02d:%02d:%03d", hours, minutes, seconds, millis);
    }

    // Static start time for use with getFormattedTime
    private static long startTimeStatic = System.currentTimeMillis();
}
