import javax.swing.*;
import java.awt.*;

public class ChessBoardPanel extends JPanel {
    private final int rows = 9;
    private final int cols = 9;
    private final Color lightColor = new Color(240, 217, 181);
    private final Color darkColor = new Color(181, 136, 99);

    public ChessBoardPanel() {
        setLayout(new GridLayout(rows, cols));
        drawBoard();
    }

    //♔ ♕ ♖  ♗ ♘ ♙ – White
    //
    //♚ ♛ ♜ ♝ ♞ ♟ – Black
    private void drawBoard() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (row == 8 && col == 0) {
                    add(new JLabel("")); // empty corner
                } else if (row == 8) {
                    // bottom coords
                    char label = (char) ('a' + col - 1);
                    add(createCoordLabel(String.valueOf(label)));
                } else if (col == 0) {
                    // left coords
                    int label = 8 - row;
                    add(createCoordLabel(String.valueOf(label)));
                } else {
                    // board square
                    JPanel square = new JPanel(new BorderLayout());
                    boolean isLight = (row + col) % 2 == 0;
                    square.setBackground(isLight ? darkColor : lightColor);


                    JLabel pieceLabel = new JLabel("", SwingConstants.CENTER);
                    pieceLabel.setFont(new Font("SansSerif", Font.PLAIN, 32));




                    int num = 1;

                    for (num = 0; num < 9; num++) {
                        if (row == 6 && col == num) {
                            pieceLabel.setText("♙");

                        }
                    }

                    for (num = 0; num < 9; num++) {
                        if (row == 1 && col == num) {
                            pieceLabel.setText("♟");

                        }
                    }
                    if (row == 0 && col == 4) {
                        pieceLabel.setText("♛");
                    }
                    if (row == 0 && col == 5) {
                        pieceLabel.setText("♚");
                    }


                    if (row == 0 && col == 2) {
                        pieceLabel.setText("♞");
                    }

                    if (row == 0 && col == 7) {
                        pieceLabel.setText("♞");
                    }


                    if (row == 7 && col == 2) {
                        pieceLabel.setText("♘");
                    }

                    if (row == 7 && col == 7) {
                        pieceLabel.setText("♘");
                    }


                    if (row == 7 && col == 4) {
                        pieceLabel.setText("♕");
                    }
                    if (row == 7 && col == 5) {
                        pieceLabel.setText("♔");
                    }


                    if (row == 0 && col == 3) {
                        pieceLabel.setText("♝");
                    }
                    if (row == 0 && col == 6) {
                        pieceLabel.setText("♝");
                    }
                    if (row == 7 && col == 3) {
                        pieceLabel.setText("♗");
                    }
                    if (row == 7 && col == 6) {
                        pieceLabel.setText("♗");
                    }


                    if (row == 0 && col == 1) {
                        pieceLabel.setText("♜");
                    }
                    if (row == 7 && col == 1) {
                        pieceLabel.setText("♖");
                    }
                    if (row == 0 && col == 8) {
                        pieceLabel.setText("♜");
                    }
                    if (row == 7 && col == 8) {
                        pieceLabel.setText("♖");
                    }

                    square.add(pieceLabel);
                    add(square);
                }
            }
        }
    }


    private JLabel createCoordLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 16));
        return label;
    }
}
