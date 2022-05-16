package ru.nomad.online_chat.client;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import ru.nomad.online_chat.ServerAPI;
import ru.nomad.online_chat.ServerConstant;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

public class ClientConnection implements ServerConstant, ServerAPI {
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private boolean isAuthorized;
    private String nick;

    // Установка соединения и цикл аутентификации
    public void start(AuthenticationController authenticationController) {
        try {
            setAuthorized(false);
            this.socket = new Socket(SERVER_URL, PORT);
            this.out = new DataOutputStream(socket.getOutputStream());
            this.in = new DataInputStream(socket.getInputStream());
            new Thread(() -> {
                try {
                    while (true) {
                        String message = in.readUTF();
                        if (message.equalsIgnoreCase(CLOSE_CONNECTION)) {
                            disconnect();
                            return;
                        }
                        if (message.startsWith(AUTH_SUCCESSFUL)) {
                            setAuthorized(true);
                            this.nick = message.split(" ")[1];
                            authenticationController.inChat();
                            return;
                        }
                        authenticationController.showMessage(message);
                    }
                } catch (IOException e) {
                    disconnect();
                    throw new RuntimeException("Closing without authentication");
                }
            }).start();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.NONE, "Failed to connect to the server!", ButtonType.OK);
            alert.showAndWait();
            throw new RuntimeException(e);
        }
    }

    public void readingMessages(ChatController controller) {
        new Thread(() -> {
            try {
                while (true) {
                    String message = in.readUTF();
                    if (message.startsWith(SYSTEM_SYMBOL)) {
                        if (message.equalsIgnoreCase(CLOSE_CONNECTION)) {
                            controller.saveHistory();
                            break;
                        }
                        if (message.startsWith(CHANGE_NICKNAME_SUCCESSFUL)) {
                            this.nick = message.split(" ")[1];
                            Platform.runLater(() -> controller.getStage().setTitle("ON-Chat [" + nick + "]"));
                        }
                        if (message.startsWith(USERS_LIST)) {
                            String[] users = message.substring(USERS_LIST.length() + 1).split(" ");
                            Arrays.sort(users);
                            controller.showUsersList(users);
                        }
                    } else {
                        controller.showMessage(message);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                setAuthorized(false);
                disconnect();
                this.nick = "undefined";
            }
        }).start();
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

    public void changeNickname(String nickname) {
        try {
            out.writeUTF(CHANGE_NICKNAME + " " + nickname);
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

    public void disconnect() {
        try {
            in.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
