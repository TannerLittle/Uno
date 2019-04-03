package com.tannerlittle.uno.network;

import com.tannerlittle.uno.Main;
import com.tannerlittle.uno.UnoGame;
import com.tannerlittle.uno.model.Player;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

// Runs on Uno Servers
// Manages Server <=> Client socket connections

public class UnoServer {

    private ServerSocket server;
    private Thread thread;

    private UnoGame game;

    private List<ClientListenerThread> clients = new ArrayList<>();

    public UnoServer(String address) throws IOException {
        InetAddress inet = ((address == null) || (address.isEmpty())) ? InetAddress.getLocalHost() : InetAddress.getByName(address);
        this.server = new ServerSocket(0, 1, inet);

        this.game = new UnoGame();

        this.thread = new Thread(() -> {
            while (true) {
                Socket socket = null;

                try {
                    socket = server.accept();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                if (socket == null) continue;

                ClientListenerThread client = new ClientListenerThread(socket, this);
                client.setGame(game);
                client.start();

                for (Player player : game.getPlayers()) {
                    client.sendCommand(player.getCommand());
                }

                this.clients.add(client);
            }
        });

        this.thread.start();
    }

    public InetAddress getSocketAddress() {
        return this.server.getInetAddress();
    }

    public int getPort() {
        return this.server.getLocalPort();
    }

    public void broadcastCommand(String command) {
        for (ClientListenerThread client : clients) {
            client.sendCommand(command);
        }
    }
}