package com.client;

// enum - перечисление комманд
public enum CommandType {
    AUTH,
    AUTH_ERROR,
    PRIVATE_MESSAGE,
    BROADCAST_MESSAGE,
    MESSAGE,
    UPDATE_USERS_LIST,
    UPDATE_USER_NICKNAME,
    ERROR,
    END,
}
