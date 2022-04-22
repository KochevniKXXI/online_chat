package ru.nomad.online_chat.server;

import ru.nomad.online_chat.ServerAPI;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements ServerAPI {
    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String nick;

    public ClientHandler(Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            nick = "undefined";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        new Thread(() -> {
            try {
                // Авторизация
                while (true) {
                    String message = in.readUTF();
                    if (message.startsWith(AUTH)) {
                        String[] elements = message.split(" ");
                        String nick = server.getAuthService().getNickByLoginPassword(elements[1], elements[2]);
                        if (nick != null) {
                            if (!server.isNickBusy(nick)) {
                                sendMessage(AUTH_SUCCESSFUL + " " + nick);
                                this.nick = nick;
                                server.broadcast(this.nick + " has entered the chat room!");
                                break;
                            } else sendMessage("This account is already in use!");
                        } else sendMessage("Wrong login/password!");
                    } else sendMessage("You should authorize first!");
                    if (message.equalsIgnoreCase(CLOSE_CONNECTION)) disconnect();
                }
                // Отвечает за приём обычных сообщений
                while (true) {
                    String message = in.readUTF();
                    if (message.equalsIgnoreCase(CLOSE_CONNECTION)) break;
                    if (message.startsWith(PRIVATE_MESSAGE)) {
                        String[] elements = message.split(" ");
                        server.broadcast(this.nick, elements[1], this.nick + ": " + elements[2]);
                    } else {
                        System.out.println("Client: " + message);
                        server.broadcast(this.nick + ": " + message);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                disconnect();
            }
        }).start();
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
        sendMessage("You have been disconnect!");
        server.unSubscribeMe(this);
        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
