package com.tannerlittle.uno.network;

import com.tannerlittle.uno.UnoGame;
import com.tannerlittle.uno.enums.Rank;
import com.tannerlittle.uno.enums.Suit;
import com.tannerlittle.uno.model.Card;
import com.tannerlittle.uno.model.Player;

import java.io.*;
import java.net.Socket;
import java.util.UUID;

public abstract class ListenerThread extends Thread {

    private Socket socket;
    private UnoGame game;

    public ListenerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        InputStream input = null;

        try {
            input = socket.getInputStream();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        if (input == null) {
            return;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(input));

        while (true) {
            if (game == null) {
                try {
                    sleep(100);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }

                continue;
            }

            String line;

            try {
                line = reader.readLine();
            } catch (IOException ex) {
                ex.printStackTrace();
                continue;
            }

            if (line == null) continue;

            if (this instanceof ClientListenerThread) System.out.println("Client => " + line);
            if (this instanceof ServerListenerThread) System.out.println("Server => " + line);

            String command = line.split("\\s+")[0];
            String content = (line.length() >= command.length() + 1) ? line.substring(command.length() + 1) : line;

            // Game play commands

            if (command.equals("PLAYER")) {
                String[] args = content.split("\\s+");
                UUID id = UUID.fromString(args[0]);
                String name = args[1];

                Player player = new Player(id, name);
                this.game.addPlayer(player);
            }

            if (command.equals("PLAY")) {
                String[] args = content.split("\\s+");

                UUID id = UUID.fromString(args[0]);
                Suit suit = Suit.valueOf(args[1]);
                Rank rank = Rank.valueOf(args[2]);

                Player player = game.getPlayer(id);
                Card card = new Card(suit, rank);

                this.game.playCard(player, card);
            }

            if (command.equals("PICKUP")) {
                String[] args = content.split("\\s+");

                UUID id = UUID.fromString(args[0]);

                Player player = game.getPlayer(id);

                this.game.pickupCard(player);
            }

            if (command.equals("WILD")) {
                String[] args = content.split("\\s+");

                UUID id = UUID.fromString(args[0]);
                Suit suit = Suit.valueOf(args[1]);

                Player player = game.getPlayer(id);

                this.game.setWild(player, suit);
            }

            // Command pass-through
            this.onCommand(line, command, content);
        }
    }

    public void sendCommand(String command) {
        PrintWriter out;

        try {
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }

        out.println(command);
        out.flush();
    }

    public Socket getSocket() {
        return socket;
    }

    public UnoGame getGame() {
        return game;
    }

    public void setGame(UnoGame game) {
        this.game = game;
    }

    public abstract void onCommand(String input, String command, String content);
}
