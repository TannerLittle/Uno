package com.tannerlittle.uno.enums;

import java.awt.*;

public enum Suit {

    RED(new Color(192,80,77), new Color(57, 49, 48)),
    YELLOW(new Color(255,204,0), new Color(58, 55, 42)),
    GREEN(new Color(0,153,0), new Color(25, 35, 25)),
    BLUE(new Color(31,73,125), new Color(26, 30, 35)),

    WILD(new Color(0, 0, 0), new Color(31, 31, 31));

    private Color color;
    private Color faded;

    Suit(Color color, Color faded) {
        this.color = color;
        this.faded = faded;
    }

    public Color getColor() {
        return color;
    }

    public Color getFaded() {
        return faded;
    }
}
