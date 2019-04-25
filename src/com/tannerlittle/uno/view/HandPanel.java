package com.tannerlittle.uno.view;

import com.tannerlittle.uno.UnoGame;
import com.tannerlittle.uno.model.Card;
import com.tannerlittle.uno.model.Hand;
import com.tannerlittle.uno.model.Player;
import com.tannerlittle.uno.network.UnoClient;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class HandPanel extends JPanel {

    private GameFrame frame;
    private UnoClient client;
    private UnoGame game;

    public HandPanel(GameFrame frame, UnoClient client, UnoGame game) {
        this.frame = frame;
        this.client = client;
        this.game = game;

        this.initialize();
    }

    private void initialize() {
        Hand hand = game.getPlayer().getHand();

        for (Card card : hand) {
            boolean faded = ((!((game.getRank() == null) || (card.getRank().equals(game.getRank())))) && game.isActive());

            CardPanel panel = new CardPanel(card, faded);

            panel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    Player player = game.getPlayer();

                    if (game.isActive()) {
                        client.sendCommand("PLAY " + player.getUniqueId() + " " + card.toString());
                    }
                }
            });

            this.add(panel);
        }
    }
}
