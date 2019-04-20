package com.tannerlittle.uno.network;

import com.tannerlittle.uno.model.Player;

import java.net.Socket;
import java.util.UUID;

// Runs on Uno Server
// Accepts and broadcasts commands

//TODO: This class should ONLY translate commands - all processing should be in Server

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
            String name = content.substring(args[0].length() + 1);

            Player player = new Player(id, name);

            this.server.getPlayers().add(player);
        }

        if (command.equals("START")) {
            this.server.start();
        }

        if (command.equals("REVERSE")) {
            UUID id = UUID.fromString(content);

            this.server.reverse(id);
            return;
        }

        if (command.equals("ROTATE")) {
            UUID id = UUID.fromString(content);

            this.server.rotate(id);
            return;
        }

        if (command.equals("SKIP")) {
            UUID id = UUID.fromString(content);

            this.server.skip(id);
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
