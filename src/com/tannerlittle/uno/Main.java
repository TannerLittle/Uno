package com.tannerlittle.uno;

import com.tannerlittle.uno.network.UnoClient;
import com.tannerlittle.uno.network.UnoServer;
import com.tannerlittle.uno.view.GameFrame;
import com.tannerlittle.uno.view.menu.MenuFrame;

import javax.swing.*;
import java.util.Scanner;

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

        MenuFrame frame = new MenuFrame(this);

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

                String command = scanner.next();
                this.server.broadcastCommand(command);
            }
        }).start();
    }

    public static void main(String[] args) {
        new Main();
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

    public void setClient(UnoClient client) {
        this.client = client;
    }
}
