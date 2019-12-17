package main.com.pyratron.pugmatt.bedrockconnect;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

import main.com.pyratron.pugmatt.bedrockconnect.sql.Data;
import main.com.pyratron.pugmatt.bedrockconnect.sql.Database;
import main.com.pyratron.pugmatt.bedrockconnect.sql.MySQL;
import main.com.pyratron.pugmatt.bedrockconnect.sql.Sqlite;
import main.com.pyratron.pugmatt.bedrockconnect.utils.PaletteManager;

public class BedrockConnect {


    public static PaletteManager paletteManager;

    public static Database dao;
    public static Connection connection;
    public static Data data;

    public static Server server;

    public static void main(String[] args) {
        paletteManager =  new PaletteManager();

        try {
            String dbType = "sqlite";

            String sqliteFilePath = null;

            String hostname = "localhost";
            String database = "bedrock-connect";
            String username = "root";
            String password = "";
            String port = "19132";

            String serverLimit = "100";

            for(String str : args) {
                if(str.startsWith("mysql_host="))
                    hostname = getArgValue(str, "mysql_host");
                if(str.startsWith("mysql_db="))
                    database = getArgValue(str, "mysql_db");
                if(str.startsWith("mysql_user="))
                    username = getArgValue(str, "mysql_user");
                if(str.startsWith("mysql_pass="))
                    password = getArgValue(str, "mysql_pass");
                if(str.startsWith("server_limit="))
                    serverLimit = getArgValue(str, "server_limit");
                if(str.startsWith("port="))
                    port = getArgValue(str, "port");
                if (str.startsWith("db_type="))
                    dbType = getArgValue(str, "db_type");
                if (str.startsWith("sqlite_path="))
                    sqliteFilePath = getArgValue(str, "sqlite_path");
            }

            if ("mysql".equals(dbType)) {
                System.out.println("MySQL Host: " + hostname + "\n" +
                        "MySQL Database: " + database + "\n" +
                        "MySQL User: " + username);
            } else if ("sqlite".equals(dbType)) {
                System.out.println("Using SQLite Database");
            }

            System.out.println("Server Limit: " + serverLimit + "\n" +
                    "Port: " + port + "\n");

            if ("sqlite".equals(dbType)) {
                if (sqliteFilePath != null && sqliteFilePath.length() > 0)
                    dao = new Sqlite(sqliteFilePath);
                else
                    dao = new Sqlite();
            } else if ("mysql".equals(dbType)) {
                dao = new MySQL(hostname, database, username, password);
            } else {
                System.out.println("Unsupported Database \"" + dbType + "\"");
            }

            connection = null;

            connection = dao.openConnection();

            data = new Data(serverLimit, dbType);

            // Keep MySQL connection alive
            Timer timer = new Timer();
            TimerTask task = new TimerTask() {
                int sec;

                public void run() {
                    try {
                        if (connection == null || connection.isClosed()) {
                            connection = dao.openConnection();
                        } else {
                            if (sec == 600) {
                                try {
                                    ResultSet rs = connection
                                            .createStatement()
                                            .executeQuery(
                                                    "SELECT 1");
                                    rs.next();
                                } catch (SQLException e) {
                                    // TODO Auto-generated
                                    // catch block
                                    e.printStackTrace();
                                }
                                sec = 0;
                            }
                        }
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    sec++;
                }
            };
            timer.scheduleAtFixedRate(task, 0L, 1200L);


            server = new Server(port);
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    public static String getArgValue(String str, String name) {
        String target = name + "=";
        int index = str.indexOf(target);
        int subIndex = index + target.length();
        return str.substring(subIndex);
    }

}
