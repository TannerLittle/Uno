package com.tannerlittle.uno.model;

import com.tannerlittle.uno.enums.Rank;
import com.tannerlittle.uno.enums.Suit;

public class Card {

    private Suit suit;
    private Rank rank;

    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }

    public Suit getSuit() {
        return suit;
    }

    public void setSuit(Suit suit) {
        this.suit = suit;
    }

    public Rank getRank() {
        return rank;
    }

    public String toString() {
        return suit.name() + " " + rank.name();
    }

    public boolean isSimilar(Card card) {
        return (suit.equals(card.getSuit()) || rank.equals(card.getRank()) || card.getSuit().equals(Suit.WILD));
    }

    public static Card parse(String str) {
        String[] parsed = str.split(" ");
        Suit suit = Suit.valueOf(parsed[0]);
        Rank rank = Rank.valueOf(parsed[1]);

        return new Card(suit, rank);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Card)) return false;

        Card card = (Card) object;
        return card.getSuit().equals(suit) && card.getRank().equals(rank);
    }
}
