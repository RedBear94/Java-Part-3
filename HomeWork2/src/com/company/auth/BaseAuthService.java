package com.company.auth;

import sun.rmi.server.UnicastServerRef;

import java.lang.reflect.Array;
import java.util.*;

public class BaseAuthService implements AuthService {
    private static class UserData{
        private String login;
        private String password;
        private String username;

        public UserData(String login, String password, String username) {
            this.login = login;
            this.password = password;
            this.username = username;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UserData userData = (UserData) o;
            return Objects.equals(login, userData.login) &&
                    Objects.equals(password, userData.password) &&
                    Objects.equals(username, userData.username);
        }

        @Override
        public int hashCode() {
            return Objects.hash(login, password, username);
        }
    }

    private static List<UserData> USER_DATA = new ArrayList<UserData>(Arrays.asList(
            new UserData("login1", "pass1", "username1"),
            new UserData("login2", "pass2", "username2"),
            new UserData("login3", "pass3", "username3")
    )
    );

    @Override
    public String getUsernameByLoginAndPassword(String login, String password) {
        for(UserData userData: USER_DATA){
            if(userData.login.equals(login) && userData.password.equals(password)){
                return userData.username;
            }
        }
        return null;
    }

    @Override
    public void start() {
        System.out.println("Сервис аутентификации запущен");
    }

    @Override
    public void stop() {
        System.out.println("Сервис аутентификации остановлен");
    }
}
