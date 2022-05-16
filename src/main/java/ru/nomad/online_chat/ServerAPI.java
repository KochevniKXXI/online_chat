package ru.nomad.online_chat;

public interface ServerAPI {
    String SYSTEM_SYMBOL = "/";
    String CLOSE_CONNECTION = "/end";
    String AUTH = "/auth";
    String AUTH_SUCCESSFUL = "/authok";
    String PRIVATE_MESSAGE = "/w";
    String USERS_LIST = "/userslist";
    String CHANGE_NICKNAME = "/cnick";
    String CHANGE_NICKNAME_SUCCESSFUL = "/cnickok";
    String SERVER_STOP = "/stopserv";
}
