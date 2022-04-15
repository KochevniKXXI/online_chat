module ru.nomad.online_chat.online_chat {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;

    opens ru.nomad.online_chat.online_chat to javafx.fxml;
    exports ru.nomad.online_chat.online_chat;
}