package sample.db;

import sample.Model.User;

import java.sql.*;

public class DBExecutor {
    private final String DB_PATH = "jdbc:sqlite:src\\sample\\db\\users.db";
    private Connection connection;

    private Statement statement;

    private Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(DB_PATH);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return connection;
    }

    private Statement getStatement() {
        if (statement == null) {
            try {
                statement = getConnection().createStatement();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return statement;
    }

    private void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User getUserByLoginAndPassword(String login, String password) {
        String request = Request.selectByLoginAndPassword(login, password);
        try {
            ResultSet resultSet = getStatement().executeQuery(request);
            return new User(resultSet.getString("nick"),
                    resultSet.getString("login"),
                    resultSet.getString("password"));
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
        return null;
    }

    public boolean registrationNewUser(String nick, String login, String password) {
        String selectRequest = Request.selectByNick(nick);

        try {
            ResultSet resultSet = getStatement().executeQuery(selectRequest);

            if (resultSet != null) {
                return insertNewUser(nick, login, password);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean insertNewUser(String nick, String login, String password) {
        String insertRequest = Request.insertNewUser(nick, login, password);

        try {
            int addedUser = getStatement().executeUpdate(insertRequest);
            return (addedUser == 1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}



