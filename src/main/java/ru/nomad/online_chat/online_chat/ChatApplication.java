package ru.nomad.online_chat.online_chat;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ChatApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ChatApplication.class.getResource("chat-window.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 240, 320);
        ChatWindowController chatWindowController = fxmlLoader.getController();
        stage.setTitle("Эхо-чат [Nomad]");
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(windowEvent -> {
            if (!chatWindowController.getSocket().isClosed()) {
                try {
                    chatWindowController.getOut().writeUTF("/end");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    chatWindowController.getThread().join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        chatWindowController.openConnection();
    }



    public static void main(String[] args) {
        launch();
    }
}