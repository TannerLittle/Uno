package com.tannerlittle.uno.model;

import javax.swing.*;
import java.util.UUID;

public class Player {

    private UUID id;
    private String name;

    private Hand hand;

    public Player(UUID id, String name) {
        this.id = id;
        this.name = name;
        this.hand = new Hand();
    }

    public UUID getUniqueId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Hand getHand() {
        return hand;
    }

    public String getCommand() {
        return "PLAYER " + id + " " + name;
    }

    public void sendMessage(String message) {
        JOptionPane.showMessageDialog(null, message);
    }
}
