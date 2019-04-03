package com.tannerlittle.uno.enums;

public enum Rank {

    ZERO("0"),
    ONE("1"),
    TWO("2"),
    THREE("3"),
    FOUR("4"),
    FIVE("5"),
    SIX("6"),
    SEVEN("7"),
    EIGHT("8"),
    NINE("9"),
    SKIP("⦸"),
    REVERSE("↻"),
    DRAW_TWO("+2"),
    WILD("⨁"),
    WILD_DRAW_FOUR("+4");

    private String value;

    Rank(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
