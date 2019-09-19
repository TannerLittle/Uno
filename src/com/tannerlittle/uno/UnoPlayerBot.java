package com.tannerlittle.uno;

import com.tannerlittle.uno.enums.Rank;
import com.tannerlittle.uno.enums.Suit;
import com.tannerlittle.uno.model.Card;
import com.tannerlittle.uno.model.Hand;
import com.tannerlittle.uno.model.Player;
import com.tannerlittle.uno.network.UnoClient;
import com.tannerlittle.uno.network.UnoServer;

import java.net.InetAddress;
import java.util.Collections;
import java.util.Random;
import java.util.UUID;

public class UnoPlayerBot {

    private UnoServer server;
    private UnoClient client;

    private UnoGame game;

    private Thread thread;
    private Random random;

    public UnoPlayerBot(UnoServer server, UUID id) {
        this.server = server;

        Player player = new Player(id, "Computer #" + (server.getBots().size() + 1));

        this.game = new UnoGame(player, true);

        try {
            InetAddress address = InetAddress.getByName("0.0.0.0");
            int port = server.getPort();

            this.client = new UnoClient(game, address, port);
            this.client.sendCommand(game.getPlayer().getCommand());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        this.random = new Random();
    }

    public void play() {
        this.thread = new Thread() {
            public void run() {
                this.delay();

                Player player = game.getPlayer();
                Card discard = game.getDiscard();

                Collections.shuffle(player.getHand());

                Hand hand = (Hand) player.getHand().clone();
                Card played = null;

                System.out.println(hand.toString());

                for (Card card : hand) {
                    if (card.isSimilar(discard) || (card.getSuit() == Suit.WILD) || (discard.getSuit() == Suit.WILD)) {
                        client.sendCommand("PLAY " + player.getUniqueId() + " " + (played = card).toString());

                        if (card.getSuit().equals(Suit.WILD)) {
                            this.delay();

                            Suit suit;

                            do {
                                suit = Suit.values()[random.nextInt(Suit.values().length)];
                            } while (suit == Suit.WILD);

                            client.sendCommand("WILD " + player.getUniqueId() + " " + suit.name());
                            return;
                        }

                        break;
                    }
                }

                if (played == null) {
                    if (!(game.isActive())) return;

                    UnoPlayerBot.this.client.sendCommand("PICKUP " + player.getUniqueId());
                    UnoPlayerBot.this.play();
                    return;
                }

                hand.remove(played);

                for (Card card : hand) {
                    if (card.getRank().equals(played.getRank())) {
                        if (card.getRank().equals(Rank.SKIP) && game.getPlayers().size() == 2) {
                            client.sendCommand("ROTATE " + game.getPlayer().getUniqueId());
                            return;
                        }

                        this.delay();

                        client.sendCommand("PLAY " + player.getUniqueId() + " " + card.toString());
                    }
                }
            }

            private void delay() {
                try {
                    Thread.sleep(random.nextInt(2) * 1000 + 2000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        };

        this.thread.start();
    }
}
