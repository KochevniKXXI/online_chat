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
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            this.nick = "undefined";
            new Thread(() -> {
                try {
                    authentication();
                    readMessage();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    if (!socket.isClosed()) {
                        disconnect();
                    }
                }
            }).start();
        } catch (IOException e) {
            throw new RuntimeException("Problems when creating a client handler!");
        }
    }

    public void authentication() throws IOException {
        while (true) {
            socket.setSoTimeout(120000);
            String message = in.readUTF();
            if (message.equalsIgnoreCase(CLOSE_CONNECTION)) {
                disconnect();
                break;
            }
            if (message.startsWith(AUTH)) {
                String[] elements = message.split(" ");
                String nick = server.getAuthService().getNickByLoginPassword(elements[1], elements[2]);
                if (nick != null) {
                    if (!server.isNickBusy(nick)) {
                        sendMessage(AUTH_SUCCESSFUL + " " + nick);
                        this.nick = nick;
                        server.broadcastMessage(this.nick + " has entered the chat room.");
                        server.subscribeClient(this);
                        break;
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
                if (message.equalsIgnoreCase(CLOSE_CONNECTION)) break;
                else if (message.startsWith(PRIVATE_MESSAGE)) {
                    String nameTo = message.split(" ")[1];
                    String privateMessage = message.substring(nameTo.length() + 4);
                    server.broadcastMassage(this, nameTo, privateMessage);
                } else {
                    sendMessage("Command doesn't exist!");
                }
            } else {
                System.out.println("from " + this.nick + ": " + message);
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
        server.broadcastMessage(this.nick + " left the chat.");
        sendMessage(CLOSE_CONNECTION);
        try {
            in.close();
            System.out.println("in.close");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            out.close();
            System.out.println("out.close");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            socket.close();
            System.out.println("socket.close");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
