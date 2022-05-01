package ru.nomad.online_chat.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import ru.nomad.online_chat.ServerAPI;

import java.io.IOException;
import java.util.Objects;

public class ChatStart extends Application implements ServerAPI {
    private static ClientConnection client;

    public static ClientConnection getClient() {
        return client;
    }

    @Override
    public void init() throws Exception {
        super.init();
        client = new ClientConnection();
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ChatStart.class.getResource("authentication.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 250, 250);
        stage.getIcons().add(new Image(Objects.requireNonNull(ChatStart.class.getResourceAsStream("images/on.png"))));
        stage.setTitle("ON-Chat");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        if (client.getSocket() != null && !client.getSocket().isClosed())
            client.sendMessage(CLOSE_CONNECTION);
    }

    public static void main(String[] args) {
        launch();
    }
}