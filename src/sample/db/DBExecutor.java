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
        String request = Request.SelectByLoginAndPassword(login, password);
        try {
            System.out.println("DBExecutor.getUserByLoginAndPassword..."+login+" "+password);
            ResultSet resultSet = getStatement().executeQuery(request);


            System.out.println(resultSet.getString("nick"));
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

}



