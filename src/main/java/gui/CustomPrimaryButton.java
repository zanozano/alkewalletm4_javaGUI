package gui;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import static config.Tokens.*;


public class CustomPrimaryButton {

    public static JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Helvetica", Font.BOLD, 12));
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(ON_PRIMARY_COLOR);
        button.setBorder(new EmptyBorder(0, 0, 0, 0));
        button.setPreferredSize(new Dimension(100, 32));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                button.setBackground(PRESS_PRIMARY_COLOR);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(HOVER_PRIMARY_COLOR);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(PRIMARY_COLOR);
            }
        });


        return button;
    }
}
