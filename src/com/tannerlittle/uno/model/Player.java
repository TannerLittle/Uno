package com.tannerlittle.uno.model;

public class Player {

    private String name;

    private Hand hand;

    public Player(String name) {
        this.name = name;

        this.hand = new Hand();
    }

    public String getName() {
        return name;
    }

    public Hand getHand() {
        return hand;
    }

    public void sendMessage(String message) {
        //TODO:
        System.out.println(message);
    }
}
