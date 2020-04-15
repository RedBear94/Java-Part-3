package com.company.auth;

import java.sql.SQLException;

public interface AuthService {
    String getUsernameByLoginAndPassword(String login, String password);

    void start() throws SQLException, ClassNotFoundException;
    void stop();
}
