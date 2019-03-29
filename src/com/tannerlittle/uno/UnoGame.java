package com.tannerlittle.uno;

import com.tannerlittle.uno.enums.GameState;
import com.tannerlittle.uno.enums.Rank;
import com.tannerlittle.uno.enums.Rotation;
import com.tannerlittle.uno.enums.Suit;
import com.tannerlittle.uno.model.Card;
import com.tannerlittle.uno.model.Deck;
import com.tannerlittle.uno.model.Discards;
import com.tannerlittle.uno.model.Player;

import java.util.LinkedList;
import java.util.List;

public class UnoGame {

    private GameThread thread;
    private GameState state;

    private List<Player> players;

    private Rotation rotation;

    private int dealer;
    private int active;

    private Deck deck;
    private Discards discards;

    public UnoGame(List<Player> players) {
        this.thread = new GameThread(this);

        this.state = GameState.RUNNING;

        this.players = new LinkedList<>();
        this.players.addAll(players);

        this.rotation = Rotation.CLOCKWISE;

        this.dealer = 0;
        this.active = (players.size() > 1 ? 1 : 0);

        this.deck = new Deck();
        this.discards = new Discards();

        this.initialize();
    }

    private void initialize() {
        for (Player player : players) {
            for (int i = 0; i < 7; i++) {
                Card card = deck.pop();
                player.getHand().push(card);
            }
        }

        Card card = deck.pop();
        this.discards.push(card);

        this.thread.start();
    }

    public GameState getState() {
        return state;
    }

    public Rotation getRotation() {
        return rotation;
    }

    public Player getDealer() {
        return this.players.get(dealer);
    }

    public Player getActive() {
        return this.players.get(active);
    }

    public Deck getDeck() {
        return deck;
    }

    public Discards getDiscards() {
        return discards;
    }

    public boolean playCard(Player player, Card card) {
        if (!(player.equals(getActive()))) {
            player.sendMessage("It is not your turn to go!");
            return false;
        }

        if (!(checkCard(card))) {
            player.sendMessage("You cannot play that card.");
            return false;
        }

        player.getHand().remove(card);

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
                this.pickupCards(2);
                break;
            case WILD_DRAW_FOUR:
                this.pickupCards(4);
                break;
            default:
                break;
        }

        if (player.getHand().size() == 0) {
            //TODO:
            System.out.println(player.getName() + " Wins!");
            System.exit(0);
        }

        this.rotate();
        return true;
    }

    public boolean setWild(Player player, Suit suit) {
        if (!(player.equals(getActive()))) {
            player.sendMessage("It is not your turn to go!");
            return false;
        }

        if (suit.equals(Suit.WILD)) {
            player.sendMessage("You cannot choose that suit!");
            return false;
        }

        this.discards.peek().setSuit(suit);

        this.state = GameState.RUNNING;
        return true;
    }

    public boolean pickupCard(Player player) {
        if (!(player.equals(getActive()))) {
            player.sendMessage("It is not your turn to go!");
            return false;
        }

        for (Card card : player.getHand()) {
            if (checkCard(card)) {
                player.sendMessage("You cannot pickup because you can");
                player.sendMessage("play your " + card.getSuit() + " " + card.getRank() + "!");
                return false;
            }
        }

        player.getHand().push(discards.pop());

        this.rotate();
        return true;
    }

    private void pickupCards(int cards) {
        int index = (rotation.equals(Rotation.CLOCKWISE)) ?
                (active == players.size() - 1 ? 0 : active + 1):
                (active == 0 ? players.size() - 1 : active - 1);

        Player player = players.get(index);

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
                this.active = (active == players.size() - 1 ? 0 : active + 1);
                break;
            case COUNTER_CLOCKWISE:
                this.active = (active == 0 ? players.size() - 1 : active - 1);
                break;
            default:
                break;
        }
    }
}
