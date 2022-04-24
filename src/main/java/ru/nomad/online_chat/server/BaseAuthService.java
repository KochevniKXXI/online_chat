package ru.nomad.online_chat.server;

import java.util.ArrayList;

public class BaseAuthService implements AuthService {
    private class Entry {
        private String login;
        private String password;
        private String nick;

        public Entry(String login, String password, String nick) {
            this.login = login;
            this.password = password;
            this.nick = nick;
        }
    }

    private ArrayList<Entry> entries;

    public BaseAuthService() {
        this.entries = new ArrayList<>();
        entries.add(new Entry("login1", "pass1", "KochevniKXXI"));
        entries.add(new Entry("login2", "pass2", "Thirteen"));
        entries.add(new Entry("login3", "pass3", "BV"));
    }

    @Override
    public void start() {
        System.out.println("Authentication service started.");
    }

    @Override
    public String getNickByLoginPassword(String login, String password) {
        for (Entry entry : entries) {
            if (entry.login.equals(login) && entry.password.equals(password)) return entry.nick;
        }
        return null;
    }

    @Override
    public void stop() {
        System.out.println("Authentication service stopped.");
    }
}
