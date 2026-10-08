import java.util.Random;

/**
 * Randomly selects Rock, Paper, or Scissors
 * for the computer's move.
 */
public class RandomStrategy implements Strategy {

    private final Random random = new Random();

    @Override
    public String getMove(String playerMove) {

        int choice = random.nextInt(3);

        if (choice == 0) {
            return "R";
        } else if (choice == 1) {
            return "P";
        } else {
            return "S";
        }
    }
}