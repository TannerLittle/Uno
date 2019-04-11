package com.tannerlittle.uno.view;

import com.tannerlittle.uno.UnoGame;
import com.tannerlittle.uno.enums.Suit;
import com.tannerlittle.uno.model.Player;
import com.tannerlittle.uno.network.UnoClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PlayersPanel extends JPanel {

    private GameFrame frame;
    private UnoClient client;
    private UnoGame game;

    public PlayersPanel(GameFrame frame, UnoClient client, UnoGame game) {
        this.frame = frame;
        this.client = client;
        this.game = game;

        this.initialize();
    }

    private void initialize() {
        this.setLayout(new FlowLayout(FlowLayout.LEFT));

        for (Player player : game.getPlayers()) {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBorder(null);

            HiddenCardPanel card = new HiddenCardPanel(50, 75, Integer.toString(player.getHand().size()));

            card.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {

                }
            });

            JLabel label = new JLabel(player.getName());

            if (game.getActive().equals(player.getUniqueId())) {
                label.setBackground(Suit.RED.getColor());

                Font font = label.getFont();
                label.setFont(font.deriveFont(font.getStyle() | Font.BOLD));
            }

            panel.add(label);
            panel.add(card);

            this.add(panel);
        }
    }
}
