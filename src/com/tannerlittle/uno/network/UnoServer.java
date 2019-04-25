package com.tannerlittle.uno.network;

import com.tannerlittle.uno.enums.Rotation;
import com.tannerlittle.uno.model.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;

// Runs on Uno Servers
// Manages Server <=> Client socket connections

public class UnoServer {

    private ServerSocket server;
    private Thread thread;

    private List<ClientListenerThread> clients = new ArrayList<>();

    private Deck deck;
    private Discards discards;

    private Rotation rotation;

    private List<Player> players = new LinkedList<>();
    private int active;

    public UnoServer(InetAddress address, int port) throws IOException {
        this.server = new ServerSocket(port, 1, address);

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
                client.start();

                for (Player player : players) {
                    client.sendCommand(player.getCommand());
                }

                this.clients.add(client);
            }
        });

        this.thread.start();
    }

    public UUID getActive() {
        return players.get(active).getUniqueId();
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

    public void start() {
        this.deck = new Deck();
        this.discards = new Discards();

        this.rotation = Rotation.CLOCKWISE;

        this.active = (players.size() > 1 ? 1 : 0);

        for (Player player : players) {
            for (int i = 0; i < 7; i++) {
                Card card = deck.pop();
                player.getHand().push(card);
            }
        }

        Card card = deck.pop();
        this.discards.push(card);

        //TODO:
        // Temporarily set order to alphabetical in order to match PlayerPanel client-side.
        this.players = players.stream().sorted(Comparator.comparing(Player::getName)).collect(Collectors.toList());

        // Sync each client with the server
        this.broadcastCommand("DECK " + deck.toString());
        this.broadcastCommand("DISCARDS " + discards.toString());

        for (Player player : players) {
            Hand hand = player.getHand();
            this.broadcastCommand("HAND " + player.getUniqueId() + " " + hand.toString());
        }

        this.rotate();
    }

    public Deck getDeck() {
        return deck;
    }

    public Discards getDiscards() {
        return discards;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public int next() {
        return (rotation.equals(Rotation.CLOCKWISE)
                ? (active == players.size() - 1 ? 0 : active + 1)
                : (active == 0 ? players.size() - 1 : active - 1));
    }

    public void rotate(UUID id) {
        if (!(getActive().equals(id))) return;

        this.rotate();
    }

    public void rotate() {
        this.active = next();

        Player player = players.get(active);
        this.broadcastCommand("ACTIVE " + player.getUniqueId());
    }

    public void skip(UUID id) {
        if (!(players.get(active).equals(id))) return;

        this.active = next();

        this.broadcastCommand("FLASH " + id + " Your turn has been skipped!");

        this.rotate();
    }

    public void draw(int count) {
        UUID id = players.get(next()).getUniqueId();
        this.broadcastCommand("DRAW " + id + " " + count);
    }

    public void reverse(UUID id) {
        if (!(getActive().equals(id))) return;

        this.rotation = Rotation.getReverse(rotation);
    }
}