package com.tannerlittle.uno.view;

import com.tannerlittle.uno.UnoGame;
import com.tannerlittle.uno.model.Player;
import com.tannerlittle.uno.network.UnoClient;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DeckPanel extends JPanel {

    private GameFrame frame;
    private UnoGame game;

    public DeckPanel(GameFrame frame, UnoGame game) {
        this.frame = frame;
        this.game = game;

        this.initialize();
    }

    private void initialize() {
        CardPanel card = new CardPanel(game.getDiscards().peek());
        this.add(card);

        HiddenCardPanel hidden = new HiddenCardPanel();

        hidden.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                UnoClient client = game.getClient();
                Player player = game.getPlayer();

                client.sendCommand("PICKUP " + player.getUniqueId());
            }
        });

        this.add(hidden);
    }
}
