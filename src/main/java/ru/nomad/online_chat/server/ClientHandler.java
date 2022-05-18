package ru.nomad.online_chat.server;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import ru.nomad.online_chat.ServerAPI;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.sql.Statement;

public class ClientHandler implements ServerAPI {
    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String nick;
    private static final Logger LOGGER = LogManager.getLogger(ClientHandler.class);

    public ClientHandler(Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            this.nick = "undefined";
            server.getPoolConnection().execute(() -> {
                try {
                    authentication();
                    readMessage();
                } catch (IOException e) {
                    LOGGER.info("Closing without authentication");
                } finally {
                    disconnect();
                }
            });
        } catch (IOException e) {
            LOGGER.error("Problems when creating a client handler!");
        }
    }

    public void authentication() throws IOException {
        while (true) {
            socket.setSoTimeout(120000);
            String message = in.readUTF();
            if (message.equalsIgnoreCase(CLOSE_CONNECTION)) {
                LOGGER.debug(this.nick + " disconnect");
                throw new IOException();
            }
            if (message.startsWith(AUTH)) {
                LOGGER.debug(this.nick + " authorized");
                String[] elements = message.split(" ");
                String nick;
                nick = server.getAuthService().getNickByLoginPassword(elements[1], elements[2]);
                if (nick != null) {
                    if (server.isNickBusy(nick)) {
                        sendMessage(AUTH_SUCCESSFUL + " " + nick);
                        this.nick = nick;
                        server.broadcastMessage(this.nick + " has entered the chat room.");
                        server.subscribeClient(this);
                        LOGGER.debug(this.nick + " authorized successful");
                        return;
                    } else sendMessage("This account is already in use!");
                } else sendMessage("Wrong login/password!");
            } else sendMessage("You should authorize first!");
        }
    }

    public void readMessage() throws IOException {
        while (true) {
            socket.setSoTimeout(0);
            String message = in.readUTF();
            if (message.startsWith(SYSTEM_SYMBOL)) {
                if (message.equalsIgnoreCase(CLOSE_CONNECTION)) {
                    LOGGER.debug(this.nick + " disconnect");
                    break;
                }
                else if (message.startsWith(PRIVATE_MESSAGE)) {
                    LOGGER.debug(this.nick + " sent a private message");
                    String nameTo = message.split(" ")[1];
                    String privateMessage = message.substring(nameTo.length() + 4);
                    server.broadcastMassage(this, nameTo, privateMessage);
                } else if (message.startsWith(CHANGE_NICKNAME)) {
                    LOGGER.debug(this.nick + " change nickname");
                    Statement statement = ((BaseAuthService) server.getAuthService()).getStatement();
                    String nick = message.split(" ")[1];
                    try {
                        if (server.isNickBusy(nick)) { // isNickBusyDataBase
                            statement.executeUpdate("UPDATE authentication SET nickname='" + nick + "' WHERE nickname='" + this.nick + "'");
                            sendMessage(CHANGE_NICKNAME_SUCCESSFUL + " " + nick);
                            server.broadcastMessage(this.nick + " changed nick to " + nick);
                            this.nick = nick;
                            server.broadcastUsersList();
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    LOGGER.debug(this.nick + " sent unknown command");
                    sendMessage("Command doesn't exist!");
                }
            } else {
                LOGGER.debug(this.nick + " sent a message");
                server.broadcastMessage(this.nick + ": " + message);
            }
        }
    }

    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNick() {
        return nick;
    }

    public void disconnect() {
        sendMessage("You have been disconnect.");
        server.unsubscribeClient(this);
        if (!this.nick.equals("undefined")) server.broadcastMessage(this.nick + " left the chat.");
        sendMessage(CLOSE_CONNECTION);
        try {
            in.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
