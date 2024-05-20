package gui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

import db.DatabaseConnector;
import db.SQLQueries;

public class LoginGUI extends JFrame {

    private final JTextField emailTextField;
    private final JPasswordField passwordField;

    public LoginGUI() {

        setTitle("AlkeWallet / Login");
        setSize(440, 440);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/brand.png")));
        setIconImage(icon.getImage());

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        getContentPane().add(panel);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(5, 5, 5, 5);

        // Title
        JLabel titleLabel = new JLabel("Welcome to AlkeWallet!", JLabel.CENTER);
        titleLabel.setFont(new Font("Helvetica", Font.BOLD, 16));
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.insets = new Insets(5, 5, 2, 5);
        panel.add(titleLabel, constraints);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Please log in below", JLabel.CENTER);
        subtitleLabel.setFont(new Font("Helvetica", Font.PLAIN, 14));
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 2;
        constraints.insets = new Insets(5, 5, 20, 5);
        panel.add(subtitleLabel, constraints);

        // Email label
        JLabel emailLabel = new JLabel("Email");
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        constraints.insets = new Insets(5, 5, 2, 5);
        panel.add(emailLabel, constraints);

        // Email input
        emailTextField = new CustomTextField(15);
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 2;
        constraints.insets = new Insets(2, 5, 10, 5);
        panel.add(emailTextField, constraints);

        // Password label
        JLabel passwordLabel = new JLabel("Password");
        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 2;
        constraints.insets = new Insets(10, 5, 2, 5);
        panel.add(passwordLabel, constraints);

        // Password input
        passwordField = new CustomPasswordField(15);
        constraints.gridx = 0;
        constraints.gridy = 5;
        constraints.gridwidth = 2;
        constraints.insets = new Insets(2, 5, 10, 5);
        panel.add(passwordField, constraints);

        // Button
        JButton loginButton = CustomPrimaryButton.createButton("CONFIRM");
        constraints.gridx = 0;
        constraints.gridy = 6;
        constraints.gridwidth = 2;
        constraints.insets = new Insets(10, 5, 5, 5);
        constraints.anchor = GridBagConstraints.CENTER;
        panel.add(loginButton, constraints);

        loginButton.addActionListener(new LoginButtonListener());
    }

    private class LoginButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String email = emailTextField.getText();
            String password = new String(passwordField.getPassword());

            try {
                DatabaseConnector connector = new DatabaseConnector();
                Connection connection = connector.getConnection();
                boolean isValidUser = SQLQueries.validateAccount(connection, email, password);

                if (isValidUser) {
                    JOptionPane.showMessageDialog(LoginGUI.this, "Login Successfully");
                    dispose();
                    SwingUtilities.invokeLater(() -> new MenuGUI().setVisible(true));
                } else {
                    JOptionPane.showMessageDialog(LoginGUI.this,
                            "Incorrect Email or password. Please try again.",
                            "Login Error",
                            JOptionPane.WARNING_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(LoginGUI.this,
                        "Error connecting to the database: " + ex.getMessage(),
                        "Database Connection Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}