package ru.nomad.online_chat.server;

import java.sql.SQLException;

public interface AuthService {
    void start() throws ClassNotFoundException, SQLException;
    String getNickByLoginPassword(String login, String password);
    void stop();
}
