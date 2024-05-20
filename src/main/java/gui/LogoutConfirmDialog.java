package gui;

import javax.swing.*;
import java.awt.*;

public class LogoutConfirmDialog extends JDialog {
    private boolean confirmed;

    public LogoutConfirmDialog(JFrame parentFrame) {
        super(parentFrame, "Confirm Logout", true);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 15, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(new JLabel("Do you want to logout?"), gbc);

        JPanel buttonPanel = new JPanel();
        JButton yesButton = CustomPrimaryButton.createButton("Yes");
        JButton noButton = CustomSecondaryButton.createButton("No");
        buttonPanel.add(yesButton);
        buttonPanel.add(noButton);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 5, 5, 5);
        add(buttonPanel, gbc);

        yesButton.addActionListener(_ -> {
            confirmed = true;
            dispose();
        });

        noButton.addActionListener(_ -> {
            confirmed = false;
            dispose();
        });

        pack();
        setLocationRelativeTo(parentFrame);
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}
