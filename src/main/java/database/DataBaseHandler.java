package database;

import java.sql.*;

public class DataBaseHandler {
    // Singleton Pattern
    private  static  DataBaseHandler handler;

    // String connect Derby Database with URL or Create new one if not existed yet
    private static final String DB_URL = "jdbc:derby:database;create=true";
    // store the connection between java and database
    private static Connection conn = null;
    // store the statement to the database
    private static Statement stmt = null;

    public DataBaseHandler() {
        createConnection();
        setupBookTable();
    }

    void createConnection() {
        try {
            // fetch driver for database
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
            // connect database with URL
            conn = DriverManager.getConnection(DB_URL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // This method is a part of AddBook statement, so javafx of it written in AddBookController
    // Before save the book, create a table for it
    void setupBookTable() {
        String TABLE_NAME = "BOOK";
        try {
            // Create a variable to store the introduction that user give to database to execute
            stmt = conn.createStatement();
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, TABLE_NAME.toUpperCase(), null);

            // If exist a table already, then return table, else create new table with SQL introduction
            if (tables.next()) {
                System.out.println("Table " + TABLE_NAME + " already exists. Ready for go!");
            } else {
                stmt.execute("CREATE TABLE " + TABLE_NAME + "("
                        + "         id varchar(200) primary key,\n"
                        + "         title varchar(200),\n"
                        + "         author varchar(200),\n"
                        + "         publisher varchar(100),\n"
                        + "         isAvail boolean default true"
                        + " )");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage() + " ... setupDatabase");
        } finally {
        }
    }
}
