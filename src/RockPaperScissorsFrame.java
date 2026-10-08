import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Random;

/**
 * Rock Paper Scissors game GUI.
 * The computer uses several strategies to choose its move.
 */
public class RockPaperScissorsFrame extends JFrame {

    private JButton rockButton;
    private JButton paperButton;
    private JButton scissorsButton;
    private JButton quitButton;

    private JTextField playerWinsField;
    private JTextField computerWinsField;
    private JTextField tiesField;

    private JTextArea resultsArea;

    private int playerWins = 0;
    private int computerWins = 0;
    private int ties = 0;

    private int rockCount = 0;
    private int paperCount = 0;
    private int scissorsCount = 0;

    private String lastPlayerMove = "";

    private final Strategy cheatStrategy = new Cheat();
    private final Strategy randomStrategy = new RandomStrategy();

    private final Strategy leastUsedStrategy = new LeastUsed();
    private final Strategy mostUsedStrategy = new MostUsed();
    private final Strategy lastUsedStrategy = new LastUsed();

    private final Random random = new Random();

    /**
     * Creates the Rock Paper Scissors window.
     */
    public RockPaperScissorsFrame() {

        setTitle("Rock Paper Scissors Game");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        createButtonPanel();
        createStatsPanel();
        createResultsPanel();
        addListeners();

        setVisible(true);
    }

    /**
     * Creates and resizes an ImageIcon.
     *
     * @param fileName image file name
     * @return resized ImageIcon
     */
    private ImageIcon createIcon(String fileName) {

        URL imageURL = getClass().getResource("/" + fileName);

        if (imageURL == null) {
            System.out.println("Image not found: " + fileName);
            return new ImageIcon();
        }

        ImageIcon originalIcon = new ImageIcon(imageURL);

        Image scaledImage = originalIcon.getImage().getScaledInstance(
                70,
                70,
                Image.SCALE_SMOOTH
        );

        return new ImageIcon(scaledImage);
    }

    /**
     * Creates the game buttons.
     */
    private void createButtonPanel() {

        JPanel buttonPanel =
                new JPanel(new GridLayout(1, 4, 10, 10));

        ImageIcon rockIcon = createIcon("rock.png");
        ImageIcon paperIcon = createIcon("paper.png");
        ImageIcon scissorsIcon = createIcon("scissors.png");

        rockButton = new JButton("Rock", rockIcon);
        paperButton = new JButton("Paper", paperIcon);
        scissorsButton = new JButton("Scissors", scissorsIcon);
        quitButton = new JButton("Quit");

        rockButton.setHorizontalTextPosition(SwingConstants.CENTER);
        rockButton.setVerticalTextPosition(SwingConstants.BOTTOM);

        paperButton.setHorizontalTextPosition(SwingConstants.CENTER);
        paperButton.setVerticalTextPosition(SwingConstants.BOTTOM);

        scissorsButton.setHorizontalTextPosition(SwingConstants.CENTER);
        scissorsButton.setVerticalTextPosition(SwingConstants.BOTTOM);

        buttonPanel.add(rockButton);
        buttonPanel.add(paperButton);
        buttonPanel.add(scissorsButton);
        buttonPanel.add(quitButton);

        buttonPanel.setBorder(
                BorderFactory.createTitledBorder("Choose Your Move")
        );

        add(buttonPanel, BorderLayout.NORTH);
    }

    /**
     * Creates the statistics panel.
     */
    private void createStatsPanel() {

        JPanel statsPanel =
                new JPanel(new GridLayout(3, 2, 5, 5));

        statsPanel.add(new JLabel("Player Wins:"));

        playerWinsField = new JTextField("0");
        playerWinsField.setEditable(false);
        statsPanel.add(playerWinsField);

        statsPanel.add(new JLabel("Computer Wins:"));

        computerWinsField = new JTextField("0");
        computerWinsField.setEditable(false);
        statsPanel.add(computerWinsField);

        statsPanel.add(new JLabel("Ties:"));

        tiesField = new JTextField("0");
        tiesField.setEditable(false);
        statsPanel.add(tiesField);

        statsPanel.setBorder(
                BorderFactory.createTitledBorder("Game Statistics")
        );

        add(statsPanel, BorderLayout.WEST);
    }

    /**
     * Creates the game results area.
     */
    private void createResultsPanel() {

        resultsArea = new JTextArea();

        resultsArea.setEditable(false);
        resultsArea.setLineWrap(true);
        resultsArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(resultsArea);

        scrollPane.setBorder(
                BorderFactory.createTitledBorder("Game Results")
        );

        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Adds one ActionListener to the three game buttons.
     */
    private void addListeners() {

        ActionListener gameListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                String playerMove;

                if (e.getSource() == rockButton) {

                    playerMove = "R";

                } else if (e.getSource() == paperButton) {

                    playerMove = "P";

                } else {

                    playerMove = "S";
                }

                playGame(playerMove);
            }
        };

        rockButton.addActionListener(gameListener);
        paperButton.addActionListener(gameListener);
        scissorsButton.addActionListener(gameListener);

