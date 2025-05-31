/**
 * Entry point of the chess application.
 * Initializes the game and opens the main menu.
 */
public class Main {

    /**
     * Launches the application by resetting game state and displaying the main menu.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        DrawRules.reset();
        MoveAnimations.clearLastMove();
        MoveAnimations.clearCheckHighlight();

        MainMenu.show();
    }
}
