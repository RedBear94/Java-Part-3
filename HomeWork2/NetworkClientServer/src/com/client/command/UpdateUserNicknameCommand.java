package com.client.command;

import java.io.Serializable;

public class UpdateUserNicknameCommand implements Serializable {
    private final String oldNickname;
    private final String newNickname;

    public UpdateUserNicknameCommand(String oldNickname, String newNickname) {
        this.oldNickname = oldNickname;
        this.newNickname = newNickname;
    }

    public String getNewNickname() {
        return newNickname;
    }

    public String getOldNickname() {
        return oldNickname;
    }
}
