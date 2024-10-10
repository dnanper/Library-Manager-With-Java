package database;

import javax.swing.*;
import java.sql.*;

public class DataBaseHandler {
    // Singleton Pattern
    private  static  DataBaseHandler handler = null;

    // String connect Derby Database with URL or Create new one if not existed yet
    private static final String DB_URL = "jdbc:derby:database;create=true";
    // store the connection between java and database
    private static Connection conn = null;
    // store the statement to the database
    private static Statement stmt = null;

    private DataBaseHandler() {
        createConnection();
        setupBookTable();
        setupMemberTable();
        setupIssueTable();
    }

    // Singleton
    public static DataBaseHandler getInstance() {
        if (handler == null) {
            handler = new DataBaseHandler();
        }
        return handler;
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

    void setupMemberTable() {
        String TABLE_NAME = "MEMBER";
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
                        + "         name varchar(200),\n"
                        + "         phone varchar(30),\n"
                        + "         email varchar(100)\n"
                        + " )");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage() + " ... setupDatabase");
        } finally {
        }
    }

    // prepare table for book-member table
    void setupIssueTable() {
        String TABLE_NAME = "ISSUE";
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
                        + "         bookID varchar(200) primary key,\n"
                        + "         memberID varchar(200),\n"
                        + "         issueTime timestamp default CURRENT_TIMESTAMP,\n"
                        + "         renew_count integer default 0,\n"
                        + "         FOREIGN KEY (bookID) REFERENCES BOOK(id),\n"
                        + "         FOREIGN KEY (memberID) REFERENCES MEMBER(id)"
                        + " )");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage() + " ... setupDatabase");
        } finally {
        }
    }

    // return the pointer to the result table of statement "query"
    // execQuery: get something from SQL database
    public ResultSet execQuery(String query) {
        // maintain cursor point to the first row of data table that gain after execute statement
        ResultSet result;
        try {
            stmt = conn.createStatement();
            result = stmt.executeQuery(query);
        } catch (SQLException ex) {
            System.out.println("Exception at execQuery: dataHandler" + ex.getLocalizedMessage());
            return null;
        } finally {
        }
        return result;
    }

    // check if we can execute statement "qu"
    // execAction: add/update something to SQL database
    public boolean execAction(String qu) {
        try {
            stmt = conn.createStatement();
            stmt.execute(qu);
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Error Occured", JOptionPane.ERROR_MESSAGE);
            System.out.println("Exception at execAction: dataHandler" + e.getLocalizedMessage());
            return false;
        } finally {
        }
    }
}
