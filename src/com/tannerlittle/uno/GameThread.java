package com.tannerlittle.uno;

import com.tannerlittle.uno.enums.GameState;
import com.tannerlittle.uno.enums.Rank;
import com.tannerlittle.uno.enums.Rotation;
import com.tannerlittle.uno.enums.Suit;
import com.tannerlittle.uno.model.Card;
import com.tannerlittle.uno.model.Hand;
import com.tannerlittle.uno.model.Player;

import java.util.Scanner;

public class GameThread extends Thread {

    private UnoGame game;

    public GameThread(UnoGame game) {
        this.game = game;
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            if (game.getState() == GameState.WILD) {
                System.out.println("Choose a new color: ");
                for (Suit suit : Suit.values()) {
                    if (suit.equals(Suit.WILD)) continue;
                    System.out.println(suit.name().toUpperCase());
                }

                Suit suit;
                do {
                    suit = Suit.valueOf(scanner.next());
                } while (!game.setWild(game.getActive(), suit));

                continue;
            }

            Player player = game.getActive();

            System.out.println("Player " + player.getName() + "'s turn.");

            Hand hand = player.getHand();

            for (int index = 0; index < hand.size(); index++) {
                Card card = hand.get(index);
                System.out.println((index + 1) + ": " + card.getSuit() + " " + card.getRank());
            }

            System.out.println("0: Draw card");

            System.out.println("");

            Card discard = game.getDiscards().peek();
            System.out.println("Discard: " + discard.getSuit() + " " + discard.getRank());

            boolean played = false;

            while (!(played)) {
                int index = scanner.nextInt();

                if (index == 0) {
                    played = game.pickupCard(player);
                } else {
                    Card card = hand.get(index - 1);
                    played = game.playCard(player, card);
                }
            }

            // card played
        }
    }
}
