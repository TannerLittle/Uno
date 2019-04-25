package com.tannerlittle.uno;

import com.tannerlittle.uno.enums.*;
import com.tannerlittle.uno.model.Card;
import com.tannerlittle.uno.model.Deck;
import com.tannerlittle.uno.model.Discards;
import com.tannerlittle.uno.model.Player;
import com.tannerlittle.uno.network.UnoClient;
import com.tannerlittle.uno.view.GameFrame;

import java.util.*;

public class UnoGame {

    private UUID player;

    private GameState state;

    private Map<UUID, Player> players;
    private UUID active;

    private Rotation rotation;

    private Deck deck;
    private Discards discards;

    private Rank rank;

    private GameFrame frame;

    public UnoGame(Player player) {
        this.player = player.getUniqueId();

        this.state = GameState.SETUP;

        this.players = new HashMap<>();
        this.active = null;

        this.rotation = Rotation.CLOCKWISE;

        this.deck = new Deck();
        this.discards = new Discards();

        this.addPlayer(player);
    }

    public void addPlayer(Player player) {
        if (players.containsKey(player.getUniqueId())) return;

        this.players.put(player.getUniqueId(), player);
    }

    public void start(UnoClient client) {
        this.state = GameState.RUNNING;
        this.frame = new GameFrame(client, this);
    }

    public Player getPlayer() {
        return players.get(player);
    }

    public boolean isActive() {
        return this.isPlayer(active);
    }

    public boolean isPlayer(UUID id) {
        if (player == null) return false;

        return player.equals(id);
    }

    public Deck getDeck() {
        return deck;
    }

    public Discards getDiscards() {
        return discards;
    }

    public Collection<Player> getPlayers() {
        return players.values();
    }

    public GameState getState() {
        return state;
    }

    public UUID getActive() {
        return active;
    }

    public void setActive(UUID active) {
        this.active = active;
    }

    public Player getPlayer(UUID id) {
        return players.get(id);
    }

    public Rank getRank() {
        return rank;
    }

    public void setRank(Rank rank) {
        this.rank = rank;
    }

    public GameFrame getFrame() {
        return frame;
    }

    public boolean playCard(Player player, Card card) {
        UUID id = player.getUniqueId();

        if (!(player.getUniqueId().equals(active))) {
            this.flash(id, Message.ERROR_NOT_ACTIVE.getMessage());
            return false;
        }

        if ((state.equals(GameState.WILD)) && (!(card.isSimilar(discards.peek())))) {
            this.flash(id, Message.ERROR_WILD.getMessage());
            return false;
        }

        if (!((rank == null) || (card.getRank().equals(rank)))) {
            this.flash(id, Message.ERROR_INVALID_CARD.getMessage());
            return false;
        }

        if (!(checkCard(card))) {
            this.flash(id, Message.ERROR_INVALID_CARD.getMessage());
            return false;
        }


        player.getHand().removeElement(card);

        this.discards.push(card);
        this.rank = card.getRank();

        if (player.getHand().size() == 0) {
            this.frame.flash(Message.SUCCESS_WIN.getMessage(player.getName()));
            System.exit(0);
        }

        if (card.getSuit() == Suit.WILD) {
            this.state = GameState.WILD;
        }

        player.setUno(false);

        return true;
    }

    public boolean setWild(Player player, Suit suit) {
        if (!(player.getUniqueId().equals(active))) {
            if (isPlayer(player.getUniqueId())) this.frame.flash(Message.ERROR_NOT_ACTIVE.getMessage());
            return false;
        }

        if (!(state.equals(GameState.WILD))) {
            if (isPlayer(player.getUniqueId())) this.frame.flash(Message.ERROR_NOT_WILD.getMessage());
            return false;
        }

        if (suit.equals(Suit.WILD)) {
            return false;
        }

        this.discards.peek().setSuit(suit);

        this.state = GameState.RUNNING;
        return true;
    }

    public Card pickupCard(Player player) {
        UUID id = player.getUniqueId();

        if (!(id.equals(active))) {
            if (isPlayer(player.getUniqueId())) this.frame.flash(Message.ERROR_NOT_ACTIVE.getMessage());
            return null;
        }

        if (state.equals(GameState.WILD)) {
            return null;
        }

        if (!(rank == null)) {
            this.flash(id, "You cannot pick because you've already played!");
            return null;
        }

        for (Card card : player.getHand()) {
            if (checkCard(card)) {
                this.flash(id, "You cannot pickup because you can play a card!");
                return null;
            }
        }

        Card card = deck.pop();

        player.getHand().push(card);
        return card;
    }

    public void draw(UUID id, int count) {
        Player player = players.get(id);

        Rank rank = (count == 2 ? Rank.DRAW_TWO : (count == 4) ? Rank.WILD_DRAW_FOUR : null);

        if (!(rank == null)) {
            int i = 1;

            while ((discards.size() > i) && (discards.get(discards.size() - i).getRank().equals(rank))) {
                i++;
            }

            count = ((i - 1) * count);
        }

        for (int i = 0; i < count; i++) {
            player.getHand().push(deck.pop());
        }
    }

    public boolean callUno(UUID id) {
        Player player = players.get(id);

        if (player.getHand().size() == 1) {
            if (player.getUno()) {
                this.flash(id, "You have already called Uno!");
                return false;
            }

            this.flash(id, "You called Uno!");

            player.setUno(true);
            return true;
        }

        for (Player target : players.values()) {
            if ((target.getHand().size() == 1) && (!(target.getUno()))) {
                if (isPlayer(id)) {
                    this.flash(id, "You called " + target.getName() + " for not calling Uno!");
                } else {
                    this.frame.flash(player.getName() + " called " + target.getName() + " for not calling Uno!");
                }

                for (int i = 0; i < 2; i++) {
                    target.getHand().add(deck.pop());
                }

                return false;
            }
        }

        this.flash(id,"You must pick up 2 for calling Uno! with more than one card!");

        if (isPlayer(id)) {
            for (int i = 0; i < 2; i++) {
                player.getHand().add(deck.pop());
            }
        }

        return false;
    }

    private boolean checkCard(Card card) {
        Card discard = discards.peek();

        if (card.getSuit() == Suit.WILD) return true;
        if (discard.getSuit() == Suit.WILD) return true;

        if (card.getSuit().equals(discard.getSuit())) return true;
        if (card.getRank().equals(discard.getRank())) return true;

        return false;
    }

    private void flash(UUID id, String message) {
        if (active.equals(id)) {
            this.frame.flash(message);
        }
    }
}
