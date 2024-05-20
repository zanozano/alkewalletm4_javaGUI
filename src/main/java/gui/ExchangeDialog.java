package gui;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class ExchangeDialog extends JDialog {
    private final JComboBox<String> currencyComboBox;
    private final JTextField amountField;
    private boolean confirmed;

    public ExchangeDialog(JFrame parentFrame) {
        super(parentFrame, "Currency Exchange", true);
        ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/brand.png")));
        setIconImage(icon.getImage());

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 2, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Select currency"), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(2, 5, 10, 5);
        currencyComboBox = new JComboBox<>(new String[]{"USD", "EUR", "THB", "CNY"});
        add(currencyComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.insets = new Insets(5, 5, 2, 5);
        add(new JLabel("Enter amount"), gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.insets = new Insets(2, 5, 15, 5);
        amountField = new CustomTextField(10);
        add(amountField, gbc);

        JPanel buttonPanel = new JPanel();
        JButton confirmButton = CustomPrimaryButton.createButton("CONFIRM");
        JButton cancelButton = CustomSecondaryButton.createButton("CANCEL");
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 5, 5, 5);
        add(buttonPanel, gbc);

        confirmButton.addActionListener(_ -> {
            confirmed = true;
            dispose();
        });

        cancelButton.addActionListener(_ -> {
            confirmed = false;
            dispose();
        });

        pack();
        setLocationRelativeTo(parentFrame);
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public String getSelectedCurrency() {
        return (String) currencyComboBox.getSelectedItem();
    }

    public String getAmount() {
        return amountField.getText();
    }
}
