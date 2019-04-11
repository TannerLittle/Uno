package com.tannerlittle.uno.network;

import com.tannerlittle.uno.model.Hand;
import com.tannerlittle.uno.model.Player;

import java.net.Socket;
import java.util.UUID;

// Runs on Uno Server
// Accepts and broadcasts commands

public class ClientListenerThread extends ListenerThread {

    private UnoServer server;

    public ClientListenerThread(Socket socket, UnoServer server) {
        super(socket);

        this.server = server;
    }

    public void onCommand(String input, String command, String content) {
        if (command.equals("PLAYER")) {
            String[] args = content.split("\\s+");
            UUID id = UUID.fromString(args[0]);
            String name = args[1];

            Player player = new Player(id, name);
            this.server.getPlayers().add(player);
        }

        if (command.equals("START")) {
            this.server.setup();

            this.server.broadcastCommand("DECK " + server.getDeck().toString());
            this.server.broadcastCommand("DISCARDS " + server.getDiscards().toString());

            for (Player player : server.getPlayers()) {
                Hand hand = player.getHand();
                this.server.broadcastCommand("HAND " + player.getUniqueId() + " " + hand.toString());
            }
        }

        if (command.equals("REVERSE")) {
            UUID id = UUID.fromString(content);
            if (!(server.getActive().equals(id))) return;

            this.server.reverse();
            return;
        }

        if (command.equals("ROTATE")) {
            UUID id = UUID.fromString(content);
            if (!(server.getActive().equals(id))) return;

            this.server.rotate();
            return;
        }

        if (command.equals("SKIP")) {
            UUID id = UUID.fromString(content);
            if (!(server.getActive().equals(id))) return;

            this.server.skip();
            return;
        }

        if (command.equals("DRAW")) {
            int count = Integer.parseInt(content);
            this.server.draw(count);
            return;
        }

        this.server.broadcastCommand(input);
    }
}
