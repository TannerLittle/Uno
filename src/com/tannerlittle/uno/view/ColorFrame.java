package com.tannerlittle.uno.view;

import com.tannerlittle.uno.UnoGame;
import com.tannerlittle.uno.enums.Suit;
import com.tannerlittle.uno.model.Player;
import com.tannerlittle.uno.network.UnoClient;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;

public class ColorFrame extends JFrame {

    private final Border border = BorderFactory.createEtchedBorder(JPanel.WHEN_FOCUSED, Color.WHITE, Color.GRAY);
    private final Border focused = BorderFactory.createEtchedBorder(JPanel.WHEN_FOCUSED, Color.BLACK, Color.GRAY);

    private GameFrame frame;
    private UnoGame game;

    public ColorFrame(GameFrame frame, UnoGame game) {
        this.frame = frame;
        this.game = game;

        this.initialize();
    }

    private void initialize() {
        this.setTitle("Choose a Colour");
        this.setSize(250, 250);
        this.setPreferredSize(new Dimension(250, 250));
        this.setLayout(new GridLayout(2, 2));

        for (Suit suit : Suit.values()) {
            if (suit.equals(Suit.WILD)) continue;

            JPanel panel = new JPanel();
            panel.setPreferredSize(new Dimension(125,125));
            panel.setBorder(border);

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
                    UnoClient client = game.getClient();
                    Player player = game.getPlayer();

                    client.sendCommand("WILD " + player.getUniqueId() + " " + suit.name());

                    dispatchEvent(new WindowEvent(ColorFrame.this, WindowEvent.WINDOW_CLOSING));
                }
            });

            panel.setBackground(suit.getColor());

            switch (suit) {
                case RED:
                case BLUE:
                    this.add(panel);
                    break;
                case YELLOW:
                case GREEN:
                    this.add(panel);
            }
        }
    }
}
