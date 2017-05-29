package Dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgreSQLConnection implements ConnectionToDatabase{
    private static final String URL = "jdbc:postgresql://localhost:5432/voting_results";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    @Override
    public Connection connection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
