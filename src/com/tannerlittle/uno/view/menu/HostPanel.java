package com.tannerlittle.uno.view.menu;

import com.tannerlittle.uno.Main;
import com.tannerlittle.uno.network.UnoClient;
import com.tannerlittle.uno.network.UnoServer;
import com.tannerlittle.uno.view.BackgroundPanel;
import com.tannerlittle.uno.view.GameFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.net.InetAddress;

public class HostPanel extends BackgroundPanel {

    private Main main;
    private MenuFrame frame;

    private JTextField field_address;
    private JTextField field_port;

    private UnoClient client;

    public HostPanel(Main main, MenuFrame frame) {
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
        this.field_address.setEnabled(false);

        this.field_port = new JTextField();
        this.field_port.setEnabled(false);

        JPanel panel = new JPanel(new BorderLayout());

        JPanel panel_address = new JPanel(new BorderLayout());
        panel_address.add(label_address, BorderLayout.NORTH);
        panel_address.add(field_address, BorderLayout.SOUTH);
        panel_address.setOpaque(false);

        JPanel panel_port = new JPanel(new BorderLayout());
        panel_port.add(label_port, BorderLayout.NORTH);
        panel_port.add(field_port, BorderLayout.SOUTH);
        panel_port.setOpaque(false);

        panel.add(panel_address, BorderLayout.NORTH);
        panel.add(panel_port, BorderLayout.SOUTH);

        this.add(panel, BorderLayout.NORTH);

        JButton button_start = new JButton("Start Server");

        JPanel panel_buttons = new JPanel();
        panel_buttons.add(button_start);

        this.add(panel_buttons, BorderLayout.SOUTH);

        button_start.addActionListener(event -> {
            this.client.sendCommand("START");
            this.frame.setVisible(false);
        });

        this.initialize();
    }

    public void initialize() {
        UnoServer server = null;

        try {
            InetAddress address = InetAddress.getLocalHost();
            int port = 0; // Automatically assign a free port

            server = new UnoServer(address, port);
            this.main.setServer(server);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        if (server == null) {
            System.out.println("Unable to establish a socket connection.");
            System.exit(0);
        }

        try {
            InetAddress address = InetAddress.getByName("0.0.0.0");
            int port = server.getPort();

            this.client = new UnoClient(main.getGame(), address, port);
            this.client.sendCommand(main.getGame().getPlayer().getCommand());
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        this.field_address.setText(server.getSocketAddress().getHostAddress());
        this.field_port.setText(String.valueOf(server.getPort()));
    }
}
