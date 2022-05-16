package ru.nomad.online_chat.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import ru.nomad.online_chat.ServerAPI;
import ru.nomad.online_chat.ServerConstant;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

public class ChatController implements Initializable, ServerAPI, ServerConstant {
    private Stage stage;
    private ClientConnection client;
    @FXML
    private TextArea correspondenceArea;
    @FXML
    private TextField messageField;
    @FXML
    private ListView<String> usersList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        client = ChatStart.getClient();
        loadHistory();
        client.readingMessages(this);
    }

    public Stage getStage() {
        return stage;
    }

    public void sendMessage(ActionEvent actionEvent) {
        if (!messageField.getText().trim().isEmpty()) {
            String privateMessage = "";
            if (!usersList.getSelectionModel().isEmpty() && !usersList.getSelectionModel().getSelectedItem().equals("All")) {
                privateMessage = PRIVATE_MESSAGE + " " + usersList.getSelectionModel().getSelectedItem() + " ";
            }
            String message = messageField.getText();
            messageField.clear();
            client.sendMessage(privateMessage + message);
        }
    }

    public void showMessage(String message) {
        correspondenceArea.appendText(message + "\n");
    }

    public void showUsersList(String[] users) {
        Platform.runLater(() -> {
            usersList.getItems().clear();
            usersList.getItems().add("All");
            usersList.getItems().addAll(users);
        });
    }

    public void editNickname(ActionEvent actionEvent) {
        this.stage = (Stage) correspondenceArea.getScene().getWindow();
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(ChatStart.class.getResource("edit-nick.fxml"));
        Scene scene;
        try {
            scene = new Scene(fxmlLoader.load(), 250, 150);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        stage.getIcons().add(new Image(Objects.requireNonNull(ChatStart.class.getResourceAsStream("images/on.png"))));
        stage.setTitle("Change nickname");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public void loadHistory() {
        File history = new File("history/history_" + client.getNick() + ".txt");
        if (history.exists()) {
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(history))) {
                String string;
                ArrayList<String> listString = new ArrayList<>();
                while ((string = bufferedReader.readLine()) != null) {
                    listString.add(string);
                }
                if (listString.size() <= LAST_STRINGS_HISTORY) {
                    for (String s : listString) {
                        correspondenceArea.appendText(s + "\n");
                    }
                } else {
                    for (int i = listString.size() - LAST_STRINGS_HISTORY - 1; i < listString.size(); i++) {
                        correspondenceArea.appendText(listString.get(i) + "\n");
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void saveHistory() {
        File file = new File("history/history_" + client.getNick() + ".txt");
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
            bufferedWriter.write(correspondenceArea.getText());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}