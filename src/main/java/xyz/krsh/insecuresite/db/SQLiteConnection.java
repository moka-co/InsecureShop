package xyz.krsh.insecuresite.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;


public class SQLiteConnection {
    private static SQLiteConnection id = null;
    private final String sqlitePath = "/home/mokassino/prog/Java/InsecureWebsite/InsecureShop/src/main/resources/insecureShop.sqlite"; //Why this? This webapp is mean to be runned on Docker
    private Connection connection = null;

    /* 
    Class constructor that connect to a local sqlite db;
    */
    public SQLiteConnection() {


        try {
            // Register the SQLite JDBC driver
        Class.forName("org.sqlite.JDBC");

        // Define the JDBC URL for the SQLite database
        String url = "jdbc:sqlite:" + sqlitePath ;

        // Create a connection
        this.connection = DriverManager.getConnection(url);

        } catch (ClassNotFoundException e) {
            System.out.println("SQLite JDBC driver not found");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Failed to connect to the database");
            e.printStackTrace();
        }
    }

    /*
        Method that implements singleton
    */
    public static SQLiteConnection getInstance(){
        if ( id == null){
            id = new SQLiteConnection();
        }
        return id;
    }

    /*
    Query some data
    */
   public String getDatabaseVersion(){
    
        Statement statement = null;

        try {
            statement = this.connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT sqlite_version()");
            ResultSetMetaData rsmd = resultSet.getMetaData();

            System.out.println("##################### CALLED SQLiteConnection.getDatabaseVersion() ##############");
            String columnName = rsmd.getColumnName(1);
            String columnValue = resultSet.getString(1); //get the version number
            System.out.println( columnName + ' ' + columnValue);
            System.out.println("#################################################################");


            resultSet.close(); //Close the resultSet

        } catch (SQLException e){
            System.out.println("Error cannot connect to database, maybe it's closed??");

        }finally {
            return "test";
        }
   }

}