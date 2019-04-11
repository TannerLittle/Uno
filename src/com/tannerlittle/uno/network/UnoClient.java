package com.tannerlittle.uno.network;

import com.tannerlittle.uno.UnoGame;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

// Runs on Uno Clients
// Sends commands to server

public class UnoClient {

    private Socket socket;
    private ListenerThread thread;

    public UnoClient(UnoGame game, InetAddress address, int port) throws IOException {
        this.socket = new Socket(address, port);

        this.thread = new ServerListenerThread(socket, this, game);
        this.thread.start();
    }

    public void sendCommand(String command) {
        PrintWriter out;

        try {
            out = new PrintWriter(this.socket.getOutputStream(), true);
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }

        out.println(command);
        out.flush();
    }
}
