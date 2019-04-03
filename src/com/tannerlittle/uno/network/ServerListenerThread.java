package com.tannerlittle.uno.network;

import com.tannerlittle.uno.UnoGame;
import com.tannerlittle.uno.model.Player;
import com.tannerlittle.uno.view.GameFrame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.util.UUID;

// Runs on Uno Clients
// Accepts commands from server

public class ServerListenerThread extends ListenerThread {

    private UnoClient client;

    public ServerListenerThread(Socket socket, UnoClient client) {
        super(socket);

        this.client = client;
    }

    public void onCommand(String input, String command, String content) {
        if (command.equals("DECK")) {
            this.getGame().getDeck().parse(content);
        }

        if (command.equals("DISCARDS")) {
            this.getGame().getDiscards().parse(content);
        }

        if (command.equals("HAND")) {
            String[] args = content.split("\\s+");
            UUID id = UUID.fromString(args[0]);
            String cards = content.substring(args[0].length() + 1);

            this.getGame().getPlayer(id).getHand().parse(cards);
        }

        if (command.equals("START")) {
            this.getGame().start();
        }

        if (!(this.getGame().getFrame() == null)) {
            this.getGame().getFrame().update();
        }
    }
}