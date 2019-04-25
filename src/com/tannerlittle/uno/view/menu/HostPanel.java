package com.tannerlittle.uno.view.menu;

import com.tannerlittle.uno.Main;
import com.tannerlittle.uno.network.UnoClient;
import com.tannerlittle.uno.network.UnoServer;
import com.tannerlittle.uno.view.GameFrame;

import javax.swing.*;
import java.io.IOException;
import java.net.InetAddress;

public class HostPanel extends JPanel {

    private Main main;
    private MenuFrame frame;

    private JTextField field_address;
    private JTextField field_port;

    private UnoClient client;

    public HostPanel(Main main, MenuFrame frame) {
        this.main = main;
        this.frame = frame;

        JLabel label_address = new JLabel("Server Address: ");
        JLabel label_port = new JLabel("Server Port: ");

        this.field_address = new JTextField();
        this.field_address.setEnabled(false);

        this.field_port = new JTextField();
        this.field_port.setEnabled(false);

        JButton button_start = new JButton("Start Server");

        JPanel panel_buttons = new JPanel();
        panel_buttons.add(button_start);

        Box box = new Box(BoxLayout.Y_AXIS);
        box.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        box.add(Box.createVerticalGlue());
        box.add(label_address);
        box.add(field_address);
        box.add(label_port);
        box.add(field_port);
        box.add(panel_buttons);
        box.add(Box.createVerticalGlue());

        this.add(box);

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

            this.main.setClient(client);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        this.field_address.setText(server.getSocketAddress().getHostAddress());
        this.field_port.setText(String.valueOf(server.getPort()));
    }
}
