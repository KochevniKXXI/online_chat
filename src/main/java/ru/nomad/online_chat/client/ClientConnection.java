package ru.nomad.online_chat.client;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import ru.nomad.online_chat.ServerAPI;
import ru.nomad.online_chat.ServerConstant;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

public class ClientConnection implements ServerConstant, ServerAPI {
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private boolean isAuthorized;
    private String nick;
    private Thread thread;

    public void start(ChatWindowController chatWindowController) {
        try {
            setAuthorized(false);
            this.socket = new Socket(SERVER_URL, PORT);
            this.out = new DataOutputStream(socket.getOutputStream());
            this.in = new DataInputStream(socket.getInputStream());
            thread = new Thread(() -> {
                try {
                    while (true) {
                        String message = in.readUTF();
                        if (message.equalsIgnoreCase(CLOSE_CONNECTION)) {
                            disconnect();
                            break;
                        }
                        if (message.startsWith(AUTH_SUCCESSFUL)) {
                            setAuthorized(true);
                            this.nick = message.split(" ")[1];
                            chatWindowController.switchWindows();
                            break;
                        }
                        chatWindowController.showMessage(message);
                    }
                    while (true) {
                        String message = in.readUTF();
                        if (message.startsWith(SYSTEM_SYMBOL)) {
                            if (message.equalsIgnoreCase(CLOSE_CONNECTION)) break;
                            if (message.startsWith(USERS_LIST)) {
                                String[] users = message.split(" ");
                                Arrays.sort(users);
                                chatWindowController.showUsersList(users);
                            }
                        } else {
                            chatWindowController.showMessage(message);
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    setAuthorized(false);
                    if (!socket.isClosed()) {
                        disconnect();
                    }
                    this.nick = "undefined";
                }
            });
//            thread.setDaemon(true);
            thread.start();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.NONE, "Failed to connect to the server!", ButtonType.OK);
            alert.showAndWait();
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
            out.flush();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.NONE, "Message sending error", ButtonType.OK);
            alert.showAndWait();
        }
    }

    public void auth(String login, String password) {
        try {
            out.writeUTF(AUTH + " " + login + " " + password);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isAuthorized() {
        return isAuthorized;
    }

    public void setAuthorized(boolean authorized) {
        isAuthorized = authorized;
    }

    public String getNick() {
        return nick;
    }

    public Socket getSocket() {
        return socket;
    }

    public Thread getThread() {
        return thread;
    }

    public void disconnect() {
        try {
            in.close();
            System.out.println("in.close");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            out.close();
            System.out.println("out.close");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            socket.close();
            System.out.println("socket.close");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
