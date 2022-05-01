module ru.nomad.online_chat.online_chat {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens ru.nomad.online_chat to javafx.fxml;
    exports ru.nomad.online_chat;
    exports ru.nomad.online_chat.server;
    opens ru.nomad.online_chat.server to javafx.fxml;
    exports ru.nomad.online_chat.client;
    opens ru.nomad.online_chat.client to javafx.fxml;
}