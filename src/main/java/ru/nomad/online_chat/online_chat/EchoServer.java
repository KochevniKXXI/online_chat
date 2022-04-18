package ru.nomad.online_chat.online_chat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class EchoServer {
    public static void main(String[] args) {
        Socket socket;
        try (ServerSocket serverSocket = new ServerSocket(8189)) {
            System.out.println("Server is run, waiting of connect...");
            socket = serverSocket.accept();
            System.out.println("Client is connect");
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            while (true) {
                String str = in.readUTF();
                if (str.equalsIgnoreCase("/end")) {
                    out.writeUTF(str);
                    break;
                }
                out.writeUTF("Эхо: " + str);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
