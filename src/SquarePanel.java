import javax.swing.*;
import java.awt.*;

/**
 * Represents a single square on the chessboard.
 */
public class SquarePanel extends JPanel {
    private final int row;
    private final int col;
    private boolean isLegalMove = false;

    /**
     * Constructs a SquarePanel at the given row and column.
     *
     * @param row    the row of this square
     * @param col    the column of this square
     * @param parent reference to the ChessBoardPanel (not used directly here)
     */
    public SquarePanel(int row, int col, ChessBoardPanel parent) {
        this.row = row;
        this.col = col;
        setPreferredSize(new Dimension(60, 60));
    }

    /**
     * Sets whether this square is a legal move destination.
     *
     * @param isLegal true if this square is a legal move, false otherwise
     */
    public void setLegalMove(boolean isLegal) {
        this.isLegalMove = isLegal;
        repaint();
    }

    /**
     * Paints the square. If the square is marked as a legal move,
     * draws a green circle to indicate it.
     *
     * @param g the Graphics object for drawing
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (isLegalMove) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(new Color(50, 200, 50, 128));
            int diameter = Math.min(getWidth(), getHeight()) / 3;
            int x = (getWidth() - diameter) / 2;
            int y = (getHeight() - diameter) / 2;
            g2.fillOval(x, y, diameter, diameter);
        }
    }
}
