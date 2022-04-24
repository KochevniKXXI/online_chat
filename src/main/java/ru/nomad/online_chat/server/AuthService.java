package ru.nomad.online_chat.server;

public interface AuthService {
    void start();
    String getNickByLoginPassword(String login, String password);
    void stop();
}
