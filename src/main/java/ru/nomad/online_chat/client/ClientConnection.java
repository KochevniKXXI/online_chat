package ru.nomad.online_chat.client;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import ru.nomad.online_chat.ServerAPI;
import ru.nomad.online_chat.ServerConstant;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientConnection implements ServerConstant, ServerAPI {
    Socket socket;
    DataOutputStream out;
    DataInputStream in;
    private boolean isAuthorized = false;

    public void init(ChatWindowController chatWindowController) {
        try {
            this.socket = new Socket(SERVER_URL, PORT);
            this.out = new DataOutputStream(socket.getOutputStream());
            this.in = new DataInputStream(socket.getInputStream());
            new Thread(() -> {
                try {
                    while (true) {
                        String message = in.readUTF();
                        if (message.startsWith(AUTH_SUCCESSFUL)) {
                            setAuthorized(true);
                            chatWindowController.switchWindows();
                            break;
                        }
                        chatWindowController.showMessage(message);
                    }
                    while (true) {
                        String message = in.readUTF();
                        if (message.equalsIgnoreCase(CLOSE_CONNECTION)) break;
                        chatWindowController.showMessage(message);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        } catch (IOException e) {
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

    public void disconnect() {
        try {
            out.writeUTF(CLOSE_CONNECTION);
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
