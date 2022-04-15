package ru.nomad.online_chat.online_chat;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ChatWindowController {
    @FXML
    TextArea correspondenceArea;
    @FXML
    TextField messageField;

    public void sendMessage(ActionEvent actionEvent) {
        correspondenceArea.appendText(messageField.getText() + "\n");
        messageField.clear();
    }
}