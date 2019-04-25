package com.tannerlittle.uno.view;

import com.tannerlittle.uno.UnoGame;
import com.tannerlittle.uno.enums.GameState;
import com.tannerlittle.uno.network.UnoClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GameFrame extends JFrame {

    private UnoClient client;
    private UnoGame game;

    private JPanel panel_top;
    private JPanel panel_center;
    private JPanel panel_bottom;

    private JPanel panel_players;

    //TODO: make button_end a custom panel as well
    private JButton button_end;
    private JPanel panel_button_uno;

    public GameFrame(UnoClient client, UnoGame game) throws HeadlessException {
        this.client = client;
        this.game = game;

        // Initialize top panel
        this.panel_top = new JPanel(new BorderLayout());
        this.getContentPane().add(panel_top, BorderLayout.NORTH);

        // Initialize center panel
        this.panel_center = new JPanel();
        this.getContentPane().add(panel_center, BorderLayout.CENTER);

        // Initialize bottom panel
        this.panel_bottom = new JPanel();
        this.getContentPane().add(panel_bottom, BorderLayout.SOUTH);

        // Initialize 'End Turn' button
        this.button_end = new JButton("End Turn");
        this.button_end.setPreferredSize(new Dimension(200, 75));
        this.button_end.addActionListener(event -> {
            this.client.sendCommand("ROTATE " + game.getPlayer().getUniqueId());
        });

        JPanel panel_button_end = new JPanel();
        panel_button_end.add(button_end);

        // Initialize 'Uno' button
        this.panel_button_uno = new UnoButtonPanel();
        this.panel_button_uno.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                GameFrame.this.client.sendCommand("UNO " + game.getPlayer().getUniqueId());
            }
        });

        // Initialize buttons framework
        JPanel panel_buttons = new JPanel();
        panel_buttons.setLayout(new BoxLayout(panel_buttons, BoxLayout.Y_AXIS));

        JPanel panel_buttons_right = new JPanel();
        panel_buttons_right.setLayout(new FlowLayout(FlowLayout.RIGHT));
        panel_buttons_right.add(panel_buttons);

        panel_buttons.add(panel_button_end, BorderLayout.EAST);
        panel_buttons.add(panel_button_uno, BorderLayout.EAST);

        this.panel_top.add(panel_buttons_right, BorderLayout.EAST);

        int width = 500;
        int height = 400;

        this.setSize(width, height);

        this.setTitle("Uno | Player: " + game.getPlayer().getName());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setUndecorated(true);

        this.revalidate();
        this.repaint();
        this.setVisible(true);
    }

    public void update() {
        // Clean up
        this.panel_center.removeAll();
        this.panel_bottom.removeAll();

        // Update deck
        if ((game.getState() == GameState.WILD) && (game.isActive())) {
            ColorPanel panel_color = new ColorPanel(client, game);
            this.panel_center.add(panel_color);
        } else {
            DeckPanel panel_deck = new DeckPanel(this, client, game);
            this.panel_center.add(panel_deck);
        }

        // Update players preview
        if (!(panel_players == null)) {
            this.panel_top.remove(panel_players);
        }

        this.panel_players = new PlayersPanel(this, client, game);
        this.panel_top.add(panel_players, BorderLayout.WEST);

        // Update player hand
        HandPanel panel_hand = new HandPanel(this, client, game);

        JScrollPane scroll_pane_hand = new JScrollPane(panel_hand);
        scroll_pane_hand.setBorder(null);
        scroll_pane_hand.setPreferredSize(new Dimension(740, 180));

        this.panel_bottom.add(scroll_pane_hand);

        // Update buttons
        boolean visible = ((game.isActive()) && (!(game.getRank() == null || game.getState().equals(GameState.WILD))));
        this.button_end.setVisible(visible);

        // Render
        this.revalidate();
        this.repaint();
    }

    public void flash(String message) {
        JLabel label = new JLabel();
        label.setOpaque(true);

        label.setText(message);
        label.setFont(new Font("Arial", Font.PLAIN,42));
        label.setForeground(Color.WHITE);

        JPanel panel_flash = new JPanel();
        panel_flash.add(label);

        this.panel_top.add(panel_flash, BorderLayout.SOUTH);

        final int steps = 25;

        Thread thread = new Thread(() -> {
            final Timer timer_in = new Timer(50, null);
            final Timer timer_out = new Timer(50, null);

            timer_in.addActionListener(new ActionListener() {
                int count = 0;

                public void actionPerformed(ActionEvent e) {
                    if (count <= steps) {
                        float intensity = (1.0f - count / (float) steps);
                        label.setForeground(new Color(intensity, intensity, intensity));
                        count++;
                    } else {
                        timer_in.stop();
                    }
                }
            });

            timer_out.addActionListener(new ActionListener() {
                int count = 0;

                public void actionPerformed(ActionEvent e) {
                    if (count <= steps) {
                        float intensity = (count / (float) steps);
                        label.setForeground(new Color(intensity, intensity, intensity));
                        count++;
                    } else {
                        label.setVisible(false);
                        timer_out.stop();
                    }
                }
            });

            timer_in.start();

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            timer_out.start();
        });

        thread.start();
    }
}
