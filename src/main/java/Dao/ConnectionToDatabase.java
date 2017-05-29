package Dao;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionToDatabase {
    Connection connection() throws SQLException;
}
