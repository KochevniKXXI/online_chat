package ru.nomad.online_chat.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

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
    @FXML
    TextArea usersList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        client = new ClientConnection();
        client.start(this);
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
        if (client.getSocket() == null || client.getSocket().isClosed()) {
            client.start(this);
        }
        client.auth(loginField.getText(), passwordField.getText());
        loginField.clear();
        passwordField.clear();
    }

    public void switchWindows() {
        authorisation.setVisible(!client.isAuthorized());
        sendMessages.setVisible(client.isAuthorized());
        usersList.setVisible(client.isAuthorized());
//        ((Stage) authorisation.getScene().getWindow()).setTitle(client.getNick());
    }

    public void showUsersList(String[] users) {
        usersList.setText("");
        for (int i = 1; i < users.length; i++) {
            usersList.appendText(users[i] + "\n");
        }
    }

    public ClientConnection getClient() {
        return client;
    }
}