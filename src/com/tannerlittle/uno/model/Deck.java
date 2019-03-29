package com.tannerlittle.uno.model;

import com.tannerlittle.uno.enums.Rank;
import com.tannerlittle.uno.enums.Suit;

import java.util.Collections;
import java.util.Stack;

public class Deck extends Stack<Card> {

    public Deck() {
        for (Suit suit : Suit.values()) {
            if (suit == Suit.WILD) continue;

            for (Rank rank : Rank.values()) {
                switch (rank) {
                    case WILD:
                    case WILD_DRAW_FOUR:
                        this.push(new Card(Suit.WILD, rank));
                        break;
                    case ZERO:
                        this.push(new Card(suit, rank));
                        break;
                    default:
                        for (int i = 0; i < 2; i++) {
                            this.push(new Card(suit, rank));
                        }

                        break;
                }
            }
        }

        Collections.shuffle(this);
    }
}
