package com.tannerlittle.uno;

import com.tannerlittle.uno.model.Player;
import com.tannerlittle.uno.network.UnoClient;
import com.tannerlittle.uno.network.UnoServer;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Scanner;
import java.util.UUID;

public class Main {

    private UnoGame game;

    private UnoServer server;
    private UnoClient client;

    public Main() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Host new Uno Server [true|false]: ");
        boolean host = scanner.nextBoolean();

        if (host) {
            try {
                this.server = new UnoServer(null);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            if (server == null) {
                System.out.println("Unable to establish a socket connection.");
                System.exit(0);
            }

            System.out.println(server.getSocketAddress().getHostAddress());
            System.out.println(server.getPort());

            try {
                this.client = new UnoClient(InetAddress.getByName("0.0.0.0"), server.getPort());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        if (client == null) {
            System.out.println("Server Address: ");
            String address = scanner.next();

            System.out.println("Server Port: ");
            int port = scanner.nextInt();

            try {
                this.client = new UnoClient(InetAddress.getByName(address), port);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        System.out.println("Player Name: ");
        String name = scanner.next();

        this.game = new UnoGame(client, name);

        this.client.getThread().setGame(game);
        this.client.sendCommand(game.getPlayer().getCommand());

        if (host) {
            System.out.println("Press ENTER to start the game.");
            scanner.next();

            this.client.sendCommand("START");
        }
    }

    public UnoGame getGame() {
        return game;
    }

    public static void main(String[] args) {
        new Main();
    }
}
