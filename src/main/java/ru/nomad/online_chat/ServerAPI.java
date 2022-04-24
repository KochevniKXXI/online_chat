package ru.nomad.online_chat;

public interface ServerAPI {
    String SYSTEM_SYMBOL = "/";
    String CLOSE_CONNECTION = "/end";
    String AUTH = "/auth";
    String AUTH_SUCCESSFUL = "/authok";
    String PRIVATE_MESSAGE = "/w";
    String USERS_LIST = "/userslist";
}
