package ru.nomad.online_chat.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.nomad.online_chat.ServerAPI;

import java.io.IOException;
public class ChatApplication extends Application implements ServerAPI {
    private ChatWindowController chatWindowController;
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ChatApplication.class.getResource("chat-window.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 400);
        chatWindowController = fxmlLoader.getController();
        stage.setTitle("ON-Chat");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        chatWindowController.getClient().sendMessage(CLOSE_CONNECTION);
    }

    public static void main(String[] args) {
        launch();
    }
}