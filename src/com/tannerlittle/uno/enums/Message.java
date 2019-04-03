package com.tannerlittle.uno.enums;

public enum Message {

    ERROR_NOT_ACTIVE("It is not your turn to go."),
    ERROR_WILD("You must choose a new colour first."),
    ERROR_NOT_WILD("You cannot choose a new colour."),
    ERROR_INVALID_CARD("You cannot play that card."),

    SUCCESS_WIN("Player {0} has won the game."),

        ;

    private String message;

    Message(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getMessage(String... args) {
        String message = this.message;

        int index = 0;
        for (String arg : args) {
            message.replace(String.format("{%d}", ++index), arg);
        }

        return message;
    }
}
