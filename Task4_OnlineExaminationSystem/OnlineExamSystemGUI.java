import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class OnlineExamSystemGUI extends JFrame {

    // Demo user details (can be replaced with database later)
    private String username = "rama";
    private String password = "1234";
    private String name = "Rama Krishna";

    // Exam data
    private final ArrayList<Question> questions = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private int score = 0;

    // GUI components
    private JLabel timerLabel;
    private JLabel questionLabel;
    private JRadioButton optionA, optionB, optionC, optionD;
    private ButtonGroup optionsGroup;
    private JButton nextButton;

    // Timer settings
    private javax.swing.Timer timer;
    private int timeLeft = 30; // seconds

    public OnlineExamSystemGUI() {

        setTitle("Online Examination System");
        setSize(650, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        loadQuestions();
        showLoginDialog();
    }

    // Load MCQ questions
    private void loadQuestions() {

        questions.add(new Question(
                "Which keyword is used to create an object in Java?",
                "class", "new", "this", "static", 'B'));

        questions.add(new Question(
                "Which data structure follows FIFO?",
                "Stack", "Queue", "Tree", "Graph", 'B'));

        questions.add(new Question(
                "Which method is the entry point of Java program?",
                "run()", "start()", "main()", "init()", 'C'));

        questions.add(new Question(
                "Which is not an OOP concept?",
                "Encapsulation", "Inheritance", "Compilation", "Polymorphism", 'C'));

        questions.add(new Question(
                "Which exception occurs when dividing by zero?",
                "IOException", "NullPointerException", "ArithmeticException", "ArrayIndexOutOfBoundsException", 'C'));
    }

    // Login using JOptionPane dialog box
    private void showLoginDialog() {

        int attempts = 3;

        while (attempts > 0) {

            JTextField userField = new JTextField();
            JPasswordField passField = new JPasswordField();

            Object[] message = {
                    "Username:", userField,
                    "Password:", passField
            };

            int option = JOptionPane.showConfirmDialog(
                    null,
                    message,
                    "Login",
                    JOptionPane.OK_CANCEL_OPTION
            );

            if (option == JOptionPane.CANCEL_OPTION) {
                JOptionPane.showMessageDialog(null, "Login cancelled. Exiting application.");
                System.exit(0);
            }

            String enteredUsername = userField.getText().trim();
            String enteredPassword = new String(passField.getPassword()).trim();

            if (enteredUsername.equals(username) && enteredPassword.equals(password)) {
                JOptionPane.showMessageDialog(null, "Login Successful! Welcome " + name);
                showMainMenu();
                return;
            } else {
                attempts--;
                JOptionPane.showMessageDialog(null, "Invalid credentials! Attempts left: " + attempts);
            }
        }

        JOptionPane.showMessageDialog(null, "Account locked due to multiple failed attempts.");
        System.exit(0);
    }

    // Main menu options
    private void showMainMenu() {

        String[] options = {"Start Exam", "Update Profile", "Update Password", "Logout"};

        while (true) {

            int choice = JOptionPane.showOptionDialog(
                    null,
                    "Welcome " + name + "\nChoose an option:",
                    "Main Menu",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if (choice == 0) {
                startExamGUI();
                break;
            } else if (choice == 1) {
                updateProfile();
            } else if (choice == 2) {
                updatePassword();
            } else {
                JOptionPane.showMessageDialog(null, "Logged out successfully. Session closed.");
                System.exit(0);
            }
        }
    }

    // Update user name/profile
    private void updateProfile() {

        String newName = JOptionPane.showInputDialog(null, "Enter new name:", name);

        if (newName != null && !newName.trim().isEmpty()) {
            name = newName.trim();
            JOptionPane.showMessageDialog(null, "Profile updated successfully.\nNew Name: " + name);
        } else {
            JOptionPane.showMessageDialog(null, "Invalid name. Profile not updated.");
        }
    }

    // Update password feature
    private void updatePassword() {

        String oldPass = JOptionPane.showInputDialog(null, "Enter old password:");

        if (oldPass == null) return;

        if (!oldPass.equals(password)) {
            JOptionPane.showMessageDialog(null, "Incorrect old password.");
            return;
        }

        String newPass = JOptionPane.showInputDialog(null, "Enter new password:");

        if (newPass != null && !newPass.trim().isEmpty()) {
            password = newPass.trim();
            JOptionPane.showMessageDialog(null, "Password updated successfully.");
        } else {
            JOptionPane.showMessageDialog(null, "Invalid password. Update failed.");
        }
    }

    // Start the exam screen
    private void startExamGUI() {

        getContentPane().removeAll();
        setLayout(new BorderLayout());

        // Timer label
        timerLabel = new JLabel("Time Left: " + timeLeft + " seconds", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(timerLabel, BorderLayout.NORTH);

        // Question Panel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(6, 1));

        questionLabel = new JLabel();
        questionLabel.setFont(new Font("Arial", Font.BOLD, 14));

        optionA = new JRadioButton();
        optionB = new JRadioButton();
        optionC = new JRadioButton();
        optionD = new JRadioButton();

        optionsGroup = new ButtonGroup();
        optionsGroup.add(optionA);
        optionsGroup.add(optionB);
        optionsGroup.add(optionC);
        optionsGroup.add(optionD);

        nextButton = new JButton("Next");

        centerPanel.add(questionLabel);
        centerPanel.add(optionA);
        centerPanel.add(optionB);
        centerPanel.add(optionC);
        centerPanel.add(optionD);
        centerPanel.add(nextButton);

        add(centerPanel, BorderLayout.CENTER);

        // Load first question
        loadQuestion();

        // Timer logic (auto submit)
        timer = new javax.swing.Timer(1000, e -> {
            timeLeft--;
            timerLabel.setText("Time Left: " + timeLeft + " seconds");

            if (timeLeft <= 0) {
                timer.stop();
                JOptionPane.showMessageDialog(null, "Time Over! Exam auto-submitted.");
                showResult();
            }
        });

        timer.start();

        // Next button action
        nextButton.addActionListener(e -> {

            checkAnswer();
            currentQuestionIndex++;

            if (currentQuestionIndex < questions.size()) {
                loadQuestion();
            } else {
                timer.stop();
                showResult();
            }
        });

        revalidate();
        repaint();
        setVisible(true);
    }

    // Load question into GUI
    private void loadQuestion() {

        optionsGroup.clearSelection();

        Question q = questions.get(currentQuestionIndex);

        questionLabel.setText((currentQuestionIndex + 1) + ". " + q.getQuestion());

        optionA.setText("A) " + q.getOptionA());
        optionB.setText("B) " + q.getOptionB());
        optionC.setText("C) " + q.getOptionC());
        optionD.setText("D) " + q.getOptionD());
    }

    // Check user answer
    private void checkAnswer() {

        Question q = questions.get(currentQuestionIndex);
        char selectedOption = ' ';

        if (optionA.isSelected()) selectedOption = 'A';
        else if (optionB.isSelected()) selectedOption = 'B';
        else if (optionC.isSelected()) selectedOption = 'C';
        else if (optionD.isSelected()) selectedOption = 'D';

        if (selectedOption == q.getCorrectAnswer()) {
            score++;
        }
    }

    // Display result and exit
    private void showResult() {

        JOptionPane.showMessageDialog(null,
                "Exam Completed!\n\nScore: " + score + " / " + questions.size(),
                "Result",
                JOptionPane.INFORMATION_MESSAGE);

        System.exit(0);
    }

    public static void main(String[] args) {
        new OnlineExamSystemGUI();
    }
}

// Question class
class Question {

    private final String question;
    private final String optionA;
    private final String optionB;
    private final String optionC;
    private final String optionD;
    private final char correctAnswer;

    public Question(String question, String optionA, String optionB, String optionC, String optionD, char correctAnswer) {
        this.question = question;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctAnswer = correctAnswer;
    }

    public String getQuestion() {
        return question;
    }

    public String getOptionA() {
        return optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public char getCorrectAnswer() {
        return correctAnswer;
    }
}
