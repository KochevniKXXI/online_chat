package ru.nomad.online_chat.server;

import ru.nomad.online_chat.ServerAPI;
import ru.nomad.online_chat.ServerConstant;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Vector;

public class Server implements ServerConstant, ServerAPI {
    private Vector<ClientHandler> clients;
    private AuthService authService;

    public Server() {
        try (ServerSocket server = new ServerSocket(PORT)) {
            authService = new BaseAuthService();
            authService.start();
            clients = new Vector<>();
            System.out.println("Server is up and running!");
            while (true) {
                System.out.println("Awaiting for connection...");
                Socket socket = server.accept();
                System.out.println("Client has connected!");
                new ClientHandler(this, socket);
            }
        } catch (IOException e) {
            System.out.println("Server error!");
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC not found!");
        } catch (SQLException e) {
            System.out.println("Database not connect!");
        } finally {
            if (authService != null) authService.stop();
        }
    }

    public synchronized void broadcastMessage(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    public synchronized void broadcastMassage(ClientHandler from, String to, String message) {
        for (ClientHandler client : clients) {
            if (client.getNick().equals(to)) {
                client.sendMessage("from " + from.getNick() + ": " + message);
                from.sendMessage("to " + to + " : " + message);
                return;
            }
        }
        from.sendMessage("User " + to +" not found");
    }

    public synchronized void broadcastUsersList() {
        StringBuilder sb = new StringBuilder(USERS_LIST);
        for (ClientHandler client : clients) {
            sb.append(" ").append(client.getNick());
        }
        broadcastMessage(sb.toString());
    }

    public synchronized void subscribeClient(ClientHandler clientHandler) {
        clients.add(clientHandler);
        broadcastUsersList();
    }

    public synchronized void unsubscribeClient(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        broadcastUsersList();
    }

    public AuthService getAuthService() {
        return authService;
    }

    public synchronized boolean isNickBusy(String nick) {
        for (ClientHandler client : clients) {
            if (client.getNick().equals(nick)) return true;
        }
        return false;
    }
}
