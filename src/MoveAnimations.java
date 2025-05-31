import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MoveAnimations {
    private static Point lastFrom = null;
    private static Point lastTo = null;
    private static List<Point> checkHighlights = new ArrayList<>();

    public static void saveLastMove(int fromRow, int fromCol, int toRow, int toCol) {
        lastFrom = new Point(fromRow, fromCol);
        lastTo = new Point(toRow, toCol);
    }

    public static void clearLastMove() {
        lastFrom = null;
        lastTo = null;
    }

    public static boolean isLastMoveSquare(int row, int col) {
        return (lastFrom != null && lastFrom.equals(new Point(row, col))) ||
                (lastTo != null && lastTo.equals(new Point(row, col)));
    }

    public static void setCheckHighlight(Point king, Point attacker) {
        checkHighlights.clear();
        if (king != null) checkHighlights.add(king);
        if (attacker != null) checkHighlights.add(attacker);
    }

    public static void clearCheckHighlight() {
        checkHighlights.clear();
    }

    public static boolean isCheckHighlight(int row, int col) {
        return checkHighlights.contains(new Point(row, col));
    }


}
