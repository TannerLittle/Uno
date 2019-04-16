package com.tannerlittle.uno.view;

import com.tannerlittle.uno.UnoGame;
import com.tannerlittle.uno.model.Player;
import com.tannerlittle.uno.network.UnoClient;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DeckPanel extends JPanel {

    private GameFrame frame;
    private UnoClient client;
    private UnoGame game;

    public DeckPanel(GameFrame frame, UnoClient client, UnoGame game) {
        this.frame = frame;
        this.client = client;
        this.game = game;

        this.initialize();
    }

    private void initialize() {
        CardPanel panel_discards = new CardPanel(game.getDiscards().peek(), false);

        HiddenCardPanel panel_deck = new HiddenCardPanel(100, 150, "Uno", true);
        panel_deck.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Player player = game.getPlayer();

                if (game.isActive()) {
                    client.sendCommand("PICKUP " + player.getUniqueId());
                }
            }
        });

        this.add(panel_discards);
        this.add(panel_deck);
    }
}
