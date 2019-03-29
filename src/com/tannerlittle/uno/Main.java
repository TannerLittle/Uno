package com.tannerlittle.uno;

import com.tannerlittle.uno.enums.GameState;
import com.tannerlittle.uno.model.Card;
import com.tannerlittle.uno.model.Deck;
import com.tannerlittle.uno.model.Player;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        // Local CLI Uno for Testing

        Scanner scanner = new Scanner(System.in);

        System.out.println("How many players are in this game?");
        int count = scanner.nextInt();

        if (count <= 0) {
            System.out.println("You must have at least one player.");
            System.exit(0);
        }

        List<Player> players = new LinkedList<>();

        for (int i = 0; i < count; i++) {
            System.out.println("Player #" + (i+1) + "'s name: ");
            String name = scanner.next();

            Player player = new Player(name);
            players.add(player);
        }

        UnoGame game = new UnoGame(players);
    }
}
