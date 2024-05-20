package gui;

import db.DatabaseConnector;
import db.SQLQueries;
import org.application.Balance;
import org.application.Transaction;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class MenuGUI extends JFrame {

    private final JTable balanceTable;
    private final JTable transactionTable;

    public MenuGUI() {

        setTitle("AlkeWallet / Menu");
        setSize(800, 440);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/brand.png")));
        setIconImage(icon.getImage());

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        getContentPane().add(panel);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(5, 5, 5, 5);

        JPanel buttonPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        JButton viewBalanceButton = CustomSecondaryButton.createButton("BALANCE");
        JButton transactionsButton = CustomSecondaryButton.createButton("TRANSACTIONS");
        JButton depositButton = CustomSecondaryButton.createButton("DEPOSIT");
        JButton withdrawButton = CustomSecondaryButton.createButton("WITHDRAW");
        JButton currencyExchangeButton = CustomSecondaryButton.createButton("EXCHANGE");
        JButton logoutButton = CustomSecondaryButton.createButton("LOGOUT");
        buttonPanel.add(viewBalanceButton);
        buttonPanel.add(depositButton);
        buttonPanel.add(withdrawButton);
        buttonPanel.add(currencyExchangeButton);
        buttonPanel.add(transactionsButton);
        buttonPanel.add(logoutButton);

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 0.2;
        constraints.weighty = 0.0;
        constraints.fill = GridBagConstraints.BOTH;
        panel.add(buttonPanel, constraints);

        balanceTable = new JTable();
        balanceTable.setEnabled(false);
        JScrollPane scrollPaneBalance = new JScrollPane(balanceTable);
        scrollPaneBalance.setMinimumSize(new Dimension(100, 200));
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.weightx = 0.8;
        constraints.fill = GridBagConstraints.BOTH;
        panel.add(scrollPaneBalance, constraints);

        transactionTable = new JTable();
        transactionTable.setEnabled(false);
        JScrollPane scrollPaneTransaction = new JScrollPane(transactionTable);
        scrollPaneTransaction.setMinimumSize(new Dimension(100, 200));
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.weightx = 0.8;
        constraints.fill = GridBagConstraints.BOTH;
        panel.add(scrollPaneTransaction, constraints);

        viewBalanceButton.addActionListener(new ViewBalanceButtonListener());
        depositButton.addActionListener(new DepositButtonListener());
        withdrawButton.addActionListener(new WithdrawButtonListener());
        currencyExchangeButton.addActionListener(new CurrencyExchangeButtonListener());
        transactionsButton.addActionListener(new TransactionsButtonListener());
        logoutButton.addActionListener(new LogoutButtonListener());

    }

    private void showBalanceTable() {
        DefaultTableModel transactionTableModel = (DefaultTableModel) transactionTable.getModel();
        transactionTableModel.setRowCount(0);

        try {
            DatabaseConnector connector = new DatabaseConnector();
            Connection connection = connector.getConnection();

            List<Balance> balances = SQLQueries.viewBalance(connection);

            DefaultTableModel balanceTableModel = new DefaultTableModel();
            balanceTableModel.addColumn("Currency");
            balanceTableModel.addColumn("Amount");
            for (Balance balance : balances) {
                balanceTableModel.addRow(new Object[]{balance.currencyCode(), balance.amount()});
            }

            balanceTable.setModel(balanceTableModel);
            balanceTable.setVisible(!balances.isEmpty());
            transactionTable.setVisible(balances.isEmpty());

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(MenuGUI.this,
                    "Error connecting to the database: " + ex.getMessage(),
                    "Database Connection Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private class DepositButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                DatabaseConnector connector = new DatabaseConnector();
                Connection connection = connector.getConnection();
                SQLQueries.deposit(connection);

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(MenuGUI.this,
                        "Error connecting to the database: " + ex.getMessage(),
                        "Database Connection Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class WithdrawButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                DatabaseConnector connector = new DatabaseConnector();
                Connection connection = connector.getConnection();
                SQLQueries.withdraw(connection);

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(MenuGUI.this,
                        "Error connecting to the database: " + ex.getMessage(),
                        "Database Connection Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class CurrencyExchangeButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                DatabaseConnector connector = new DatabaseConnector();
                Connection connection = connector.getConnection();
                JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(MenuGUI.this);

                ExchangeDialog exchangeDialog = new ExchangeDialog(parentFrame);
                exchangeDialog.setVisible(true);

                if (exchangeDialog.isConfirmed()) {
                    String selectedCurrency = exchangeDialog.getSelectedCurrency();
                    String amountText = exchangeDialog.getAmount();

                    double amount;
                    try {
                        amount = Double.parseDouble(amountText);

                        if (amount == 0) {
                            JOptionPane.showMessageDialog(MenuGUI.this,
                                    "The amount cannot be zero. Please enter a valid amount.",
                                    "Invalid Amount",
                                    JOptionPane.WARNING_MESSAGE);
                            return;
                        }

                        SQLQueries.exchange(connection, amount, selectedCurrency);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(MenuGUI.this,
                                "Invalid amount entered. Please enter a valid number.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(MenuGUI.this,
                        "Error connecting to the database: " + ex.getMessage(),
                        "Database Connection Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        }
    }

    private void showTransactionTable() {
        DefaultTableModel balanceTableModel = (DefaultTableModel) balanceTable.getModel();
        balanceTableModel.setRowCount(0);

        try {
            DatabaseConnector connector = new DatabaseConnector();
            Connection connection = connector.getConnection();

            List<Transaction> transactions = SQLQueries.transactions(connection);

            DefaultTableModel transactionTableModel = new DefaultTableModel();
            transactionTableModel.addColumn("Type");
            transactionTableModel.addColumn("Amount");
            transactionTableModel.addColumn("Currency");
            transactionTableModel.addColumn("Date");
            transactionTableModel.addColumn("Sender ID");
            transactionTableModel.addColumn("Receiver ID");

            for (Transaction transaction : transactions) {
                transactionTableModel.addRow(new Object[]{
                        transaction.type(),
                        transaction.amount(),
                        transaction.currencyCode(),
                        transaction.transactionDate(),
                        transaction.senderId(),
                        transaction.receiverId(),
                });
            }

            transactionTable.setModel(transactionTableModel);
            transactionTable.setVisible(!transactions.isEmpty());
            balanceTable.setVisible(transactions.isEmpty());

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(MenuGUI.this,
                    "Error connecting to the database: " + ex.getMessage(),
                    "Database Connection Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private class LogoutButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            LogoutConfirmDialog confirmDialog = new LogoutConfirmDialog(MenuGUI.this);
            confirmDialog.setVisible(true);

            if (confirmDialog.isConfirmed()) {
                try {
                    DatabaseConnector connector = new DatabaseConnector();
                    Connection connection = connector.getConnection();
                    connection.close();

                    MenuGUI.this.dispose();

                    LoginGUI loginGUI = new LoginGUI();
                    loginGUI.setVisible(true);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(MenuGUI.this,
                            "Error connecting to the database: " + ex.getMessage(),
                            "Database Connection Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private class ViewBalanceButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            showBalanceTable();
        }
    }

    private class TransactionsButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            showTransactionTable();
        }
    }

}