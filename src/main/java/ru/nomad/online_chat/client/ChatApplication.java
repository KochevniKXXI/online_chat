package ru.nomad.online_chat.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
public class ChatApplication extends Application {
    private ChatWindowController chatWindowController;
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ChatApplication.class.getResource("chat-window.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 400);
        chatWindowController = fxmlLoader.getController();
        stage.setTitle("Эхо-чат [Nomad]");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        chatWindowController.getConnection().disconnect();
    }

    public static void main(String[] args) {
        launch();
    }
}