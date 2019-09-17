package com.tannerlittle.uno;

import com.tannerlittle.uno.network.UnoServer;
import com.tannerlittle.uno.view.menu.MenuFrame;

import javax.swing.*;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Scanner;

public class Main {

    private UnoGame game;
    private UnoServer server;

    public Main(String[] args) {
        if (args.length == 0) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            new MenuFrame(this);

            new Thread(() -> {
                Scanner scanner = new Scanner(System.in);

                while(true) {
                    if (server == null) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        continue;
                    }

                    String command = scanner.nextLine();
                    this.server.broadcastCommand(command);
                }
            }).start();

            return;
        }

        // Run as headless server

        try {
            InetAddress address = InetAddress.getByName(args[0]);
            int port = Integer.parseInt(args[1]);

            this.server = new UnoServer(address, port);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        if (server == null) {
            System.out.println("Unable to establish a socket connection.");
            System.exit(0);
        }

        System.out.println("UNO Server initialized.");
        System.out.println("Copyright © Tanner Little 2019 - All rights reserved.");
        Scanner scanner = new Scanner(System.in);

        do {
            String command = scanner.nextLine();

            if (command.equalsIgnoreCase("start")) {
                if (server.getPlayers().size() == 0) {
                    System.out.println("There are no players connected.");
                    continue;
                }

                System.out.println("Starting game.");

                this.server.start();
                this.server.broadcastCommand("START");
            } else {
                System.out.println("Broadcasting command.");
                this.server.broadcastCommand(command);
            }
        } while (true);
    }

    public static void main(String[] args) {
        new Main(args);
    }

    public UnoGame getGame() {
        return game;
    }

    public void setGame(UnoGame game) {
        this.game = game;
    }

    public void setServer(UnoServer server) {
        this.server = server;
    }
}
