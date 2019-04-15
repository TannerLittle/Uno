package com.tannerlittle.uno.view.menu;

import com.tannerlittle.uno.Main;
import com.tannerlittle.uno.UnoGame;
import com.tannerlittle.uno.model.Player;
import com.tannerlittle.uno.network.UnoClient;
import com.tannerlittle.uno.network.UnoServer;
import com.tannerlittle.uno.view.GameFrame;

import javax.swing.*;
import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;

public class PlayerPanel extends JPanel {

    private Main main;
    private MenuFrame frame;

    private JLabel label;
    private JTextField field;

    public PlayerPanel(Main main, MenuFrame frame) {
        this.main = main;
        this.frame = frame;

        this.label = new JLabel("What is your name?");
        this.field = new JTextField(20);

        JButton button_host = new JButton("Host Server");
        JButton button_join = new JButton("Join Server");

        JPanel panel_buttons = new JPanel();
        panel_buttons.add(button_host);
        panel_buttons.add(button_join);

        Box box = new Box(BoxLayout.Y_AXIS);
        box.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        box.add(Box.createVerticalGlue());
        box.add(label);
        box.add(field);
        box.add(panel_buttons);
        box.add(Box.createVerticalGlue());

        this.add(box);

        button_host.addActionListener(event -> {
            String name = field.getText();

            Player player = new Player(UUID.randomUUID(), name);
            UnoGame game = new UnoGame(player);

            this.main.setGame(game);

            frame.remove(this);
            frame.add(new HostPanel(main, frame));
            frame.revalidate();
            frame.repaint();
        });

        button_join.addActionListener(event -> {
            String name = field.getText();

            Player player = new Player(UUID.randomUUID(), name);
            UnoGame game = new UnoGame(player);

            this.main.setGame(game);

            frame.remove(this);
            frame.add(new JoinPanel(main, frame));
            frame.revalidate();
            frame.repaint();
        });
    }
}
