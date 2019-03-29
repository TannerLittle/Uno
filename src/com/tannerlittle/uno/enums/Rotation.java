package com.tannerlittle.uno.enums;

public enum Rotation {

    CLOCKWISE,
    COUNTER_CLOCKWISE;

    public static Rotation getReverse(Rotation rotation) {
        switch (rotation) {
            case CLOCKWISE:
                return COUNTER_CLOCKWISE;
            case COUNTER_CLOCKWISE:
                return CLOCKWISE;
            default:
                return null;
        }
    }
}
