package com.tannerlittle.uno.view;

import com.tannerlittle.uno.UnoGame;
import com.tannerlittle.uno.network.UnoClient;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {

    private UnoGame game;

    private HandPanel hand;
    private DeckPanel deck;

    public GameFrame(UnoGame game) throws HeadlessException {
        this.game = game;

        this.initialize();
    }

    private void initialize() {
        int width = 500;
        int height = 400;

        this.setSize(width, height);

        this.setTitle("Uno | Player: " + game.getPlayer().getName());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.update();

        this.setVisible(true);
    }

    public void update() {
        if (!(hand == null)) this.getContentPane().remove(hand);
        if (!(deck == null)) this.getContentPane().remove(deck);

        this.hand = new HandPanel(this, game);
        this.getContentPane().add(hand, BorderLayout.SOUTH);

        this.deck = new DeckPanel(this, game);
        this.getContentPane().add(deck, BorderLayout.CENTER);

        this.revalidate();
        this.repaint();
        this.pack();
    }
}
