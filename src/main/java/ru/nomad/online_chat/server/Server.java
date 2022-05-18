package ru.nomad.online_chat.server;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import ru.nomad.online_chat.ServerAPI;
import ru.nomad.online_chat.ServerConstant;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements ServerConstant, ServerAPI {
    private Vector<ClientHandler> clients;
    private AuthService authService;
    private ExecutorService poolConnection;
    private ServerSocket server;
    private Scanner scanner;
    private static final Logger LOGGER = LogManager.getLogger(Server.class);
    private boolean isRun;

    public Server() {
        try {
            server = new ServerSocket(PORT);
            authService = new BaseAuthService();
            authService.start();
            clients = new Vector<>();
            poolConnection = Executors.newFixedThreadPool(3);
            LOGGER.info("Server is up and running.");
            isRun = true;
            new Thread(() -> {
                scanner = new Scanner(System.in);
                while (!scanner.nextLine().equals("stop"));
                stop();
            }).start();
            while (true) {
                Socket socket = server.accept();
                LOGGER.info("Client has connected.");
                new ClientHandler(this, socket);
            }
        } catch (IOException e) {
            if (isRun) LOGGER.error("Server error!");
            else LOGGER.info("Server stopped.");
        } catch (ClassNotFoundException e) {
            LOGGER.warn("JDBC not found!");
        } catch (SQLException e) {
            LOGGER.error("Database not connect!");
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
//        broadcastUsersList();
    }

    public AuthService getAuthService() {
        return authService;
    }

    public synchronized boolean isNickBusy(String nick) {
        for (ClientHandler client : clients) {
            if (client.getNick().equals(nick)) return false;
        }
        return true;
    }

    public ExecutorService getPoolConnection() {
        return poolConnection;
    }

    public void stop() {
        isRun = false;
        scanner.close();
        for (ClientHandler client : clients) {
            client.sendMessage(SERVER_STOP);
        }
        try {
            server.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        poolConnection.shutdown();
    }
}
