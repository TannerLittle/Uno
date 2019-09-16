package com.tannerlittle.uno.view;

import com.tannerlittle.uno.UnoGame;
import com.tannerlittle.uno.enums.GameState;
import com.tannerlittle.uno.network.UnoClient;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class GameFrame extends JFrame {

    private UnoClient client;
    private UnoGame game;

    private JPanel panel_top;
    private JPanel panel_center;
    private JPanel panel_bottom;

    private JPanel panel_players;
    private JPanel panel_flash;

    public GameFrame(UnoClient client, UnoGame game) throws HeadlessException {
        this.client = client;
        this.game = game;

        // Initialize background panel
        BufferedImage image = null;

        try {
            image = ImageIO.read(getClass().getResource("/images/gaming-pattern.png"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        BackgroundPanel panel = new BackgroundPanel(image, BackgroundPanel.TILED, 1.0f, 0.5f);
        panel.setPaint(null);
        this.getContentPane().add(panel);

        // Initialize top panel
        this.panel_top = new JPanel(new BorderLayout());
        this.panel_top.setOpaque(false);
        panel.add(panel_top, BorderLayout.NORTH);

        // Initialize center panel
        this.panel_center = new JPanel();
        this.panel_center.setOpaque(false);
        panel.add(panel_center, BorderLayout.CENTER);

        // Initialize bottom panel
        this.panel_bottom = new JPanel();
        this.panel_bottom.setOpaque(false);
        panel.add(panel_bottom, BorderLayout.SOUTH);

        // Initialize flash
        this.panel_flash = new JPanel();
        this.panel_flash.setOpaque(false);
        this.panel_flash.setPreferredSize(new Dimension(panel_flash.getWidth(), 62));
        this.panel_top.add(panel_flash, BorderLayout.SOUTH);

        // Initialize frame
        int width = 500;
        int height = 400;

        this.setSize(width, height);

        this.setTitle("Uno | Player: " + game.getPlayer().getName());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        // Makes window truly full-screen
        //this.setUndecorated(true);

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
        panel_hand.setOpaque(false);

        JScrollPane scroll_pane_hand = new JScrollPane(panel_hand);
        scroll_pane_hand.setOpaque(false);
        scroll_pane_hand.getViewport().setOpaque(false);
        scroll_pane_hand.setBorder(null);
        scroll_pane_hand.setPreferredSize(new Dimension(740, 200));
        scroll_pane_hand.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 40));

        this.panel_bottom.add(scroll_pane_hand);

        // Render
        this.revalidate();
        this.repaint();
    }

    public void flash(String message) {
        JLabel label = new JLabel();

        label.setText(message);
        label.setFont(new Font("SansSerif", Font.PLAIN,42));
        label.setOpaque(false);
        label.setVisible(false);

        this.panel_flash.removeAll();
        this.panel_flash.add(label);

        final int steps = 25;

        Thread thread = new Thread(() -> {
            final Timer timer_in = new Timer(50, null);
            final Timer timer_out = new Timer(50, null);

            timer_in.addActionListener(new ActionListener() {
                int count = 0;

                public void actionPerformed(ActionEvent e) {
                    if (count <= steps) {
                        float intensity = (count++ / (float) steps);

                        label.setVisible(true);
                        label.setForeground(new Color(0, 0, 0, intensity));
                    } else {
                        timer_in.stop();
                    }
                }
            });

            timer_out.addActionListener(new ActionListener() {
                int count = 10;

                public void actionPerformed(ActionEvent e) {
                    if (count <= steps) {
                        float intensity = (1.0f - count++ / (float) steps);

                        label.setForeground(new Color(0, 0, 0, intensity));
                    } else {
                        timer_out.stop();

                        label.setVisible(false);

                        GameFrame.this.revalidate();
                        GameFrame.this.repaint();
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

        // Render
        this.revalidate();
        this.repaint();
    }
}
