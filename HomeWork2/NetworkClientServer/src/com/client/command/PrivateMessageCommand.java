package com.client.command;

import java.io.Serializable;

public class PrivateMessageCommand implements Serializable {
    private final String receiver;
    private final String message;

    public PrivateMessageCommand(String receiver, String message) {
        this.message = message;
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public String getReceiver() {
        return receiver;
    }
}
