package com.tannerlittle.uno.model;

import java.util.Stack;

public class CardStack extends Stack<Card> {

    public String toString() {
        StringBuilder str = new StringBuilder();

        for (Card card : this) {
            str.append(card.toString());
            str.append(", ");
        }

        return str.toString().substring(0, str.length() - 2);
    }

    public void parse(String str) {
        this.clear();

        for (String card : str.split(", ")) {
            this.add(Card.parse(card));
        }
    }
}
