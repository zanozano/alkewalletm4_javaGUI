package gui;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import static config.Tokens.*;


public class CustomSecondaryButton {

    public static JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Helvetica", Font.BOLD, 12));
        button.setBackground(SECONDARY_COLOR);
        button.setForeground(ON_SECONDARY_COLOR);
        button.setBorder(new EmptyBorder(0, 0, 0, 0));
        button.setPreferredSize(new Dimension(100, 32));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                button.setBackground(PRESS_SECONDARY_COLOR);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(HOVER_SECONDARY_COLOR);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(SECONDARY_COLOR);
            }
        });


        return button;
    }
}
