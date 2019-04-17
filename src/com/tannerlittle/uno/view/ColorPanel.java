package com.tannerlittle.uno.view;

import com.tannerlittle.uno.UnoGame;
import com.tannerlittle.uno.enums.Rank;
import com.tannerlittle.uno.enums.Suit;
import com.tannerlittle.uno.model.Card;
import com.tannerlittle.uno.model.Player;
import com.tannerlittle.uno.network.UnoClient;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ColorPanel extends JPanel {

    private UnoClient client;
    private UnoGame game;

    public ColorPanel(UnoClient client, UnoGame game) {
        this.client = client;
        this.game = game;

        for (Suit suit : Suit.values()) {
            if (suit.equals(Suit.WILD)) continue;

            Border border = BorderFactory.createEtchedBorder(WHEN_FOCUSED, Color.WHITE, suit.getColor());
            Border focused = BorderFactory.createEtchedBorder(WHEN_FOCUSED, Color.WHITE, Color.WHITE);

            Card card = new Card(suit, Rank.WILD);
            CardPanel panel = new CardPanel(card, false);

            panel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent event) {
                    panel.setBorder(focused);
                }

                @Override
                public void mouseExited(MouseEvent event) {
                    panel.setBorder(border);
                }

                @Override
                public void mouseClicked(MouseEvent event) {
                    Player player = game.getPlayer();

                    if (game.isActive()) {
                        ColorPanel.this.client.sendCommand("WILD " + player.getUniqueId() + " " + suit.name());
                    }
                }
            });

            this.add(panel, BorderLayout.CENTER);
        }
    }
}
