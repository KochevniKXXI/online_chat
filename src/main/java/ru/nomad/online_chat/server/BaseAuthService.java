package ru.nomad.online_chat.server;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.*;

public class BaseAuthService implements AuthService {
    private Connection connection;
    private Statement statement;
    private static final Logger LOGGER = LogManager.getLogger(BaseAuthService.class);

    @Override
    public void start() throws ClassNotFoundException, SQLException {
//        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:users.db");
        statement = connection.createStatement();
        LOGGER.info("Authentication service started.");
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
            LOGGER.info("Authentication service stopped.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Statement getStatement() {
        return statement;
    }
}
