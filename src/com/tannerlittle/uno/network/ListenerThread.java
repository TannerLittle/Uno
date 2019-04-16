package com.tannerlittle.uno.network;

import java.io.*;
import java.net.Socket;

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

            String command = line.split("\\s+")[0];
            String content = (line.length() >= command.length() + 1) ? line.substring(command.length() + 1) : line;

            if (this instanceof ClientListenerThread) System.out.println("Client -> Server: " + line);
            if (this instanceof ServerListenerThread) System.out.println("Server -> Client: " + line);

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