        quitButton.addActionListener(e -> System.exit(0));
    }

    /**
     * Plays one round.
     *
     * @param playerMove player's selected move
     */
    private void playGame(String playerMove) {

        String previousMove = lastPlayerMove;

        int probability = random.nextInt(100) + 1;

        Strategy selectedStrategy;
        String strategyName;

        if (probability <= 10) {

            selectedStrategy = cheatStrategy;
            strategyName = "Cheat";

        } else if (probability <= 30) {

            selectedStrategy = leastUsedStrategy;
            strategyName = "Least Used";

        } else if (probability <= 50) {

            selectedStrategy = mostUsedStrategy;
            strategyName = "Most Used";

        } else if (probability <= 70 && !previousMove.isEmpty()) {

            selectedStrategy = lastUsedStrategy;
            strategyName = "Last Used";

        } else {

            selectedStrategy = randomStrategy;
            strategyName = "Random";
        }

        String computerMove;

        if (selectedStrategy == lastUsedStrategy) {

            computerMove =
                    ((LastUsed) lastUsedStrategy).getMove(previousMove);

        } else {

            computerMove =
                    selectedStrategy.getMove(playerMove);
        }

        String result =
                determineWinner(playerMove, computerMove);

        resultsArea.append(
                result
                        + " (Computer: "
                        + strategyName
                        + ")\n"
        );

        resultsArea.setCaretPosition(
                resultsArea.getDocument().getLength()
        );

        updatePlayerHistory(playerMove);
        updateStats();
    }

    /**
     * Determines the winner.
     */
    private String determineWinner(
            String playerMove,
            String computerMove) {

        if (playerMove.equals(computerMove)) {

            ties++;

            return moveName(playerMove)
                    + " ties with "
                    + moveName(computerMove)
                    + ". (Tie!)";
        }

        if (playerMove.equals("R")
                && computerMove.equals("S")) {

            playerWins++;

            return "Rock breaks scissors. (Player wins!)";
        }

        if (playerMove.equals("P")
                && computerMove.equals("R")) {

            playerWins++;

            return "Paper covers rock. (Player wins!)";
        }

        if (playerMove.equals("S")
                && computerMove.equals("P")) {

            playerWins++;

            return "Scissors cuts paper. (Player wins!)";
        }

        computerWins++;

        if (computerMove.equals("R")
                && playerMove.equals("S")) {

            return "Rock breaks scissors. (Computer wins!)";
        }

        if (computerMove.equals("P")
                && playerMove.equals("R")) {

            return "Paper covers rock. (Computer wins!)";
        }

        return "Scissors cuts paper. (Computer wins!)";
    }

    /**
     * Updates the player's move history.
     */
    private void updatePlayerHistory(String playerMove) {

        if (playerMove.equals("R")) {

            rockCount++;

        } else if (playerMove.equals("P")) {

            paperCount++;

        } else if (playerMove.equals("S")) {

            scissorsCount++;
        }

        lastPlayerMove = playerMove;
    }

    /**
     * Updates the score display.
     */
    private void updateStats() {

        playerWinsField.setText(
                String.valueOf(playerWins)
        );

        computerWinsField.setText(
                String.valueOf(computerWins)
        );

        tiesField.setText(
                String.valueOf(ties)
        );
    }

    /**
     * Converts a move code to its name.
     */
    private String moveName(String move) {

        switch (move) {

            case "R":
                return "Rock";

            case "P":
                return "Paper";

            case "S":
                return "Scissors";

            default:
                return "Unknown";
        }
    }

    /**
     * Returns the move that beats the supplied move.
     */
    private String moveThatBeats(String move) {

        switch (move) {

            case "R":
                return "P";

            case "P":
                return "S";

            case "S":
                return "R";

            default:
                return randomStrategy.getMove("");
        }
    }

    /**
     * Least Used strategy.
     */
    private class LeastUsed implements Strategy {

        @Override
        public String getMove(String playerMove) {

            int minimum =
                    Math.min(
                            rockCount,
                            Math.min(paperCount, scissorsCount)
                    );

            String predictedMove;

            if (rockCount == minimum) {

                predictedMove = "R";

            } else if (paperCount == minimum) {

                predictedMove = "P";

            } else {

                predictedMove = "S";
            }

            return moveThatBeats(predictedMove);
        }
    }

    /**
     * Most Used strategy.
     */
    private class MostUsed implements Strategy {

        @Override
        public String getMove(String playerMove) {

            int maximum =
                    Math.max(
                            rockCount,
                            Math.max(paperCount, scissorsCount)
                    );

            String predictedMove;

            if (rockCount == maximum) {

                predictedMove = "R";

            } else if (paperCount == maximum) {

                predictedMove = "P";

            } else {

                predictedMove = "S";
            }

            return moveThatBeats(predictedMove);
        }
    }

    /**
     * Last Used strategy.
     */
    private class LastUsed implements Strategy {

        @Override
        public String getMove(String playerMove) {

            if (playerMove == null || playerMove.isEmpty()) {

                return randomStrategy.getMove("");
            }

            return playerMove;
        }
    }
}