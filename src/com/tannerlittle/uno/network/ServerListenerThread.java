package com.tannerlittle.uno.network;

import com.tannerlittle.uno.UnoGame;
import com.tannerlittle.uno.enums.GameState;
import com.tannerlittle.uno.enums.Rank;
import com.tannerlittle.uno.enums.Suit;
import com.tannerlittle.uno.model.Card;
import com.tannerlittle.uno.model.Discards;
import com.tannerlittle.uno.model.Player;

import java.net.Socket;
import java.util.UUID;

// Runs on Uno Clients
// Accepts commands from server

public class ServerListenerThread extends ListenerThread {

    private UnoClient client;
    private UnoGame game;

    public ServerListenerThread(Socket socket, UnoClient client, UnoGame game) {
        super(socket);

        this.client = client;
        this.game = game;
    }

    public void onCommand(String input, String command, String content) {
        if (command.equals("DECK")) {
            this.game.getDeck().parse(content);
        }

        if (command.equals("DISCARDS")) {
            this.game.getDiscards().parse(content);
        }

        if (command.equals("HAND")) {
            String[] args = content.split("\\s+");
            UUID id = UUID.fromString(args[0]);
            String cards = content.substring(args[0].length() + 1);

            this.game.getPlayer(id).getHand().parse(cards);
        }

        if (command.equals("START")) {
            this.game.start(client);
        }

        if (command.equals("PLAYER")) {
            String[] args = content.split("\\s+");
            UUID id = UUID.fromString(args[0]);
            String name = content.substring(args[0].length() + 1);

            Player player = new Player(id, name);
            this.game.addPlayer(player);

            System.out.println("Player " + name + " has joined the server (" + id + ").");
        }

        if (command.equals("PLAY")) {
            String[] args = content.split("\\s+");

            UUID id = UUID.fromString(args[0]);
            Suit suit = Suit.valueOf(args[1]);
            Rank rank = Rank.valueOf(args[2]);

            Player player = game.getPlayer(id);
            Card card = new Card(suit, rank);

            boolean success = this.game.playCard(player, card);
            if (!(success)) return;

            if (game.isPlayer(id)) {
                switch (rank) {
                    case REVERSE:
                        this.sendCommand("REVERSE " + player.getUniqueId());
                        break;
                    case SKIP:
                        this.sendCommand("SKIP " + player.getUniqueId());
                        break;
                    case DRAW_TWO:
                        this.sendCommand("DRAW 2");
                        break;
                    case WILD_DRAW_FOUR:
                        this.sendCommand("DRAW 4");
                        break;
                    default:
                        break;
                }

                if (!(game.getState() == GameState.WILD)) {
                    boolean rotate = true;

                    for (Card hand : player.getHand()) {
                        if ((rank.equals(Rank.SKIP)) || (hand.getRank().equals(card.getRank()))) {
                            rotate = false;
                        }
                    }

                    rotate = (rank.equals(Rank.DRAW_TWO) || rank.equals(Rank.WILD_DRAW_FOUR)) || rotate;

                    if (rotate) this.sendCommand("ROTATE " + player.getUniqueId());
                }
            }
        }

        if (command.equals("PICKUP")) {
            String[] args = content.split("\\s+");

            UUID id = UUID.fromString(args[0]);

            Player player = game.getPlayer(id);

            Card card = this.game.pickupCard(player);
            if (card == null) return;

            Card discard = game.getDiscards().peek();

            if (!((card.isSimilar(discard)) || (card.getSuit().equals(Suit.WILD)))) {
                this.sendCommand("ROTATE " + player.getUniqueId());
            }
        }

        if (command.equals("WILD")) {
            String[] args = content.split("\\s+");

            UUID id = UUID.fromString(args[0]);
            Suit suit = Suit.valueOf(args[1]);

            Player player = game.getPlayer(id);

            this.game.setWild(player, suit);
            this.sendCommand("ROTATE " + player.getUniqueId());
        }

        if (command.equals("ACTIVE")) {
            UUID id = UUID.fromString(content);

            this.game.setActive(id);
            this.game.setRank(null);

            if ((game.isPlayer(id)) && (!(game.getFrame() == null))) {
                this.game.getFrame().flash("It is your turn to play!");
            }
        }

        if (command.equals("DRAW")) {
            String[] args = content.split("\\s+");

            UUID id = UUID.fromString(args[0]);
            int count = Integer.parseInt(args[1]);

            this.game.draw(id, count);
        }

        if (command.equals("FLASH")) {
            String[] args = content.split("\\s+");

            if (args[0].equals("ALL") || game.isPlayer(UUID.fromString(args[0]))) {
                game.getFrame().flash(content.substring(args[0].length() + 1));
            }
        }

        if (command.equals("UNO")) {
            String[] args = content.split("\\s+");

            UUID id = UUID.fromString(args[0]);

            this.game.callUno(id);
        }

        if (!(game.getFrame() == null)) game.getFrame().update();
    }
}