import java.util.Random;
import java.util.Scanner;

public class GuessTheNumber {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        Random random = new Random();

        int totalScore = 0;
        int round = 1;
        boolean playAgain = true;

        System.out.println("====================================");
        System.out.println("        GUESS THE NUMBER GAME       ");
        System.out.println("====================================");

        while (playAgain) {

            int secretNumber = random.nextInt(100) + 1; // 1 to 100
            int attemptsLeft = 10;
            boolean guessedCorrectly = false;

            System.out.println("\nRound " + round + " Started!");
            System.out.println("I have generated a number between 1 and 100.");
            System.out.println("You have " + attemptsLeft + " attempts to guess it.");

            while (attemptsLeft > 0) {

                System.out.print("\nEnter your guess: ");
                int guess = sc.nextInt();

                attemptsLeft--;

                if (guess == secretNumber) {
                    guessedCorrectly = true;
                    int score = attemptsLeft + 1; // more attempts left = more score
                    totalScore += score;

                    System.out.println("Correct! You guessed the number.");
                    System.out.println("Your score for this round: " + score);
                    break;
                } 
                else if (guess > secretNumber) {
                    System.out.println("Too High!");
                } 
                else {
                    System.out.println("Too Low!");
                }

                System.out.println("Attempts left: " + attemptsLeft);
            }

            if (!guessedCorrectly) {
                System.out.println("\nGame Over! You ran out of attempts.");
                System.out.println("The correct number was: " + secretNumber);
            }

            System.out.println("\nTotal Score: " + totalScore);

            System.out.print("\nDo you want to play another round? (yes/no): ");
            String choice = sc.next();

            if (!choice.equalsIgnoreCase("yes")) {
                playAgain = false;
            } else {
                round++;
            }
        }

        System.out.println("\n====================================");
        System.out.println("Thank you for playing!");
        System.out.println("Final Score: " + totalScore);
        System.out.println("Rounds Played: " + round);
        System.out.println("====================================");

        sc.close();
    }
}
