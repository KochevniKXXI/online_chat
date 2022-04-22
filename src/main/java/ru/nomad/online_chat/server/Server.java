package ru.nomad.online_chat.server;

import ru.nomad.online_chat.ServerConstant;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Server implements ServerConstant {
    private Vector<ClientHandler> clients;
    private AuthService authService;

    public Server() {
        Socket socket;
        clients = new Vector<>();
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            authService = new BaseAuthService();
            authService.start(); // placeholder
            System.out.println("Server is up and running! Awaiting for connections...");
            while (true) {
                socket = serverSocket.accept();
                clients.add(new ClientHandler(this, socket));
                System.out.println("Client has connected!");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void broadcast(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    public void broadcast(String nick1, String nick2, String message) {
        for (ClientHandler client : clients) {
            if (client.getNick().equals(nick1) || client.getNick().equals(nick2)) client.sendMessage(message);
        }
    }

    public void unSubscribeMe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }

    public AuthService getAuthService() {
        return authService;
    }

    public boolean isNickBusy(String nick) {
        for (ClientHandler client : clients) {
            if (client.getNick().equals(nick)) return true;
        }
        return false;
    }
}
