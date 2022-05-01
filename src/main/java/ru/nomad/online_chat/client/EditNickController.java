package ru.nomad.online_chat.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class EditNickController implements Initializable {
    @FXML
    private TextField nickField;
    private ClientConnection client;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        client = ChatStart.getClient();
    }

    public void editNickname(ActionEvent actionEvent) {
        if (!nickField.getText().trim().isEmpty()) {
            client.changeNickname(nickField.getText());
            nickField.clear();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.close();
        }
    }

    public void cancel(ActionEvent actionEvent) {
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.close();
    }
}
