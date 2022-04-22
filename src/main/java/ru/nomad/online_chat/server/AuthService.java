package ru.nomad.online_chat.server;

public interface AuthService {
    void start();
    void stop();
    String getNickByLoginPassword(String login, String password);
}
