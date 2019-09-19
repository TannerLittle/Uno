package com.tannerlittle.uno.view.menu;

import com.tannerlittle.uno.Main;
import com.tannerlittle.uno.UnoGame;
import com.tannerlittle.uno.model.Player;
import com.tannerlittle.uno.view.BackgroundPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.UUID;

public class PlayerPanel extends BackgroundPanel {

    private Main main;
    private MenuFrame frame;

    public PlayerPanel(Main main, MenuFrame frame) {
        super(frame.getBackgroundImage(), BackgroundPanel.TILED, 1.0f, 0.5f);

        this.main = main;
        this.frame = frame;

        this.setLayout(new BorderLayout());
        this.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel label = new JLabel("What is your name?");
        label.setFont(new Font("Helvetica Neue", Font.PLAIN, 14));
        JTextField field = new JTextField(20);

        JButton button_host = new JButton("Host Server");
        JButton button_join = new JButton("Join Server");

        JPanel panel_buttons = new JPanel();
        panel_buttons.add(button_host);
        panel_buttons.add(button_join);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.SOUTH);

        this.add(panel, BorderLayout.NORTH);
        this.add(panel_buttons, BorderLayout.SOUTH);

        button_host.addActionListener(event -> {
            String name = field.getText();

            Player player = new Player(UUID.randomUUID(), name);
            UnoGame game = new UnoGame(player, false);

            this.main.setGame(game);

            frame.remove(this);
            frame.add(new HostPanel(main, frame));
            frame.revalidate();
            frame.repaint();
        });

        button_join.addActionListener(event -> {
            String name = field.getText();

            Player player = new Player(UUID.randomUUID(), name);
            UnoGame game = new UnoGame(player, false);

            this.main.setGame(game);

            frame.remove(this);
            frame.add(new JoinPanel(main, frame));
            frame.revalidate();
            frame.repaint();
        });
    }
}
