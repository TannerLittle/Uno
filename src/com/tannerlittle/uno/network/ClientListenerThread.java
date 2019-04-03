package com.tannerlittle.uno.network;

import com.tannerlittle.uno.UnoGame;
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
        if (command.equals("START")) {
            UnoGame game = this.getGame();
            game.deal();

            this.server.broadcastCommand("DECK " + game.getDeck().toString());
            this.server.broadcastCommand("DISCARDS " + game.getDiscards().toString());

            for (Player player : game.getPlayers()) {
                Hand hand = player.getHand();
                this.server.broadcastCommand("HAND " + player.getUniqueId() + " " + hand.toString());
            }
        }

        this.server.broadcastCommand(input);
    }
}
