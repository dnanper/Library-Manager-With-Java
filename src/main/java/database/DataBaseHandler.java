package database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import ui.listbook.ListBookController;
import ui.listmember.ListMemberController;
import ui.main.MainController;
import ui.settings.Preferences;

import javax.swing.*;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        setupReviewTable();
    }

    // Singleton
    public static DataBaseHandler getInstance() {
        if (handler == null) {
            handler = new DataBaseHandler();
        }
        return handler;
    }

    public Connection getConnection() {
        return conn;
    }

    void createConnection() {
        try {
            // fetch driver for database
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
            // connect database with URL
            conn = DriverManager.getConnection(DB_URL);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Can't load Database", "Database Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
            e.printStackTrace();
        }
    }

    // This method is a part of AddBook statement, so javafx of it written in AddBookController
    // Before save the book, create a table for it
    void setupBookTable() {
        String TABLE_NAME = "BOOK";
        try {

            stmt = conn.createStatement();
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, TABLE_NAME.toUpperCase(), null);

            if (tables.next()) {
                System.out.println("Table " + TABLE_NAME + " already exists. Ready for go!");


                ResultSet columns = dbm.getColumns(null, null, TABLE_NAME.toUpperCase(), "URL");
                if (!columns.next()) {

                    stmt.execute("ALTER TABLE " + TABLE_NAME + " ADD COLUMN url VARCHAR(500)");
                    System.out.println("Column 'url' added to the table.");
                }

                // Check if 'urlCoverImage' column exists
                ResultSet urlCoverImageColumn = dbm.getColumns(null, null, TABLE_NAME.toUpperCase(), "URLCOVERIMAGE");
                if (!urlCoverImageColumn.next()) {

                    stmt.execute("ALTER TABLE " + TABLE_NAME + " ADD COLUMN urlCoverImage VARCHAR(500)");
                    System.out.println("Column 'urlCoverImage' added to the table.");
                }

                // Check if 'description' column exists
                ResultSet descriptionColumn = dbm.getColumns(null, null, TABLE_NAME.toUpperCase(), "DESCRIPTION");
                if (!descriptionColumn.next()) {
                    // If the 'description' column doesn't exist, add it
                    stmt.execute("ALTER TABLE " + TABLE_NAME + " ADD COLUMN description VARCHAR(1000)");
                    System.out.println("Column 'description' added to the table.");
                }

            } else {
                // If the table doesn't exist, create the table with all columns
                stmt.execute("CREATE TABLE " + TABLE_NAME + " ("
                        + "id VARCHAR(200) PRIMARY KEY, "
                        + "title VARCHAR(200), "
                        + "author VARCHAR(200), "
                        + "publisher VARCHAR(100), "
                        + "isAvail BOOLEAN DEFAULT TRUE, "
                        + "genre VARCHAR(100), "
                        + "url VARCHAR(500), "
                        + "urlCoverImage VARCHAR(500), "
                        + "description VARCHAR(3000)"
                        + ")");
                System.out.println("Table " + TABLE_NAME + " created successfully.");
            }
        } catch (SQLException e) {
            System.err.println("Error setting up the database: " + e.getMessage());
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
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

    void setupReviewTable() {
        String TABLE_NAME = "REVIEW";
        try {
            stmt = conn.createStatement();
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, TABLE_NAME.toUpperCase(), null);

            if (tables.next()) {
                System.out.println("Table " + TABLE_NAME + " already exists.");
            } else {
                stmt.execute("CREATE TABLE " + TABLE_NAME + "("
                        + "         userID VARCHAR(200),\n"
                        + "         bookID VARCHAR(200),\n"
                        + "         review VARCHAR(1000),\n"
                        + "         rating DOUBLE CHECK (rating >= 0 AND rating <= 5),\n"
                        + "         PRIMARY KEY (userID, bookID),\n"
                        + "         FOREIGN KEY (userID) REFERENCES MEMBER(id),\n"
                        + "         FOREIGN KEY (bookID) REFERENCES BOOK(id)"
                        + " )");
                System.out.println("Table " + TABLE_NAME + " created.");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage() + " ... setupReviewTable");
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
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

    public boolean deleteBook(ListBookController.Book book) {

        try {
            String delStatement = "DELETE FROM BOOK WHERE ID = ?";
            PreparedStatement stmt = conn.prepareStatement(delStatement);
            stmt.setString(1, book.getId());
            int rw = stmt.executeUpdate();
            if (rw == 1) {
                return true;
            }
        } catch (SQLException e) {
            Logger.getLogger(DataBaseHandler.class.getName()).log(Level.SEVERE, null ,e);
        }
        return false;
    }

    public boolean deleteMember(ListMemberController.Member member) {

        try {
            String delStatement = "DELETE FROM MEMBER WHERE ID = ?";
            PreparedStatement stmt = conn.prepareStatement(delStatement);
            stmt.setString(1, member.getId());
            int rw = stmt.executeUpdate();
            if (rw == 1) {
                return true;
            }
        } catch (SQLException e) {
            Logger.getLogger(DataBaseHandler.class.getName()).log(Level.SEVERE, null ,e);
        }
        return false;
    }

    public boolean isIssued(ListBookController.Book book) {
        try {
            String checkSmt = "SELECT COUNT(*) FROM ISSUE WHERE bookID = ?";
            // Get the string that has same bookID
            PreparedStatement stmt = conn.prepareStatement(checkSmt);
            stmt.setString(1, book.getId());
            ResultSet rs = stmt.executeQuery();
            // If execute query success
            if (rs.next()) {
                int cnt = rs.getInt(1); // get first col
                return (cnt > 0);
            }

        } catch (SQLException e) {
            Logger.getLogger(DataBaseHandler.class.getName()).log(Level.SEVERE, null, e);
        }
        return false;
    }

    public boolean isMemberHasAnyBooks(ListMemberController.Member member) {
        try {
            String checkstmt = "SELECT COUNT(*) FROM ISSUE WHERE memberID=?";
            PreparedStatement stmt = conn.prepareStatement(checkstmt);
            stmt.setString(1, member.getId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println(count);
                return (count > 0);
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(DataBaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean updateBook(ListBookController.Book book) {
        try {
            String update = "UPDATE BOOK SET TITLE = ?, AUTHOR = ?, PUBLISHER = ?, GENRE = ?, url = ?, urlCoverImage = ?, description = ? WHERE ID = ?";
            PreparedStatement stmt = conn.prepareStatement(update);
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getPublisher());
            stmt.setString(4, book.getGenre());
            stmt.setString(5, book.getUrl());
            stmt.setString(6, book.getUrlCoverImage());
            stmt.setString(7, book.getDescription());
            stmt.setString(8, book.getId());

            int res = stmt.executeUpdate();
            return (res > 0);
        } catch (SQLException e) {
            Logger.getLogger(DataBaseHandler.class.getName()).log(Level.SEVERE, null, e);
        }
        return false;
    }

    public boolean updateMember(ListMemberController.Member member) {
        try {
            String update = "UPDATE MEMBER SET NAME = ?, PHONE = ?, EMAIL = ? WHERE ID = ?";
            PreparedStatement stmt = conn.prepareStatement(update);
            stmt.setString(1, member.getName());
            stmt.setString(2, member.getPhone());
            stmt.setString(3, member.getEmail());
            stmt.setString(4, member.getId());
            int res = stmt.executeUpdate();
            return (res > 0);
        } catch (SQLException e) {
            Logger.getLogger(DataBaseHandler.class.getName()).log(Level.SEVERE, null, e);
        }
        return false;
    }

//    public static void main(String[] args) throws Exception {
//        DataBaseHandler.getInstance();
//    }

    public ObservableList<PieChart.Data> getBookGraphicStatics() {
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        try {
            String st1 = "SELECT COUNT(*) FROM BOOK";
            String st2 = "SELECT COUNT(*) FROM ISSUE";
            ResultSet res = execQuery(st1);
            if (res.next()) {
                int cnt = res.getInt(1);
                data.add(new PieChart.Data("Total Books (" + cnt + ")", cnt));
            }
            res = execQuery(st2);
            if (res.next()) {
                int cnt = res.getInt(1);
                data.add(new PieChart.Data("Issued Books (" + cnt + ")", cnt));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return data;
    }

    public ObservableList<PieChart.Data> getMemberGraphicStatics() {
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        try {
            String st1 = "SELECT COUNT(*) FROM MEMBER";
            String st2 = "SELECT COUNT(DISTINCT memberID) FROM ISSUE";
            ResultSet res = execQuery(st1);
            if (res.next()) {
                int cnt = res.getInt(1);
                data.add(new PieChart.Data("Total Members (" + cnt + ")", cnt));
            }
            res = execQuery(st2);
            if (res.next()) {
                int cnt = res.getInt(1);
                data.add(new PieChart.Data("Issuing Members (" + cnt + ")", cnt));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return data;
    }

    public ObservableList<ListMemberController.Member> getOTData() {
        ObservableList<ListMemberController.Member> list = FXCollections.observableArrayList();
        int n = Preferences.getPreferences().getnDaysWithoutFine();
        System.out.println(n);

        String qu = "SELECT MEMBER.id, MEMBER.name, MEMBER.phone, MEMBER.email " +
                "FROM MEMBER INNER JOIN ISSUE ON MEMBER.id = ISSUE.memberID " +
                "WHERE {fn TIMESTAMPDIFF(SQL_TSI_DAY, CAST(ISSUE.issueTime AS DATE), CURRENT_DATE)} >= ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(qu);
            stmt.setInt(1, n);  // Set the overdue days threshold
            ResultSet res = stmt.executeQuery();

            while (res.next()) {
                // Retrieve member data from the result set
                String nam = res.getString("name");
                String pho = res.getString("phone");
                String idx = res.getString("id");
                String ema = res.getString("email");

                // Add the member to the list
                list.add(new ListMemberController.Member(nam, idx, pho, ema));
            }
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

        return list;
    }

    public ObservableList<String> getOTBooks(String memberId) {
        ObservableList<String> otBooks = FXCollections.observableArrayList();
        int n = Preferences.getPreferences().getnDaysWithoutFine();
        String query = "SELECT BOOK.title FROM BOOK INNER JOIN ISSUE ON BOOK.id = ISSUE.bookID " +
                "WHERE ISSUE.memberID = ? AND {fn TIMESTAMPDIFF(SQL_TSI_DAY, ISSUE.issueTime, CURRENT_TIMESTAMP)} >= ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, memberId);
            stmt.setInt(2, n);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                otBooks.add(rs.getString("title"));
            }
        } catch (SQLException e) {
            Logger.getLogger(DataBaseHandler.class.getName()).log(Level.SEVERE, null, e);
        }
        return otBooks;
    }

    public ObservableList<String> getBooksIssuedToMember(String memberId) {
        ObservableList<String> issuedBooks = FXCollections.observableArrayList();
        String query = "SELECT BOOK.title FROM BOOK INNER JOIN ISSUE ON BOOK.id = ISSUE.bookID WHERE ISSUE.memberID = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, memberId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                issuedBooks.add(rs.getString("title"));
            }
        } catch (SQLException e) {
            Logger.getLogger(DataBaseHandler.class.getName()).log(Level.SEVERE, null, e);
        }
        return issuedBooks;
    }

    public ObservableList<String> getRecommendedBooksForMember(String memberId) {
        ObservableList<String> recommendedBooks = FXCollections.observableArrayList();

        String query = "SELECT title FROM BOOK WHERE genre = " +
                "(SELECT genre FROM ( " +
                "   SELECT B.genre, COUNT(*) AS count " +
                "   FROM ISSUE I " +
                "   INNER JOIN BOOK B ON I.bookID = B.id " +
                "   WHERE I.memberID = ? " +
                "   GROUP BY B.genre " +
                "   ORDER BY count DESC " +
                "   FETCH FIRST 1 ROW ONLY " +
                ") AS top_genre) " +
                "FETCH FIRST 5 ROWS ONLY";

        try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
            stmt.setString(1, memberId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String title = rs.getString("title");
                recommendedBooks.add(title);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exception appropriately
        }

        return recommendedBooks;
    }

    public String getTargetEmail(String memberId) {
        String res = "";
        String qu = "SELECT email FROM MEMBER WHERE id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(qu)) {
            stmt.setString(1, memberId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                res = rs.getString("email");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exception appropriately
        }
        return  res;
    }

}
