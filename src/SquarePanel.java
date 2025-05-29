import javax.swing.*;
import java.awt.*;

public class SquarePanel extends JPanel {
    private final int row;
    private final int col;
    private boolean isLegalMove = false;

    public SquarePanel(int row, int col, ChessBoardPanel parent) {
        this.row = row;
        this.col = col;
        setPreferredSize(new Dimension(60, 60));
    }

    public void setLegalMove(boolean isLegal) {
        this.isLegalMove = isLegal;
        repaint();
    }


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
