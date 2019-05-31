package com.tannerlittle.uno.view;

import com.tannerlittle.uno.UnoGame;
import com.tannerlittle.uno.enums.Suit;
import com.tannerlittle.uno.model.Player;
import com.tannerlittle.uno.network.UnoClient;

import javax.swing.*;
import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class PlayersPanel extends JPanel {

    private GameFrame frame;
    private UnoClient client;
    private UnoGame game;

    public PlayersPanel(GameFrame frame, UnoClient client, UnoGame game) {
        this.frame = frame;
        this.client = client;
        this.game = game;

        this.setOpaque(false);

        this.initialize();
    }

    private void initialize() {
        this.setLayout(new FlowLayout(FlowLayout.LEFT));

        List<Player> players = game.getPlayers().stream().sorted(Comparator.comparing(Player::getName)).collect(Collectors.toList());

        for (Player player : players) {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

            HiddenCardPanel card = new HiddenCardPanel(50, 75, Integer.toString(player.getHand().size()), false);

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
