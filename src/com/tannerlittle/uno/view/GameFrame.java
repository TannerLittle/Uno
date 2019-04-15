package com.tannerlittle.uno.view;

import com.tannerlittle.uno.UnoGame;
import com.tannerlittle.uno.network.UnoClient;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GameFrame extends JFrame {

    private UnoClient client;
    private UnoGame game;

    private JScrollPane scroll;
    private HandPanel hand;
    private DeckPanel deck;
    private PlayersPanel players;

    private JPanel top;

    private List<JButton> buttons = new ArrayList<>();

    public GameFrame(UnoClient client, UnoGame game) throws HeadlessException {
        this.client = client;
        this.game = game;

        int width = 500;
        int height = 400;

        this.setSize(width, height);

        this.setTitle("Uno | Player: " + game.getPlayer().getName());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void addButtons() {
        this.buttons.clear();

        JButton end = new JButton("End Turn");
        end.addActionListener(event -> {
            this.client.sendCommand("ROTATE " + game.getPlayer().getUniqueId());
        });

        JButton uno = new JButton("Uno!");
        uno.setPreferredSize(new Dimension(150, 50));
        uno.addActionListener(event -> {
            this.client.sendCommand("UNO " + game.getPlayer().getUniqueId());
        });

        JPanel group = new JPanel();
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        panel.add(group);

        this.top.add(panel, BorderLayout.EAST);

        this.buttons.add(end);

        for (JButton button : buttons) {
            if ((game.isActive()) && (game.getRank() != null)) {
                group.add(button, BorderLayout.EAST);
            }
        }
    }

    public void update(boolean pack) {
        this.getContentPane().removeAll();

        this.top = new JPanel(new BorderLayout());
        this.getContentPane().add(top, BorderLayout.NORTH);

        this.addButtons();

        this.hand = new HandPanel(this, client, game);
        this.scroll = new JScrollPane(hand);
        this.scroll.setBorder(null);
        this.scroll.setPreferredSize(new Dimension(740, 180));
        this.getContentPane().add(scroll, BorderLayout.SOUTH);

        this.deck = new DeckPanel(this, client, game);
        this.getContentPane().add(deck, BorderLayout.CENTER);

        this.players = new PlayersPanel(this, client, game);
        this.top.add(players, BorderLayout.WEST);

        this.revalidate();
        this.repaint();

        if (pack) this.pack();
    }

    public boolean isInitialized() {
        return (hand != null) && (deck != null);
    }
}
