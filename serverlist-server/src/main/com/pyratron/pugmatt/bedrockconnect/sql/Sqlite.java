package main.com.pyratron.pugmatt.bedrockconnect.sql;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Sqlite extends Database {

    final public static String DBFILE_NAME = "settings.db";

    private Connection mConnection;
    private String url;

    /**
     * Create Sqlite instance
     * @param dbPath Database file path
     */
    public Sqlite(String dbPath) {
        url = "jdbc:sqlite:" + dbPath;
    }

    /**
     * Create Sqlite instance
     * Default database file path : $HOME/.bedrockconnect/{@value #DBFILE_NAME}
     */
    public Sqlite() {
        String home = System.getProperty("user.home");
        if (home == null || !new File(home).exists()) {
            home = ".";
        } else {
            home += File.separator + ".bedrockconnect";
            File dir = new File(home);
            if (!dir.exists()) {
                if (!dir.mkdir()) {
                    System.out.println("Can not create directory!! Put database file in current directory!!");
                    home = ".";
                }
            }
        }
        String path = home + File.separator + DBFILE_NAME;
        System.out.println("SQLite db file path: " + path);
        this.url = "jdbc:sqlite:" + path;
    }

    @Override
    public Connection openConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            this.mConnection = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println("ERROR: Could not connect to Sqlite! because: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("ERROR: Sqlite JDBC Driver not found!");
        }
        return this.mConnection;
    }

    @Override
    public boolean checkConnection() {
        return this.mConnection != null;
    }

    @Override
    public Connection getConnection() {
        return this.mConnection;
    }

    @Override
    public void closeConnection() {
        if (this.mConnection != null) {
            try {
                this.mConnection.close();
            } catch (SQLException e) {
                System.out.println("ERROR: Error closing the Sqlite Connection!");
                e.printStackTrace();
            }
        }
    }

    @Override
    public ResultSet querySQL(String query) {
        if (!checkConnection()) {
            openConnection();
        }

        ResultSet rs = null;
        try {
            Statement statement = getConnection().createStatement();
            rs = statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeConnection();

        return rs;
    }

    @Override
    public void updateSQL(String update) {
        if (!checkConnection()) {
            openConnection();
        }

        try {
            Statement statement = getConnection().createStatement();
            statement.executeUpdate(update);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeConnection();
    }
}
