package gui;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class ActionDialog extends JDialog {
    private final JTextField amountField;

    private String amount;

    public ActionDialog(Frame parent, String title, String labelText, String buttonText) {
        super(parent, title, true);

        ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/brand.png")));
        setIconImage(icon.getImage());

        JPanel panel = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 2, 10);

        JLabel amountLabel = new JLabel(labelText);
        panel.add(amountLabel, gbc);

        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(2, 10, 15, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        amountField = new CustomTextField(10);
        panel.add(amountField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = CustomSecondaryButton.createButton("CANCEL");
        JButton actionButton = CustomPrimaryButton.createButton(buttonText);
        buttonPanel.add(cancelButton);
        buttonPanel.add(actionButton);

        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.5;
        gbc.insets = new Insets(10, 5, 5, 5);
        panel.add(buttonPanel, gbc);

        getContentPane().add(panel);
        pack();
        setLocationRelativeTo(parent);

        cancelButton.addActionListener(_ -> {
            amount = null;
            dispose();
        });

        actionButton.addActionListener(_ -> {
            amount = amountField.getText();
            dispose();
        });
    }

    public String getAmount() {
        return amount;
    }
}
