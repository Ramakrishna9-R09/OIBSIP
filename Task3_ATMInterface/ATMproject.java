import java.util.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class User {
    private int userId;
    private int pin;
    private double balance;
    private ArrayList<String> transactionHistory;
    private double dailyWithdrawn;

    public User(int userId, int pin, double balance) {
        this.userId = userId;
        this.pin = pin;
        this.balance = balance;
        this.transactionHistory = new ArrayList<>();
        this.dailyWithdrawn = 0;
    }

    public int getUserId() {
        return userId;
    }

    public int getPin() {
        return pin;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public ArrayList<String> getTransactionHistory() {
        return transactionHistory;
    }

    public void addTransaction(String transaction) {
        transactionHistory.add(transaction);
    }

    public double getDailyWithdrawn() {
        return dailyWithdrawn;
    }

    public void addDailyWithdraw(double amount) {
        dailyWithdrawn += amount;
    }
}

public class ATMproject {

    static HashMap<Integer, User> users = new HashMap<>();
    static final double DAILY_WITHDRAW_LIMIT = 10000;

    public static void main(String[] args) {

        // Demo users
        users.put(101, new User(101, 1234, 5000));
        users.put(102, new User(102, 4321, 8000));
        users.put(103, new User(103, 1111, 12000));

        Scanner sc = new Scanner(System.in);

        System.out.println("==================================");
        System.out.println("         WELCOME TO ATM           ");
        System.out.println("==================================");

        User currentUser = null;
        int attempts = 3;

        // LOGIN WITH 3 ATTEMPTS
        while (attempts > 0) {
            System.out.print("Enter User ID: ");
            int enteredId = sc.nextInt();

            System.out.print("Enter PIN: ");
            int enteredPin = sc.nextInt();

            currentUser = login(enteredId, enteredPin);

            if (currentUser != null) {
                System.out.println("\nLogin Successful! Welcome User " + currentUser.getUserId());
                break;
            } else {
                attempts--;
                System.out.println("Invalid Login! Attempts Left: " + attempts);
            }
        }

        if (currentUser == null) {
            System.out.println("\nAccount Locked! Too many wrong attempts.");
            return;
        }

        // MENU LOOP
        while (true) {
            System.out.println("\n========== ATM MENU ==========");
            System.out.println("1. Transaction History");
            System.out.println("2. Withdraw");
            System.out.println("3. Deposit");
            System.out.println("4. Transfer");
            System.out.println("5. Check Balance");
            System.out.println("6. Quit");
            System.out.print("Enter choice: ");

            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    showTransactionHistory(currentUser);
                    break;

                case 2:
                    withdrawMoney(currentUser, sc);
                    break;

                case 3:
                    depositMoney(currentUser, sc);
                    break;

                case 4:
                    transferMoney(currentUser, sc);
                    break;

                case 5:
                    System.out.println("\nCurrent Balance: ₹" + currentUser.getBalance());
                    break;

                case 6:
                    System.out.println("\nThank you for using ATM. Goodbye!");
                    saveTransactionsToFile(currentUser);
                    return;

                default:
                    System.out.println("\nInvalid choice! Please try again.");
            }
        }
    }

    // LOGIN FUNCTION
    public static User login(int userId, int pin) {
        if (users.containsKey(userId)) {
            User user = users.get(userId);
            if (user.getPin() == pin) {
                return user;
            }
        }
        return null;
    }

    // GET CURRENT DATE TIME STRING
    public static String getDateTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return LocalDateTime.now().format(dtf);
    }

    // TRANSACTION HISTORY
    public static void showTransactionHistory(User user) {
        System.out.println("\n========== TRANSACTION HISTORY ==========");

        if (user.getTransactionHistory().isEmpty()) {
            System.out.println("No transactions found.");
        } else {
            for (String transaction : user.getTransactionHistory()) {
                System.out.println(transaction);
            }
        }

        System.out.println("-----------------------------------------");
        System.out.println("Current Balance: ₹" + user.getBalance());
        System.out.println("-----------------------------------------");
    }

    // WITHDRAW
    public static void withdrawMoney(User user, Scanner sc) {
        System.out.print("\nEnter amount to withdraw: ₹");
        double amount = sc.nextDouble();

        if (amount <= 0) {
            System.out.println("Invalid amount!");
            return;
        }

        // Daily limit check
        if (user.getDailyWithdrawn() + amount > DAILY_WITHDRAW_LIMIT) {
            System.out.println("Daily Withdraw Limit Exceeded!");
            System.out.println("Daily Limit: ₹" + DAILY_WITHDRAW_LIMIT);
            System.out.println("Already Withdrawn Today: ₹" + user.getDailyWithdrawn());
            return;
        }

        if (amount > user.getBalance()) {
            System.out.println("Insufficient Balance!");
            return;
        }

        user.setBalance(user.getBalance() - amount);
        user.addDailyWithdraw(amount);

        String transaction = "[" + getDateTime() + "] Withdrawn: ₹" + amount;
        user.addTransaction(transaction);

        System.out.println("Withdrawal Successful!");
        System.out.println("Remaining Balance: ₹" + user.getBalance());
    }

    // DEPOSIT
    public static void depositMoney(User user, Scanner sc) {
        System.out.print("\nEnter amount to deposit: ₹");
        double amount = sc.nextDouble();

        if (amount <= 0) {
            System.out.println("Invalid amount!");
            return;
        }

        user.setBalance(user.getBalance() + amount);

        String transaction = "[" + getDateTime() + "] Deposited: ₹" + amount;
        user.addTransaction(transaction);

        System.out.println("Deposit Successful!");
        System.out.println("Updated Balance: ₹" + user.getBalance());
    }

    // TRANSFER
    public static void transferMoney(User sender, Scanner sc) {
        System.out.print("\nEnter Receiver User ID: ");
        int receiverId = sc.nextInt();

        if (!users.containsKey(receiverId)) {
            System.out.println("Receiver account not found!");
            return;
        }

        User receiver = users.get(receiverId);

        System.out.print("Enter amount to transfer: ₹");
        double amount = sc.nextDouble();

        if (amount <= 0) {
            System.out.println("Invalid amount!");
            return;
        }

        if (amount > sender.getBalance()) {
            System.out.println("Insufficient Balance!");
            return;
        }

        // Deduct sender
        sender.setBalance(sender.getBalance() - amount);

        // Add receiver
        receiver.setBalance(receiver.getBalance() + amount);

        String senderTransaction = "[" + getDateTime() + "] Transferred: ₹" + amount + " to User ID " + receiverId;
        String receiverTransaction = "[" + getDateTime() + "] Received: ₹" + amount + " from User ID " + sender.getUserId();

        sender.addTransaction(senderTransaction);
        receiver.addTransaction(receiverTransaction);

        System.out.println("Transfer Successful!");
        System.out.println("Your New Balance: ₹" + sender.getBalance());
    }

    // SAVE TRANSACTIONS TO FILE
    public static void saveTransactionsToFile(User user) {
        String fileName = "transactions_" + user.getUserId() + ".txt";

        try (FileWriter fw = new FileWriter(fileName)) {
            fw.write("========== TRANSACTION HISTORY ==========\n");
            fw.write("User ID: " + user.getUserId() + "\n");
            fw.write("-----------------------------------------\n");

            if (user.getTransactionHistory().isEmpty()) {
                fw.write("No transactions found.\n");
            } else {
                for (String transaction : user.getTransactionHistory()) {
                    fw.write(transaction + "\n");
                }
            }

            fw.write("-----------------------------------------\n");
            fw.write("Final Balance: ₹" + user.getBalance() + "\n");
            fw.write("=========================================\n");

            System.out.println("\nTransactions saved to file: " + fileName);

        } catch (IOException e) {
            System.out.println("Error saving transactions to file!");
        }
    }
}
