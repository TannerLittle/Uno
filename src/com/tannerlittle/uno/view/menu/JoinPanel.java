package com.tannerlittle.uno.view.menu;

import com.tannerlittle.uno.Main;
import com.tannerlittle.uno.network.UnoClient;
import com.tannerlittle.uno.view.BackgroundPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class JoinPanel extends BackgroundPanel {

    private Main main;
    private MenuFrame frame;

    private JTextField field_address;
    private JTextField field_port;

    public JoinPanel(Main main, MenuFrame frame) {
        super(frame.getBackgroundImage(), BackgroundPanel.TILED, 1.0f, 0.5f);

        this.main = main;
        this.frame = frame;

        this.setLayout(new BorderLayout());
        this.setBorder(new EmptyBorder(25, 25, 25, 25));

        Font font = new Font("Helvetica Neue", Font.PLAIN, 14);

        JLabel label_address = new JLabel("Server Address: ");
        label_address.setFont(font);

        JLabel label_port = new JLabel("Server Port: ");
        label_port.setFont(font);
        label_port.setBorder(new EmptyBorder(15, 0, 0, 0));

        this.field_address = new JTextField();
        this.field_port = new JTextField();

        JPanel panel = new JPanel(new BorderLayout());

        JPanel panel_address = new JPanel(new BorderLayout());
        panel_address.add(label_address, BorderLayout.NORTH);
        panel_address.add(field_address, BorderLayout.SOUTH);
        panel_address.setOpaque(false);

        JPanel panel_port = new JPanel(new BorderLayout());
        panel_port.add(label_port, BorderLayout.NORTH);
        panel_port.add(field_port, BorderLayout.SOUTH);
        panel_port.setOpaque(false);

        panel.add(panel_address , BorderLayout.NORTH);
        panel.add(panel_port, BorderLayout.SOUTH);

        this.add(panel, BorderLayout.NORTH);

        JButton button_join = new JButton("Join Server");

        JPanel panel_buttons = new JPanel();
        panel_buttons.add(button_join);

        this.add(panel_buttons, BorderLayout.SOUTH);

        button_join.addActionListener(event -> {
            try {
                InetAddress address = InetAddress.getByName(field_address.getText());
                int port = Integer.valueOf(field_port.getText());

                UnoClient client = new UnoClient(main.getGame(), address, port);
                client.sendCommand(main.getGame().getPlayer().getCommand());

                this.main.setClient(client);

                this.removeAll();
                this.add(new JLabel("Connected. Wait for the host to start the game."));

                this.revalidate();
                this.repaint();
            } catch (UnknownHostException ex) {
                JOptionPane.showMessageDialog(frame, "Unknown host: " + field_address.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid port: " + field_port.getText());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Unable to connect to the Uno server.");
            }
        });
    }
}
