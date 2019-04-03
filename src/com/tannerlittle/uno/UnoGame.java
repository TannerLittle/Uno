package com.tannerlittle.uno;

import com.tannerlittle.uno.enums.*;
import com.tannerlittle.uno.model.Card;
import com.tannerlittle.uno.model.Deck;
import com.tannerlittle.uno.model.Discards;
import com.tannerlittle.uno.model.Player;
import com.tannerlittle.uno.network.UnoClient;
import com.tannerlittle.uno.view.ColorFrame;
import com.tannerlittle.uno.view.GameFrame;

import java.util.*;

public class UnoGame {

    private UnoClient client;
    private UUID player;

    private GameState state;

    private Map<UUID, Player> players;

    private List<UUID> cycle;
    private int active;

    private UUID host;

    private GameFrame frame;

    private Rotation rotation;

    private Deck deck;
    private Discards discards;

    public UnoGame() {
        this(null, null);
    }

    public UnoGame(UnoClient client, String name) {
        this.client = client;

        this.state = GameState.SETUP;

        this.players = new HashMap<>();

        this.cycle = new LinkedList<>();
        this.active = 0;

        this.rotation = Rotation.CLOCKWISE;

        this.deck = new Deck();
        this.discards = new Discards();

        if (!(name == null)) {
            Player player = new Player(UUID.randomUUID(), name);

            this.player = player.getUniqueId();
            this.addPlayer(player);
        }
    }

    public void addPlayer(Player player) {
        this.players.put(player.getUniqueId(), player);
        this.cycle.add(player.getUniqueId());

        // First player to join is the host
        if (host == null) {
            this.host = player.getUniqueId();
        }

        // Second player to join is active
        if ((active == 0) && (cycle.size() > 1)) {
            this.active = 1;
        }
    }

    public void deal() {
        for (Player player : players.values()) {
            for (int i = 0; i < 7; i++) {
                Card card = deck.pop();
                player.getHand().push(card);
            }
        }

        Card card = deck.pop();
        this.discards.push(card);
    }

    public void start() {
        this.state = GameState.RUNNING;
        this.frame = new GameFrame(this);
    }

    public UnoClient getClient() {
        return client;
    }

    public Player getPlayer() {
        return players.get(player);
    }

    public boolean isPlayer(UUID id) {
        if (player == null) return false;

        return player.equals(id);
    }

    public GameState getState() {
        return state;
    }

    public int getActive() {
        return active;
    }

    public UUID getHost() {
        return host;
    }

    public GameFrame getFrame() {
        return frame;
    }

    public Rotation getRotation() {
        return rotation;
    }

    public Deck getDeck() {
        return deck;
    }

    public Discards getDiscards() {
        return discards;
    }

    public Player getActivePlayer() {
        UUID id = cycle.get(active);
        return players.get(id);
    }

    public Collection<Player> getPlayers() {
        return players.values();
    }

    public Player getPlayer(UUID id) {
        return players.get(id);
    }

    public boolean playCard(Player player, Card card) {
        if (!(player.equals(getActivePlayer()))) {
            if (isPlayer(player.getUniqueId())) player.sendMessage(Message.ERROR_NOT_ACTIVE.getMessage());
            return false;
        }

        if (state.equals(GameState.WILD)) {
            if (isPlayer(player.getUniqueId())) player.sendMessage(Message.ERROR_WILD.getMessage());
            return false;
        }

        if (!(checkCard(card))) {
            if (isPlayer(player.getUniqueId())) player.sendMessage(Message.ERROR_INVALID_CARD.getMessage());
            return false;
        }

        player.getHand().removeElement(card);

        this.discards.push(card);

        if (card.getSuit().equals(Suit.WILD)) {
            this.state = GameState.WILD;
        }

        Rank rank = card.getRank();

        switch (rank) {
            case REVERSE:
                this.rotation = Rotation.getReverse(rotation);
                break;
            case SKIP:
                this.rotate();
                break;
            case DRAW_TWO:
                int i = 1;
                while ((discards.size() >= i) && (discards.get(discards.size() - i).getRank() == Rank.DRAW_TWO)) i++;

                this.pickupCards((i - 1) * 2);
                break;
            case WILD_DRAW_FOUR:
                int j = 1;
                while ((discards.size() >= j) && (discards.get(discards.size() - j).getRank() == Rank.WILD_DRAW_FOUR)) j++;

                this.pickupCards((j - 1) * 4);
                break;
            default:
                break;
        }

        if (player.getHand().size() == 0) {
            for (Player p : players.values()) p.sendMessage(Message.SUCCESS_WIN.getMessage(player.getName()));
            System.exit(0);
        }

        if (isPlayer(player.getUniqueId())) {
            if (card.getSuit() == Suit.WILD) {
                ColorFrame color = new ColorFrame(frame, this);
                color.setVisible(true);
            }
        }

        this.rotate();
        return true;
    }

    public boolean setWild(Player player, Suit suit) {
        if (!(player.equals(getActivePlayer()))) {
            if (isPlayer(player.getUniqueId())) player.sendMessage(Message.ERROR_NOT_ACTIVE.getMessage());
            return false;
        }

        if (!(state.equals(GameState.WILD))) {
            if (isPlayer(player.getUniqueId())) player.sendMessage(Message.ERROR_NOT_WILD.getMessage());
            return false;
        }

        if (suit.equals(Suit.WILD)) {
            return false;
        }

        this.discards.peek().setSuit(suit);

        this.state = GameState.RUNNING;
        return true;
    }

    public boolean pickupCard(Player player) {
        if (!(player.equals(getActivePlayer()))) {
            if (isPlayer(player.getUniqueId())) player.sendMessage("It is not your turn to go!");
            return false;
        }

        if (state.equals(GameState.WILD)) {
            return false;
        }

        for (Card card : player.getHand()) {
            if (checkCard(card)) {
                if (isPlayer(player.getUniqueId())) player.sendMessage("You cannot pickup because you can play your " + card.toString());
                return false;
            }
        }

        Card card = deck.pop();

        player.getHand().push(card);

        this.rotate();
        return true;
    }

    private void pickupCards(int cards) {
        Player player = this.getNextPlayer();

        for (int i = 0; i < cards; i++) {
            player.getHand().push(deck.pop());
        }
    }

    private boolean checkCard(Card card) {
        Card discard = discards.peek();

        if (card.getSuit() == Suit.WILD) return true;
        if (discard.getSuit() == Suit.WILD) return true;

        if (card.getSuit().equals(discard.getSuit())) return true;
        if (card.getRank().equals(discard.getRank())) return true;

        return false;
    }

    private void rotate() {
        switch (rotation) {
            case CLOCKWISE:
                this.active = (active == cycle.size() - 1 ? 0 : active + 1);
                break;
            case COUNTER_CLOCKWISE:
                this.active = (active == 0 ? cycle.size() - 1 : active - 1);
                break;
            default:
                break;
        }
    }

    private Player getNextPlayer() {
        UUID id;

        switch (rotation) {
            case CLOCKWISE:
                id = cycle.get(active == cycle.size() - 1 ? 0 : active + 2);
                break;
            case COUNTER_CLOCKWISE:
                id = cycle.get(active == 0 ? cycle.size() - 2 : active - 2);
                break;
            default:
                return null;
        }

        return players.get(id);
    }
}
