package xyz.notlightbeat.litehomes.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * SQL database interface that complies with the CRUD (Create, Read, Update, Delete) model
 */
public interface IDatabase {

    /**
     * Returns the connection for the current database and stores it.
     *
     * @return              the current database connection. Never null
     * @throws SQLException if an exception was thrown while connecting to the database
     */
    Connection getConnection() throws SQLException;

    /**
     * Safely closes the current database connection if it isn't null and not already closed.
     *
     * @throws SQLException if an exception was thrown when closing the connection
     */
    void closeConnection() throws SQLException;

    /**
     * Creates an SQL table on the desired database.
     * Its implementation should access the database through the <code>getConnection()</code> method.
     *
     * @param  table        the name of the table to create
     * @param  cols         the columns of the table using SQL notation
     * @throws SQLException if an exception was thrown while creating the database
     * @see    #getConnection()
     */
    void create(String table, String[] cols) throws SQLException;

    /**
     * Makes a query to the database and returns the ResultSet for it.
     * Its implementation should access the database through the <code>getConnection()</code> method.
     *
     * @param  columns                  the columns to include in the result
     * @param  table                    the table to make the query onto
     * @param  clauses                  the conditions/clauses of the selection using the '?' symbol for each parameter
     * @param  parameters               the parameters for the selection
     * @return                          the result of the query
     * @throws SQLException             if an exception was thrown while making the query
     * @throws IllegalArgumentException if the number of '?' symbols in the clauses and the parameters' length do not match
     * @see    ResultSet
     */
    ResultSet select(String columns, String table, String clauses, String[] parameters) throws SQLException, IllegalArgumentException;

    /**
     * Inserts a row into a table with column names and column data.
     * Its implementation should access the database through the <code>getConnection()</code> method.
     *
     * @param  table                     the table to insert a row into
     * @param  cols                      the columns that will be inserted
     * @param  colData                   the data of each column, in order
     * @throws SQLException             if an exception was thrown while inserting the row
     * @throws IllegalArgumentException if the column and data sizes do not match
     * @see    #getConnection()
     */
    void insert(String table, String[] cols, String[] colData) throws SQLException, IllegalArgumentException;

    /**
     * Updates entries on the desired table under certain clauses/conditions.
     * Its implementation should access the database through the <code>getConnection()</code> method.
     *
     * @param  table                    the table to update entries on
     * @param  columns                  the columns to change data on using the '?' symbol for each parameter
     * @param  clauses                  the conditions/clauses of the update using the '?' symbol for each parameter
     * @param  parameters               the parameters for the update, in order
     * @throws SQLException             if an exception was thrown while updating the entries
     * @throws IllegalArgumentException if the number of '?' symbols in the columns + clauses strings and the parameters' length do not match
     */
    void update(String table, String columns, String clauses, String[] parameters) throws SQLException, IllegalArgumentException;

    /**
     * Deletes entries on the desired table under certain clauses/conditions.
     * Its implementation should access the database through the <code>getConnection()</code> method.
     *
     * @param  table                    the table to delete entries from
     * @param  clauses                  the conditions/clauses of the deletion using the '?' symbol for each parameter
     * @param  parameters               the parameters for the deletion
     * @throws SQLException             if an exception was thrown while deleting the entries
     * @throws IllegalArgumentException if the number of '?' symbols in the clauses and the parameters' length do not match
     */
    void delete(String table, String clauses, String[] parameters) throws SQLException, IllegalArgumentException;

}
