package com.tannerlittle.uno.network;

import com.tannerlittle.uno.enums.Rotation;
import com.tannerlittle.uno.model.Card;
import com.tannerlittle.uno.model.Deck;
import com.tannerlittle.uno.model.Discards;
import com.tannerlittle.uno.model.Player;

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

    public UnoServer(String address) throws IOException {
        InetAddress inet = ((address == null) || (address.isEmpty())) ? InetAddress.getLocalHost() : InetAddress.getByName(address);
        this.server = new ServerSocket(0, 1, inet);

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

    public void setup() {
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

    public void rotate() {
        this.active = next();

        UUID id = players.get(active).getUniqueId();
        this.broadcastCommand("ACTIVE " + id);
    }

    public void skip() {
        this.active = next();
        this.rotate();
    }

    public void draw(int count) {
        UUID id = players.get(next()).getUniqueId();
        this.broadcastCommand("DRAW " + id + " " + count);
    }

    public void reverse() {
        this.rotation = Rotation.getReverse(rotation);
    }
}