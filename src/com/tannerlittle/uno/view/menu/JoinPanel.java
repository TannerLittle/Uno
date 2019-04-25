package com.tannerlittle.uno.view.menu;

import com.tannerlittle.uno.Main;
import com.tannerlittle.uno.network.UnoClient;
import com.tannerlittle.uno.view.GameFrame;

import javax.swing.*;
import java.io.IOException;
import java.net.InetAddress;

public class JoinPanel extends JPanel {

    private Main main;
    private MenuFrame frame;

    private JLabel label_address;
    private JLabel label_port;

    private JTextField field_address;
    private JTextField field_port;

    public JoinPanel(Main main, MenuFrame frame) {
        this.main = main;
        this.frame = frame;

        this.label_address = new JLabel("Server Address: ");
        this.field_address = new JTextField(10);

        this.label_port = new JLabel("Server Port: ");
        this.field_port = new JTextField(10);

        JButton button_join = new JButton("Join Server");

        JPanel panel_buttons = new JPanel();
        panel_buttons.add(button_join);

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

        button_join.addActionListener(event -> {
            try {
                InetAddress address = InetAddress.getByName(field_address.getText());
                int port = Integer.valueOf(field_port.getText());

                UnoClient client = new UnoClient(main.getGame(), address, port);
                client.sendCommand(main.getGame().getPlayer().getCommand());

                this.main.setClient(client);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }
}
