package gui;

import javax.swing.*;
import java.awt.*;
import static config.Tokens.*;

public class CustomPasswordField extends JPasswordField {

    public CustomPasswordField(int columns) {
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
        setSelectionEnd(getPassword().length);

        getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updateCaretPosition();
            }
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updateCaretPosition();
            }
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updateCaretPosition();
            }

            private void updateCaretPosition() {
                SwingUtilities.invokeLater(() -> setCaretPosition(getPassword().length));
            }
        });
    }
}
