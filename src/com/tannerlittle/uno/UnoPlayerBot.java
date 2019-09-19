package com.tannerlittle.uno;

import com.tannerlittle.uno.model.Player;
import com.tannerlittle.uno.network.UnoServer;

import java.util.UUID;

public class UnoPlayerBot {

    private UnoServer server;
    private UnoGame game;

    public UnoPlayerBot(UnoServer server, UUID id) {
        this.server = server;

        Player player = new Player(id, "Computer");

        this.game = new UnoGame(player);
        this.server.getPlayers().add(player);

        this.server.broadcastCommand("PLAYER " + player.getUniqueId() + " " + player.getName());
    }

    public void play() {

    }
}
