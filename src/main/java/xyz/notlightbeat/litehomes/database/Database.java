package xyz.notlightbeat.litehomes.database;

import java.sql.Connection;
import java.sql.SQLException;

abstract public class Database implements IDatabase {

    protected final String databaseUrl;
    protected String username;
    protected String password;
    protected Connection connection;
    public System system;

    public Database(String databaseUrl, System system) {
        this.databaseUrl = databaseUrl;
        this.system = system;
    }

    public Database(String databaseUrl, String username, String password, System system) {
        this.databaseUrl = databaseUrl;
        this.username = username;
        this.password = password;
        this.system = system;
    }

    @Override
    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public enum System {
        SQLITE,
        MYSQL
    }

}
