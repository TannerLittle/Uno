package com.tannerlittle.uno.view;

import com.tannerlittle.uno.UnoGame;
import com.tannerlittle.uno.enums.GameState;
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

        this.setOpaque(false);

        this.initialize();
    }

    private void initialize() {
        Hand hand = game.getPlayer().getHand();


        boolean visible = ((game.isActive()) && (!(game.getRank() == null || game.getState().equals(GameState.WILD))));

        HiddenCardPanel button = new HiddenCardPanel(100, 150, "End", true);

        button.setVisible(visible);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                HandPanel.this.client.sendCommand("ROTATE " + game.getPlayer().getUniqueId());
            }
        });

        this.add(button);

        for (Card card : hand) {
            boolean faded = ((!((game.getRank() == null) || (card.getRank().equals(game.getRank())))) && game.isActive());

            CardPanel panel = new CardPanel(card, faded);

            panel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    Player player = game.getPlayer();

                    if (game.isActive()) {
                        client.sendCommand("PLAY " + player.getUniqueId() + " " + card.toString());
                    } else {
                        game.getFrame().flash("It is not your turn to play!");
                    }
                }
            });

            this.add(panel);
        }
    }
}
