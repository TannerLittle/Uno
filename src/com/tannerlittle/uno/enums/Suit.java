package com.tannerlittle.uno.enums;

import java.awt.*;

public enum Suit {

    RED(new Color(192,80,77)),
    YELLOW(new Color(255,204,0)),
    GREEN(new Color(0,153,0)),
    BLUE(new Color(31,73,125)),

    WILD(new Color(0, 0, 0));

    private Color color;

    Suit(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}
