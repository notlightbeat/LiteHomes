package xyz.notlightbeat.litehomes.database;

import xyz.notlightbeat.litehomes.util.StringUtils;

import java.sql.*;

public class SQL extends Database {

    public SQL(String database) {
        super("jdbc:sqlite:" + database + "/", System.SQLITE);
    }

    public SQL(String host, String port, String username, String password, String database) {
        super("jdbc:mysql://" + host + ":" + port + "/" + database, username, password, System.MYSQL);
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (system == System.SQLITE) {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(databaseUrl);
            }
        } else if (system == System.MYSQL) {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(databaseUrl, username, password);
            }
        }
        return connection;
    }

    @Override
    public void create(String table, String[] cols) throws SQLException {
        StringBuilder sql = new StringBuilder("CREATE TABLE IF NOT EXISTS " + table + " (");
        for (int i = 0; i < cols.length - 1; ++i) {
            sql.append(cols[i]).append(", ");
        }
        sql.append(cols[cols.length - 1]).append(");");

        try (Statement statement = getConnection().createStatement()) {
            statement.execute(sql.toString());
        }
    }

    @Override
    public void insert(String table, String[] cols, String[] colData) throws SQLException, IllegalArgumentException {
        if (cols.length != colData.length) {
            throw new IllegalArgumentException("Columns and column data must be the same length.");
        }

        StringBuilder sql = new StringBuilder("INSERT INTO " + table + " (");
        for (int i = 0; i < cols.length - 1; ++i) {
            sql.append(cols[i]).append(", ");
        }
        sql.append(cols[cols.length - 1]).append(") VALUES (");
        for (int i = 0; i < colData.length - 1; ++i) {
            sql.append("?, ");
        }
        sql.append("?);");

        try (PreparedStatement preparedStatement = getConnection().prepareStatement(String.valueOf(sql))) {
            for (int i = 0; i < colData.length; ++i) {
                preparedStatement.setString(i + 1, colData[i]);
            }

            preparedStatement.executeUpdate();
        }
    }

    @Override
    public ResultSet select(String columns, String table, String clauses, String[] parameters) throws SQLException, IllegalArgumentException {
        if (StringUtils.countCharOccurrences(clauses, '?') != parameters.length) {
            throw new IllegalArgumentException("Amount of clauses placeholders and parameters' length do not match.");
        }
        String sql = "SELECT " + columns + " FROM " + table + " " + clauses;

        PreparedStatement preparedStatement = getConnection().prepareStatement(sql);
        for (int i = 0; i < parameters.length; ++i) {
            preparedStatement.setString(i + 1, parameters[i]);
        }
        return preparedStatement.executeQuery();
    }

    @Override
    public void delete(String table, String clauses, String[] parameters) throws SQLException, IllegalArgumentException {
        if (StringUtils.countCharOccurrences(clauses, '?') != parameters.length) {
            throw new IllegalArgumentException("Amount of clauses placeholders and parameters' length do not match.");
        }
        String sql = "DELETE FROM " + table + " " + clauses + ";";

        try (PreparedStatement preparedStatement = getConnection().prepareStatement(sql)) {
            for (int i = 0; i < parameters.length; ++i) {
                preparedStatement.setString(i + 1, parameters[i]);
            }
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void update(String table, String columns, String clauses, String[] parameters) throws SQLException, IllegalArgumentException {
        if (StringUtils.countCharOccurrences(columns, '?') + StringUtils.countCharOccurrences(clauses, '?') != parameters.length) {
            throw new IllegalArgumentException("Amount of columns + clauses placeholders and parameters' length do not match.");
        }
        String sql = "UPDATE " + table + " SET " + columns + " " + clauses + ";";

        try (PreparedStatement preparedStatement = getConnection().prepareStatement(sql)) {
            for (int i = 0; i < parameters.length; ++i) {
                preparedStatement.setString(i + 1, parameters[i]);
            }
            preparedStatement.executeUpdate();
        }
    }


}
