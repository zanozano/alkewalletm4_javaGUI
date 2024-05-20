package org.application;

import db.DatabaseConnector;
import gui.LoginGUI;

import javax.swing.*;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {

        try {
            DatabaseConnector connector = new DatabaseConnector();
            connector.getConnection();
            SwingUtilities.invokeLater(() -> new LoginGUI().setVisible(true));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Error connecting to the database: " + e.getMessage(),
                    "Database Connection Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

}
