package ru.nomad.online_chat.online_chat;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ChatWindowController {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private Thread thread;
    @FXML
    TextArea correspondenceArea;
    @FXML
    TextField messageField;

    public void sendMessage(ActionEvent actionEvent) {
        if (!messageField.getText().trim().isEmpty()) {
            try {
                out.writeUTF(messageField.getText());
                messageField.clear();
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.NONE, "", ButtonType.OK);
                alert.showAndWait();
                throw new RuntimeException(e);
            }
        }
    }

    public void receiveMessage(String message) {
        correspondenceArea.appendText(message + "\n");
    }

    public void openConnection() throws IOException {
        String SERVER_ADDRESS = "localhost";
        int SERVER_PORT = 8189;
        socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        thread = new Thread(() -> {
            try {
                while (true) {
                    String strFromServer = in.readUTF();
                    if (strFromServer.equalsIgnoreCase("/end")) {
                        closeConnection();
                        break;
                    }
                    this.receiveMessage(strFromServer);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        thread.start();
    }

    public void closeConnection() {
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

    public DataOutputStream getOut() {
        return out;
    }

    public Thread getThread() {
        return thread;
    }

    public Socket getSocket() {
        return socket;
    }
}