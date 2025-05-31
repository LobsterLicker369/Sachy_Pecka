import org.junit.jupiter.api.Test;
import java.awt.Point;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class to verify the functionality of SpecialMoves methods,
 * which handle special chess moves and rules.
 */
public class SpecialMovesTests {

    /**
     * Tests that isKingInCheck returns true when the king is in check.
     */
    @Test
    void testIsKingInCheckTrue() {
        String[][] board = new String[8][8];
        board[0][4] = "k";
        board[1][4] = "Q";
        SpecialMoves sm = new SpecialMoves(board, false, -1, -1);
        assertTrue(sm.isKingInCheck(false));
    }

    /**
     * Tests that hasNoLegalMoves returns true in a stalemate situation.
     */
    @Test
    void testHasNoLegalMovesStalemate() {
        String[][] board = new String[8][8];
        board[0][0] = "k";
        board[1][2] = "Q";
        board[2][1] = "Q";
        SpecialMoves sm = new SpecialMoves(board, false, -1, -1);
        assertTrue(sm.hasNoLegalMoves(false));
    }

    /**
     * Tests that a rook cannot move through its own pieces blocking it.
     */
    @Test
    void testRookBlockedBySameColor() {
        String[][] board = new String[8][8];
        board[4][4] = "R"; // Rook on e4
        board[4][6] = "P"; // Own pawn on g4
        SpecialMoves sm = new SpecialMoves(board, true, -1, -1);
        Set<Point> moves = sm.getLegalMoves(4, 4);
        assertFalse(moves.contains(new Point(4, 6))); // g4 should not be allowed
    }

    /**
     * Tests generation of knight moves in an L shape.
     */
    @Test
    void testKnightLShapeMoves() {
        String[][] board = new String[8][8];
        board[4][4] = "N"; // Knight on e4
        board[7][4] = "K"; // White king on e1 (to prevent check logic blocking moves)
        SpecialMoves sm = new SpecialMoves(board, true, -1, -1);
        Set<Point> moves = sm.getLegalMoves(4, 4);
        assertTrue(moves.contains(new Point(2, 5))); // f6
        assertTrue(moves.contains(new Point(3, 6))); // g5
    }

    /**
     * Tests detection of threefold repetition draw rule.
     */
    @Test
    void testThreefoldRepetitionDetection() {
        String[][] board = new String[8][8];
        board[0][0] = "R";
        DrawRules.reset();
        DrawRules.recordPosition(board, true);
        DrawRules.recordPosition(board, true);
        DrawRules.recordPosition(board, true);
        assertTrue(DrawRules.isThreefoldRepetition(board, true));
    }
}
