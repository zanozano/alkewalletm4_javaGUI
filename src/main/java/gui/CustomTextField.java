package gui;

import javax.swing.*;
import java.awt.*;

import static config.Tokens.DEFAULT_COLOR;
import static config.Tokens.TEXT_COLOR;

public class CustomTextField extends JTextField {

    public CustomTextField(int columns) {
        super(columns);
        setPreferredSize(new Dimension(200, 24));
        initialize();
    }

    private void initialize() {
        setFont(new Font("Helvetica", Font.PLAIN, 12));
        setForeground(TEXT_COLOR);
        setBackground(DEFAULT_COLOR);
        setBorder(BorderFactory.createLineBorder(Color.GRAY));
        setEditable(true);

        setCaretPosition(0);
        setSelectionStart(0);
        setSelectionEnd(getDocument().getLength());

        getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updateCaretPosition();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updateCaretPosition();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updateCaretPosition();
            }

            private void updateCaretPosition() {
                SwingUtilities.invokeLater(() -> setCaretPosition(getDocument().getLength()));
            }
        });
    }
}
