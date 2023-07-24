package xyz.krsh.insecuresite.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLConnector {
    private static MySQLConnector id = null;
    private final String path = "localhost:3306/insecureshop"; // MySQL server informations
    private Connection connection = null;

    /*
     * Class constructor that connect to a local sqlite db;
     */
    public MySQLConnector() {

        try {
            // Register the SQLite JDBC driver
            // Class.forName("com.mysql.cj.jdbc.Driver");

            // Define the JDBC URL for the MySQL database
            String url = "jdbc:mysql://" + path;

            // Create a connection
            this.connection = DriverManager.getConnection(url, "insecureshop", "insecureshop");
        } catch (SQLException e) {
            System.out.println("Failed to connect to the database");
            e.printStackTrace();
        }
    }

    /*
     * Method that implements singleton
     */
    public static MySQLConnector getInstance() {
        if (id == null) {
            id = new MySQLConnector();
        }
        return id;
    }

    /*
     * Query some data
     */
    public void getDatabaseVersion() {

        Statement statement = null;

        try {
            statement = this.connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT VERSION();");
            ResultSetMetaData rsmd = resultSet.getMetaData();

            if (resultSet.next()) {
                System.out.println("##################### CALLED MySQLConnector.getDatabaseVersion() ##############");
                String columnName = rsmd.getColumnName(1);
                String columnValue = resultSet.getString(1); // get the version number
                System.out.println(columnName + ' ' + columnValue);
                System.out.println("#################################################################");
            }

            resultSet.close(); // Close the resultSet

        } catch (SQLException e) {
            System.out.println(e);
        }

    }

}