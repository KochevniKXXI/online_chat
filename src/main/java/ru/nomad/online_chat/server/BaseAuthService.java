package ru.nomad.online_chat.server;

import java.sql.*;

public class BaseAuthService implements AuthService {
    private Connection connection;
    private Statement statement;

    @Override
    public void start() throws ClassNotFoundException, SQLException {
//        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:users.db");
        statement = connection.createStatement();
        System.out.println("Authentication service started.");

    }

    @Override
    public String getNickByLoginPassword(String login, String password) {
        try {
            ResultSet resultSet = statement.executeQuery("SELECT nickname FROM authentication WHERE login = '" + login + "' AND password = '" + password + "'");
            if (resultSet.next()) return resultSet.getString("nickname");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void stop() {
        try {
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            if (connection != null) {
                connection.close();
            }
            System.out.println("Authentication service stopped.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Statement getStatement() {
        return statement;
    }
}
