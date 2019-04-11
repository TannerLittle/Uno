package com.tannerlittle.uno.network;

import com.tannerlittle.uno.UnoGame;
import com.tannerlittle.uno.enums.Rank;
import com.tannerlittle.uno.enums.Suit;
import com.tannerlittle.uno.model.Card;
import com.tannerlittle.uno.model.Player;

import java.io.*;
import java.net.Socket;
import java.util.UUID;

public abstract class ListenerThread extends Thread {

    private Socket socket;

    public ListenerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        InputStream input = null;

        try {
            input = socket.getInputStream();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        if (input == null) {
            return;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(input));

        while (true) {
            String line;

            try {
                line = reader.readLine();
            } catch (IOException ex) {
                ex.printStackTrace();
                continue;
            }

            if (line == null) continue;

            if (this instanceof ClientListenerThread) System.out.println("Client => " + line);
            if (this instanceof ServerListenerThread) System.out.println("Server => " + line);

            String command = line.split("\\s+")[0];
            String content = (line.length() >= command.length() + 1) ? line.substring(command.length() + 1) : line;

            this.onCommand(line, command, content);
        }
    }

    public void sendCommand(String command) {
        PrintWriter out;

        try {
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }

        out.println(command);
        out.flush();
    }

    public Socket getSocket() {
        return socket;
    }

    public abstract void onCommand(String input, String command, String content);
}
