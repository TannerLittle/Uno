package com.tannerlittle.uno.view;

import com.tannerlittle.uno.UnoGame;
import com.tannerlittle.uno.enums.GameState;
import com.tannerlittle.uno.network.UnoClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GameFrame extends JFrame {

    private UnoClient client;
    private UnoGame game;

    private boolean initialized;

    public GameFrame(UnoClient client, UnoGame game) throws HeadlessException {
        this.client = client;
        this.game = game;

        int width = 500;
        int height = 400;

        this.setSize(width, height);

        this.setTitle("Uno | Player: " + game.getPlayer().getName());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setUndecorated(true);
    }

    public void update() {
        this.initialized = true;

        this.getContentPane().removeAll();

        JPanel panel_top = new JPanel(new BorderLayout());
        this.getContentPane().add(panel_top, BorderLayout.NORTH);

        JPanel panel_buttons = new JPanel();
        panel_buttons.setLayout(new BoxLayout(panel_buttons, BoxLayout.Y_AXIS));

        JPanel panel_buttons_right = new JPanel();
        panel_buttons_right.setLayout(new FlowLayout(FlowLayout.RIGHT));
        panel_buttons_right.add(panel_buttons);

        panel_top.add(panel_buttons_right, BorderLayout.EAST);

        if (game.isActive()) {
            if (!(game.getRank() == null || game.getState().equals(GameState.WILD))) {
                JButton button_end = new JButton("End Turn");
                button_end.setPreferredSize(new Dimension(200, 75));
                button_end.addActionListener(event -> {
                    this.client.sendCommand("ROTATE " + game.getPlayer().getUniqueId());
                });

                JPanel panel_button_end = new JPanel();
                panel_button_end.add(button_end);
                panel_buttons.add(panel_button_end, BorderLayout.EAST);
            }
        }

        UnoButtonPanel panel_button_uno = new UnoButtonPanel();
        panel_button_uno.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                GameFrame.this.client.sendCommand("UNO " + game.getPlayer().getUniqueId());
            }
        });

        panel_buttons.add(panel_button_uno, BorderLayout.EAST);

        HandPanel panel_hand = new HandPanel(this, client, game);

        JScrollPane scroll_pane_hand = new JScrollPane(panel_hand);
        scroll_pane_hand.setBorder(null);
        scroll_pane_hand.setPreferredSize(new Dimension(740, 180));

        this.getContentPane().add(scroll_pane_hand, BorderLayout.SOUTH);

        if ((game.getState() == GameState.WILD) && (game.isActive())) {
            ColorPanel panel_color = new ColorPanel(client, game);
            this.getContentPane().add(panel_color, BorderLayout.CENTER);
        } else {
            DeckPanel panel_deck = new DeckPanel(this, client, game);
            this.getContentPane().add(panel_deck, BorderLayout.CENTER);
        }

        PlayersPanel panel_players = new PlayersPanel(this, client, game);
        panel_top.add(panel_players, BorderLayout.WEST);

        this.revalidate();
        this.repaint();
    }

    public boolean isInitialized() {
        return initialized;
    }
}
