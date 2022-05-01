package ru.nomad.online_chat.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AuthenticationController implements Initializable {
    private ClientConnection client;
    private Stage stage;
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label systemMessage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        client = ChatStart.getClient();
    }

    // Сбор данных аутентификации пользователя и старт подключения при первом вводе
    public void auth(ActionEvent actionEvent) {
        if (client.getSocket() == null || client.getSocket().isClosed()) {
            client.start(this);
        }
        if (!loginField.getText().trim().isEmpty() && !passwordField.getText().trim().isEmpty()) {
            client.auth(loginField.getText(), passwordField.getText());
            loginField.clear();
            passwordField.clear();
// Получить Stage другим способом? (здесь при каждом нажатии лишнее действие - получение Stage)
            stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        }
    }

    // Переключение на сцену Чата
    public void inChat() {
        Platform.runLater(() -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(ChatStart.class.getResource("chat.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 400, 400);
                stage.setTitle("ON-Chat [" + client.getNick() + "]");
                stage.setScene(scene);
                stage.setResizable(true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    // Отображение системных сообщений о некорректном входе
    public void showMessage(String message) {
        Platform.runLater(() -> systemMessage.setText(message));
    }
}
