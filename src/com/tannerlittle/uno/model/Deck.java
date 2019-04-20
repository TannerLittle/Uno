package com.tannerlittle.uno.model;

import com.tannerlittle.uno.enums.Rank;
import com.tannerlittle.uno.enums.Suit;

import java.util.Collections;

public class Deck extends CardStack {

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

    @Override
    public void parse(String str) {
        this.clear();

        //TODO: Bad implementation, just uses the same deck multiple times over.
        //TODO: In future, if deck runs out of cards, shuffle discard pile
        for (int i = 0; i < 10; i++) super.parse(str);
    }
}
