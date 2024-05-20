package db;

import gui.ActionDialog;
import org.application.Balance;
import org.application.Transaction;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SQLQueries {

    private static String userEmail;

    public static void setUserEmail(String userEmail) {
        SQLQueries.userEmail = userEmail;
    }

    public static String getUserEmail() {
        return userEmail;
    }

    public static final String GET_USER_ID = "SELECT id FROM users WHERE email = ?";
    public static final String VALIDATE_ACCOUNT = "SELECT * FROM users WHERE email = ? AND password = ?";
    public static final String VIEW_BALANCE = "SELECT c.currency_code, b.amount " +
            "FROM users u " +
            "JOIN accounts a ON u.id = a.user_id " +
            "JOIN balances b ON a.id = b.account_id " +
            "JOIN currencies c ON b.currency_code = c.currency_code " +
            "WHERE u.email = ?";
    public static final String UPDATE_BALANCE_ADD = "UPDATE balances AS b " +
            "SET amount = amount + ? " +
            "FROM accounts AS a " +
            "JOIN users AS u ON a.user_id = u.id " +
            "WHERE b.account_id = a.id " +
            "AND u.email = ? " +
            "AND b.currency_code = 'CLP'";
    public static final String UPDATE_BALANCE_SUB = "UPDATE balances AS b " +
            "SET amount = amount - ? " +
            "FROM accounts AS a " +
            "JOIN users AS u ON a.user_id = u.id " +
            "WHERE b.account_id = a.id " +
            "AND u.email = ? " +
            "AND b.currency_code = 'CLP'";
    public static final String ADD_TRANSACTION = "INSERT INTO transactions (sender_id, receiver_id, amount, type, currency_code) " +
            "VALUES (?, ?, ?, ?, 'CLP')";
    public static final String UPDATE_EXCHANGE = "UPDATE balances AS b " +
            "SET amount = amount + ? " +
            "FROM accounts AS a " +
            "JOIN users AS u ON a.user_id = u.id " +
            "WHERE b.account_id = a.id " +
            "AND u.email = ? " +
            "AND b.currency_code = ? ";
    public static final String GET_TRANSACTIONS = "SELECT amount, transaction_date, sender_id, receiver_id, currency_code, type " +
            "FROM transactions " +
            "WHERE sender_id = ? OR receiver_id = ?";

    // Validate account
    public static boolean validateAccount(Connection connection, String userEmail, String userPassword) {
        boolean isAuthenticated = false;

        try (PreparedStatement preparedStatement = connection.prepareStatement(VALIDATE_ACCOUNT)) {
            preparedStatement.setString(1, userEmail);
            preparedStatement.setString(2, userPassword);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    System.out.println("User logged successfully");
                    isAuthenticated = true;
                    setUserEmail(userEmail);
                } else {
                    System.out.println("Invalid email or password.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error executing SQL query: " + e.getMessage());
        }

        return isAuthenticated;
    }

    // Get Balance
    public static List<Balance> viewBalance(Connection connection) {
        List<Balance> balances = new ArrayList<>();

        if (getUserEmail() == null) {
            System.out.println("No user authenticated.");
            return balances;
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement(VIEW_BALANCE)) {
            preparedStatement.setString(1, getUserEmail());
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String currencyCode = resultSet.getString("currency_code");
                double amount = resultSet.getDouble("amount");
                balances.add(new Balance(currencyCode, amount));
            }
        } catch (SQLException e) {
            System.out.println("Error connecting to database: " + e.getMessage());
        }

        return balances;
    }

    // deposit CLP
    public static void deposit(Connection connection) {

        ActionDialog dialog = new ActionDialog(null, "Deposit CLP", "Enter deposit amount", "DEPOSIT");
        dialog.setVisible(true);
        String amountStr = dialog.getAmount();

        if (amountStr == null) {
            return;
        }
        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid amount entered. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (PreparedStatement userIdStatement = connection.prepareStatement(GET_USER_ID)) {
            userIdStatement.setString(1, getUserEmail());
            ResultSet userIdResult = userIdStatement.executeQuery();

            String userId;
            if (userIdResult.next()) {
                userId = userIdResult.getString("id");
            } else {
                JOptionPane.showMessageDialog(null, "User not found with email: " + getUserEmail(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            PreparedStatement updateStatement = connection.prepareStatement(UPDATE_BALANCE_ADD);
            updateStatement.setDouble(1, amount);
            updateStatement.setString(2, getUserEmail());
            int rowsUpdated = updateStatement.executeUpdate();

            if (rowsUpdated > 0) {

                PreparedStatement insertStatement = connection.prepareStatement(ADD_TRANSACTION);
                insertStatement.setObject(1, UUID.fromString(userId));
                insertStatement.setObject(2, UUID.fromString(userId));
                insertStatement.setDouble(3, amount);
                insertStatement.setString(4, "DEPOSIT");
                insertStatement.executeUpdate();

                JOptionPane.showMessageDialog(null, "Deposit to your CLP account successful.", "Success", JOptionPane.INFORMATION_MESSAGE);

            } else {
                JOptionPane.showMessageDialog(null, "Error depositing.", "Error", JOptionPane.ERROR_MESSAGE);

            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error connecting to database: " + e.getMessage(), "Database Connection Error", JOptionPane.ERROR_MESSAGE);

        }
    }

    // withdraw CLP
    public static void withdraw(Connection connection) {

        ActionDialog dialog = new ActionDialog(null, "Withdraw CLP", "Enter withdraw amount", "WITHDRAW");
        dialog.setVisible(true);
        String amountStr = dialog.getAmount();

        if (amountStr == null) {
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid amount entered. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (PreparedStatement userIdStatement = connection.prepareStatement(GET_USER_ID)) {
            userIdStatement.setString(1, getUserEmail());
            ResultSet userIdResult = userIdStatement.executeQuery();
            String userId;

            if (userIdResult.next()) {
                userId = userIdResult.getString("id");
            } else {
                JOptionPane.showMessageDialog(null, "User not found with email: " + userEmail, "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            PreparedStatement updateStatement = connection.prepareStatement(UPDATE_BALANCE_SUB);
            updateStatement.setDouble(1, amount);
            updateStatement.setString(2, getUserEmail());
            int rowsUpdated = updateStatement.executeUpdate();

            if (rowsUpdated > 0) {

                PreparedStatement insertStatement = connection.prepareStatement(ADD_TRANSACTION);
                insertStatement.setObject(1, UUID.fromString(userId));
                insertStatement.setObject(2, UUID.fromString(userId));
                insertStatement.setDouble(3, amount);
                insertStatement.setString(4, "WITHDRAW");
                insertStatement.executeUpdate();
                JOptionPane.showMessageDialog(null, "Withdrawal from your CLP account successful.", "Success", JOptionPane.INFORMATION_MESSAGE);

            } else {
                JOptionPane.showMessageDialog(null, "Error withdrawing.", "Error", JOptionPane.ERROR_MESSAGE);

            }

        } catch (SQLException e) {
            System.out.println("Error connecting to database: " + e.getMessage());
        }
    }

    // exchange
    public static void exchange(Connection connection, double amount, String currencyCode) {

        try {
            amount = Double.parseDouble(String.valueOf(amount));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid amount entered. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            connection.setAutoCommit(false);

            double exchangeRate = getExchangeRate(currencyCode);

            double equivalentAmount = amount * exchangeRate;

            updateBalance(connection, amount, "CLP");

            updateBalance(connection, equivalentAmount, currencyCode);

            connection.commit();

            JOptionPane.showMessageDialog(null, "Exchange to " + currencyCode + " successful.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                System.out.println("Error rolling back transaction: " + ex.getMessage());
            }
            JOptionPane.showMessageDialog(null, "Error performing exchange: " + e.getMessage(), "Exchange Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                System.out.println("Error setting auto-commit mode: " + ex.getMessage());
            }
        }
    }

    // statement
    private static void updateBalance(Connection connection, double amount, String currencyCode) throws SQLException {

        if ("CLP".equals(currencyCode)) {
            try (PreparedStatement updateStatement = connection.prepareStatement(UPDATE_BALANCE_SUB)) {
                updateStatement.setDouble(1, amount);
                updateStatement.setString(2, userEmail);
                updateStatement.executeUpdate();
            }
        } else {
            try (PreparedStatement updateStatement = connection.prepareStatement(UPDATE_EXCHANGE)) {
                updateStatement.setDouble(1, amount);
                updateStatement.setString(2, getUserEmail());
                updateStatement.setString(3, currencyCode);
                updateStatement.executeUpdate();
            }
        }
    }

    //get rate
    public static double getExchangeRate(String currency) {
        return switch (currency) {
            case "USD" -> 0.00108;
            case "EUR" -> 0.00100;
            case "THB" -> 0.0396045;
            case "CNY" -> 0.00780;
            default -> 1.0;
        };
    }

    // transactions
    public static List<Transaction> transactions(Connection connection) throws SQLException {
        List<Transaction> transactions = new ArrayList<>();

        String userEmail = getUserEmail();
        if (userEmail == null) {
            System.out.println("No user authenticated.");
            return transactions;
        }

        String userId;
        try (PreparedStatement userIdStatement = connection.prepareStatement(GET_USER_ID)) {
            userIdStatement.setString(1, userEmail);
            try (ResultSet userIdResult = userIdStatement.executeQuery()) {
                if (userIdResult.next()) {
                    userId = userIdResult.getString("id");
                } else {
                    throw new IllegalArgumentException("User not found with email: " + userEmail);
                }
            }
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement(GET_TRANSACTIONS)) {
            preparedStatement.setObject(1, UUID.fromString(userId));
            preparedStatement.setObject(2, UUID.fromString(userId));
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    double amount = resultSet.getDouble("amount");
                    String transactionDate = resultSet.getString("transaction_date");
                    String senderId = resultSet.getString("sender_id");
                    String receiverId = resultSet.getString("receiver_id");
                    String currencyCode = resultSet.getString("currency_code");
                    String type = resultSet.getString("type");

                    String senderEmail = getEmailById(connection, senderId);
                    String receiverEmail = getEmailById(connection, receiverId);

                    Transaction transaction = new Transaction(amount, transactionDate, senderEmail, receiverEmail, currencyCode, type);
                    transactions.add(transaction);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving transactions from database: " + e.getMessage());
        }
        return transactions;
    }

    // change id for email
    private static String getEmailById(Connection connection, String userId) throws SQLException {
        String email = null;
        try (PreparedStatement emailStatement = connection.prepareStatement("SELECT email FROM users WHERE id = ?")) {
            emailStatement.setObject(1, UUID.fromString(userId));
            try (ResultSet emailResult = emailStatement.executeQuery()) {
                if (emailResult.next()) {
                    email = emailResult.getString("email");
                }
            }
        }
        return email != null ? email : userId;
    }

}
