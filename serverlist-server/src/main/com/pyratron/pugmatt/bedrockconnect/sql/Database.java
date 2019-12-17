package main.com.pyratron.pugmatt.bedrockconnect.sql;


import java.sql.Connection;
import java.sql.ResultSet;

/**
 * Abstract Database class, serves as a base for any connection method (MySQL, SQLite, etc.)
 *
 * @author -_Husky_-
 * @author tips48
 */
public abstract class Database {


    /**
     * Opens a connection with the database
     *
     * @return Connection opened
     */
    public abstract Connection openConnection();

    /**
     * Checks if a connection is open with the database
     *
     * @return true if a connection is open
     */
    public abstract boolean checkConnection();

    /**
     * Gets the connection with the database
     *
     * @return Connection with the database, null if none
     */
    public abstract Connection getConnection();

    /**
     * Closes the connection with the database
     */
    public abstract void closeConnection();

    /**
     * Execute query
     * @param query sql
     * @return Resultset
     */
    public abstract ResultSet querySQL(String query);

    /**
     * Execute update
     * @param update sql
     */
    public abstract void updateSQL(String update);

}