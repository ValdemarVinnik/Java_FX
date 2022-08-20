package sample.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DBExecutor {
    private Connection connection = DriverManager.getConnection("jdbc:sqlite:src\\sample\\db\\users.db");

    private Statement statement;

    public getUserByLogin
}
