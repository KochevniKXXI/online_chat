package ru.nomad.online_chat.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class ChatWindowController implements Initializable {
    private ClientConnection client;
    @FXML
    TextArea correspondenceArea;
    @FXML
    TextField messageField;
    @FXML
    TextField loginField;
    @FXML
    PasswordField passwordField;
    @FXML
    HBox authorisation;
    @FXML
    HBox sendMessages;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        client = new ClientConnection();
        client.init(this);
    }

    public void sendMessage(ActionEvent actionEvent) {
        if (!messageField.getText().trim().isEmpty()) {
            String message = messageField.getText();
            messageField.clear();
            client.sendMessage(message);
        }
    }

    public void showMessage(String message) {
        correspondenceArea.appendText(message + "\n");
    }

    public void auth(ActionEvent actionEvent) {
        client.auth(loginField.getText(), passwordField.getText());
        loginField.setText("");
        passwordField.setText("");
    }

    public void switchWindows() {
        authorisation.setVisible(!client.isAuthorized());
        sendMessages.setVisible(client.isAuthorized());
    }

    public ClientConnection getConnection() {
        return client;
    }

    /*public void openConnection() throws IOException {
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
    }*/
}