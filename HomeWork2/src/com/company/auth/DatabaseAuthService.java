package com.company.auth;

import java.sql.*;
import java.util.*;

public class DatabaseAuthService implements AuthService {
    public static Connection conn;
    public static Statement statmt;
    public static ResultSet resSet;

    private static List<UserData> userData = new ArrayList<UserData>();

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


    @Override
    public String getUsernameByLoginAndPassword(String login, String password) {
        for(UserData userData: userData){
            if(userData.login.equals(login) && userData.password.equals(password)){
                return userData.username;
            }
        }
        return null;
    }

    @Override
    public void start() {
        try {
            DatabaseAuthService.Conn();
            DatabaseAuthService.CreateDB();
            DatabaseAuthService.WriteDB();
            DatabaseAuthService.ReadDB(userData);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Сервис аутентификации запущен");
    }

    @Override
    public void stop() {
        try {
            DatabaseAuthService.CloseDB();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Сервис аутентификации остановлен");
    }

    public static void Conn () throws ClassNotFoundException, SQLException
    {
        conn = null;
        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("jdbc:sqlite:AuthDatabase.s3db");

        System.out.println("База Подключена!");
    }

    public static void CreateDB () throws ClassNotFoundException, SQLException
    {
        statmt = conn.createStatement();

        statmt.execute("CREATE TABLE if not exists 'users' " +
                "('id' INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "'name' CHAR(20) NOT NULL UNIQUE, " +
                "'nickname' CHAR(20) NOT NULL UNIQUE, " +
                "'password' CHAR(50) NOT NULL" +
                ");");

        System.out.println("Таблица создана или уже существует.");
    }

    public static void WriteDB ()
    {
        try {
            statmt.execute("INSERT INTO 'users' ('name', 'nickname', 'password') VALUES ('login1', 'username1', 'pass1'); ");
            statmt.execute("INSERT INTO 'users' ('name', 'nickname', 'password') VALUES ('login2', 'username2', 'pass2'); ");
            statmt.execute("INSERT INTO 'users' ('name', 'nickname', 'password') VALUES ('login3', 'username3', 'pass3'); ");
        } catch (SQLException e){
            System.out.println("Данные уже были добавлены");
        }

        System.out.println("Таблица заполнена");
    }

    public static void ReadDB (List<UserData> userData) throws ClassNotFoundException, SQLException
    {
        resSet = statmt.executeQuery("SELECT * FROM users");

        while (resSet.next()) {
            String name = resSet.getString("name");
            String nickname = resSet.getString("nickname");
            String password = resSet.getString("password");
            userData.add(new UserData(name, password, nickname));
        }

        System.out.println("Данные таблицы считаны");
    }

    public static void CloseDB () throws ClassNotFoundException, SQLException
    {
        resSet.close();
        statmt.close();
        conn.close();
        System.out.println("Соединения закрыты");
    }

    public static void UpdateDB(String oldUsername, String newUsername) throws ClassNotFoundException, SQLException{
            statmt.executeUpdate("UPDATE users SET nickname = '" + newUsername + "' WHERE nickname = '" + oldUsername + "';");
            for(UserData userData: userData){
                if(userData.username.equals(oldUsername)){
                    userData.username = newUsername;
                }
            }
            // userData.clear();
            // DatabaseAuthService.ReadDB(userData);
    }

}
