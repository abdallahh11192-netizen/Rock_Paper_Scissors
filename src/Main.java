import javax.swing.SwingUtilities;

/**
 * Starts the Rock Paper Scissors game.
 */
public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(
                RockPaperScissorsFrame::new
        );
    }
}