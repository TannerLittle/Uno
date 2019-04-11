package com.tannerlittle.uno;

import com.tannerlittle.uno.model.Player;
import com.tannerlittle.uno.network.UnoClient;
import com.tannerlittle.uno.network.UnoServer;
import com.tannerlittle.uno.view.GameFrame;

import javax.swing.*;
import java.awt.*;
import java.io.Console;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Scanner;
import java.util.UUID;

public class Main {

    private UnoGame game;

    private UnoServer server;
    private UnoClient client;

    public static GameFrame frame;

    public Main() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Scanner scanner = new Scanner(System.in);

        System.out.println("Player Name: ");
        String name = scanner.next();

        Player player = new Player(UUID.randomUUID(), name);

        this.game = new UnoGame(player);

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
                InetAddress address = InetAddress.getByName("0.0.0.0");
                int port = server.getPort();

                this.client = new UnoClient(game, address, port);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        if (client == null) {
            try {
                System.out.println("Server Address: ");
                InetAddress address = InetAddress.getByName(scanner.next());

                System.out.println("Server Port: ");
                int port = scanner.nextInt();

                this.client = new UnoClient(game, address, port);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        Main.frame = new GameFrame(client, game);

        this.client.sendCommand(game.getPlayer().getCommand());

        if (host) {
            System.out.println("Press ENTER to start the game.");
            scanner.next();

            this.client.sendCommand("START");
        }
    }

    public static void main(String[] args) {
        new Main();
    }
}
