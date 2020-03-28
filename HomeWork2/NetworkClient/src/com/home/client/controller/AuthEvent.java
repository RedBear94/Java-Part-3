package com.home.client.controller;

@FunctionalInterface
public interface AuthEvent {
    void authIsSuccessful(String nickname);
}
