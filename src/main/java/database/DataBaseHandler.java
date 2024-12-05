package database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import model.Book;
import model.Document;
import model.Paper;
import model.Thesis;
import ui.listbook.ListBookController;
import ui.listmember.ListMemberController;
import ui.main.MainController;
import ui.settings.Preferences;

import javax.swing.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
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
        setupThesisTable();
        setupPaperTable();
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
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
            conn = DriverManager.getConnection(DB_URL);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Can't load Database", "Database Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
            e.printStackTrace();
        }
    }

    /**
     * Sets up the `BOOK` table in the database. Checks if the table exists and, if so, adds any missing columns.
     * If the table doesn't exist, it creates the `BOOK` table with the specified columns.
     */
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

                ResultSet urlCoverImageColumn = dbm.getColumns(null, null, TABLE_NAME.toUpperCase(), "URLCOVERIMAGE");
                if (!urlCoverImageColumn.next()) {
                    stmt.execute("ALTER TABLE " + TABLE_NAME + " ADD COLUMN urlCoverImage VARCHAR(500)");
                    System.out.println("Column 'urlCoverImage' added to the table.");
                }

                ResultSet descriptionColumn = dbm.getColumns(null, null, TABLE_NAME.toUpperCase(), "DESCRIPTION");
                if (!descriptionColumn.next()) {
                    stmt.execute("ALTER TABLE " + TABLE_NAME + " ADD COLUMN description VARCHAR(1000)");
                    System.out.println("Column 'description' added to the table.");
                }

            } else {
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

    /**
     * Sets up the `THESIS` table in the database. Checks if the table exists and, if so, adds any missing columns.
     * If the table doesn't exist, it creates the `THESIS` table with the specified columns.
     */
    void setupThesisTable() {
        String TABLE_NAME = "THESIS";
        try {
            Statement stmt = conn.createStatement();
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, TABLE_NAME.toUpperCase(), null);

            if (tables.next()) {
                System.out.println("Table " + TABLE_NAME + " already exists. Ready for go!");

                ResultSet supcolumns = dbm.getColumns(null, null, TABLE_NAME.toUpperCase(), "UNIVERSITY");
                if (!supcolumns.next()) {
                    stmt.execute("ALTER TABLE " + TABLE_NAME + " ADD COLUMN UNIVERSITY VARCHAR(200)");
                    System.out.println("Column 'UNIVERSITY' added to the table.");
                }

                ResultSet departcolumns = dbm.getColumns(null, null, TABLE_NAME.toUpperCase(), "DEPARTMENT");
                if (!departcolumns.next()) {
                    stmt.execute("ALTER TABLE " + TABLE_NAME + " ADD COLUMN DEPARTMENT VARCHAR(100)");
                    System.out.println("Column 'department' added to the table.");
                }
            } else {

                String createTableSql = "CREATE TABLE " + TABLE_NAME + " ("
                        + "id VARCHAR(200) PRIMARY KEY, "
                        + "title VARCHAR(200), "
                        + "author VARCHAR(200), "
                        + "publisher VARCHAR(100), "
                        + "isAvail BOOLEAN DEFAULT TRUE, "
                        + "genre VARCHAR(100), "
                        + "university VARCHAR(200), "
                        + "department VARCHAR(100)"
                        + ")";
                stmt.execute(createTableSql);
                System.out.println("Table " + TABLE_NAME + " created successfully.");
            }
        } catch (SQLException e) {
            System.err.println("Error setting up the database: " + e.getMessage());
        }
    }

    /**
     * Sets up the `PAPER` table in the database. Checks if the table exists and, if so, adds any missing columns.
     * If the table doesn't exist, it creates the `PAPER` table with the specified columns.
     */
    void setupPaperTable() {
        String TABLE_NAME = "PAPER";
        try {
            Statement stmt = conn.createStatement();
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, TABLE_NAME.toUpperCase(), null);

            if (tables.next()) {
                System.out.println("Table " + TABLE_NAME + " already exists. Ready for go!");

                ResultSet concolumns = dbm.getColumns(null, null, TABLE_NAME.toUpperCase(), "CONFERENCE");
                if (!concolumns.next()) {
                    stmt.execute("ALTER TABLE " + TABLE_NAME + " ADD COLUMN CONFERENCE VARCHAR(200)");
                    System.out.println("Column 'conference' added to the table.");
                }

                ResultSet yearcolumns = dbm.getColumns(null, null, TABLE_NAME.toUpperCase(), "RELEASE_YEAR");
                if (!yearcolumns.next()) {
                    stmt.execute("ALTER TABLE " + TABLE_NAME + " ADD COLUMN RELEASE_YEAR INTEGER");
                    System.out.println("Column 'release_year' added to the table.");
                }
            } else {
                String createTableSql = "CREATE TABLE " + TABLE_NAME + " ("
                        + "id VARCHAR(200) PRIMARY KEY, "
                        + "title VARCHAR(200), "
                        + "author VARCHAR(200), "
                        + "publisher VARCHAR(100), "
                        + "isAvail BOOLEAN DEFAULT TRUE, "
                        + "genre VARCHAR(100), "
                        + "conference VARCHAR(200), "
                        + "release_year INTEGER"
                        + ")";
                stmt.execute(createTableSql);
                System.out.println("Table " + TABLE_NAME + " created successfully.");
            }
        } catch (SQLException e) {
            System.err.println("Error setting up the database: " + e.getMessage());
        }
    }

    /**
     * Sets up the `MEMBER` table in the database. Checks if the table exists and, if it doesn't, creates the `MEMBER` table with the specified columns.
     */
    void setupMemberTable() {
        String TABLE_NAME = "MEMBER";
        try {
            stmt = conn.createStatement();
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, TABLE_NAME.toUpperCase(), null);

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

    /**
     * Sets up the `ISSUE` table in the database. Checks if the table exists and, if it doesn't, creates the `ISSUE` table with the specified columns
     * and foreign key references to the `BOOK` and `MEMBER` tables.
     */
    void setupIssueTable() {
        String TABLE_NAME = "ISSUE";
        try {
            stmt = conn.createStatement();
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, TABLE_NAME.toUpperCase(), null);

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

    /**
     * Sets up the `REVIEW` table in the database. Checks if the table exists and, if it doesn't, creates the `REVIEW` table with the specified columns,
     * including an auto-generated primary key and constraints on the `rating` column.
     */
    void setupReviewTable() {
        String TABLE_NAME = "REVIEW";
        try {

            stmt = conn.createStatement();
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, TABLE_NAME.toUpperCase(), null);

            if (tables.next()) {
                System.out.println("Table " + TABLE_NAME + " already exists. Ready for go!");
            } else {
                stmt.execute("CREATE TABLE " + TABLE_NAME + " ("
                        + " reviewID INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,"
                        + " userID VARCHAR(200),"
                        + " bookID VARCHAR(200),"
                        + " review VARCHAR(1000),"
                        + " rating DOUBLE CHECK (rating >= 0 AND rating <= 5)"
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

    /**
     * Executes a SQL query on the database and returns the result set.
     *
     * @param query The SQL query to be executed.
     * @return The `ResultSet` containing the query results if the execution is successful. In case of a `SQLException`, it prints the error message
     *         and returns `null`.
     */
    public ResultSet execQuery(String query) {
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

    /**
     * Executes a SQL statement (e.g., INSERT, UPDATE, DELETE) on the database.
     *
     * @param qu The SQL statement to be executed.
     * @return `true` if the statement is executed successfully, `false` otherwise. In case of a `SQLException`, it shows an error message dialog,
     *         prints the error message, and returns `false`.
     */
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


    public boolean deleteBook(Book book) {

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

    public boolean deleteThesis(Thesis thesis) {
        try {
            String delStatement = "DELETE FROM THESIS WHERE ID = ?";
            PreparedStatement stmt = conn.prepareStatement(delStatement);
            stmt.setString(1, thesis.getId());
            int rw = stmt.executeUpdate();
            if (rw == 1) {
                return true;
            }
        } catch (SQLException e) {
            Logger.getLogger(DataBaseHandler.class.getName()).log(Level.SEVERE, null, e);
        }
        return false;
    }

    public boolean deletePaper(Paper paper) {
        try {
            String delStatement = "DELETE FROM PAPER WHERE ID = ?";
            PreparedStatement stmt = conn.prepareStatement(delStatement);
            stmt.setString(1, paper.getId());
            int rw = stmt.executeUpdate();
            if (rw == 1) {
                return true;
            }
        } catch (SQLException e) {
            Logger.getLogger(DataBaseHandler.class.getName()).log(Level.SEVERE, null, e);
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

    public boolean isIssued(Book book) {
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
                return (count > 0);
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(DataBaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean updateDocument(Document document) {
        if (document instanceof Book) {
            return updateBook((Book) document);
        } else if (document instanceof Thesis) {
            return updateThesis((Thesis) document);
        } else if (document instanceof Paper) {
            return updatePaper((Paper) document);
        }
        return false;
    }

    public boolean updateBook(Book book) {
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

    private boolean updateThesis(Thesis thesis) {
        try {
            String update = "UPDATE THESIS SET TITLE =?, AUTHOR =?, UNIVERSITY =?, GENRE =?, DEPARTMENT =? WHERE ID =?";
            PreparedStatement stmt = conn.prepareStatement(update);
            stmt.setString(1, thesis.getTitle());
            stmt.setString(2, thesis.getAuthor());
            stmt.setString(3, thesis.getUniversity());
            stmt.setString(4, thesis.getGenre());
            stmt.setString(5, thesis.getDepartment());
            stmt.setString(6, thesis.getId());

            int res = stmt.executeUpdate();
            return (res > 0);
        } catch (SQLException e) {
            Logger.getLogger(DataBaseHandler.class.getName()).log(Level.SEVERE, null, e);
        }
        return false;
    }

    private boolean updatePaper(Paper paper) {
        try {
            String update = "UPDATE PAPER SET TITLE =?, AUTHOR =?, CONFERENCE =?, GENRE =?, YEAR =? WHERE ID =?";
            PreparedStatement stmt = conn.prepareStatement(update);
            stmt.setString(1, paper.getTitle());
            stmt.setString(2, paper.getAuthor());
            stmt.setString(3, paper.getConference());
            stmt.setString(4, paper.getGenre());
            stmt.setString(5, paper.getYear());
            stmt.setString(6, paper.getId());

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

        String qu = "SELECT DISTINCT MEMBER.id, MEMBER.name, MEMBER.phone, MEMBER.email " +
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

    public ObservableList<Book> getBooksIssuedToMember(String memberId) {
        ObservableList<Book> issuedBooks = FXCollections.observableArrayList();
        String query = "SELECT * FROM BOOK INNER JOIN ISSUE ON BOOK.id = ISSUE.bookID WHERE ISSUE.memberID = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, memberId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String tit = rs.getString("title");
                String aut = rs.getString("author");
                String idx = rs.getString("id");
                String pub = rs.getString("publisher");
                String gen = rs.getString("genre"); // Lấy genre từ kết quả truy vấn
                Boolean ava = rs.getBoolean("isAvail");
                issuedBooks.add(new Book(tit, aut, idx, gen, pub, ava,null,null,null));
            }
        } catch (SQLException e) {
            Logger.getLogger(DataBaseHandler.class.getName()).log(Level.SEVERE, null, e);
        }
        return issuedBooks;
    }

    public ObservableList<Book> getRecommendedBooksForMember(String memberId) {
        ObservableList<Book> recommendedBooks = FXCollections.observableArrayList();

        String query = "SELECT * FROM BOOK WHERE genre = " +
                "(SELECT genre FROM ( " +
                "   SELECT B.genre, COUNT(*) AS count " +
                "   FROM ISSUE I " +
                "   INNER JOIN BOOK B ON I.bookID = B.id " +
                "   WHERE I.memberID = ? " +
                "   GROUP BY B.genre " +
                "   ORDER BY count DESC " +
                "   FETCH FIRST 1 ROW ONLY " +
                ") AS top_genre) ";

        try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
            stmt.setString(1, memberId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String tit = rs.getString("title");
                String aut = rs.getString("author");
                String idx = rs.getString("id");
                String pub = rs.getString("publisher");
                String gen = rs.getString("genre");
                Boolean ava = rs.getBoolean("isAvail");
                recommendedBooks.add(new Book(tit, aut, idx, gen, pub, ava,null,null,null));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return recommendedBooks;
    }

    public ObservableList<Book> getRecommendedBooksForMember(String memberId, String genr) {
        ObservableList<Book> recommendedBooks = FXCollections.observableArrayList();

        String query = "SELECT * FROM BOOK WHERE LOWER(genre) = LOWER(?)";

        try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
            stmt.setString(1, genr);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String tit = rs.getString("title");
                String aut = rs.getString("author");
                String idx = rs.getString("id");
                String pub = rs.getString("publisher");
                String gen = rs.getString("genre");
                Boolean ava = rs.getBoolean("isAvail");
                recommendedBooks.add(new Book(tit, aut, idx, gen, pub, ava,null,null,null));
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }
        return  res;
    }

    public ObservableList<String> getBookReview(String bookId) {
        ObservableList<String> reviews = FXCollections.observableArrayList();
        String query = "SELECT review, userID FROM REVIEW WHERE bookID = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, bookId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String review = rs.getString("review");
                String userId = rs.getString("userID");
                reviews.add("\"" + review + "\" by \"" + userId + "\"");
            }
        } catch (SQLException e) {
            Logger.getLogger(DataBaseHandler.class.getName()).log(Level.SEVERE, null, e);
        }
        return reviews;
    }

    public Map<String, Double> getBookRatings(String bookId) {
        Map<String, Double> ratingsData = new HashMap<>();
        String query = "SELECT AVG(rating) AS averageRating, COUNT(rating) AS totalRatings FROM REVIEW WHERE bookID = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, bookId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                double averageRating = rs.getDouble("averageRating");
                int totalRatings = rs.getInt("totalRatings");

                ratingsData.put("averageRating", averageRating);
                ratingsData.put("totalRatings", (double) totalRatings);
            }
        } catch (SQLException e) {
            Logger.getLogger(DataBaseHandler.class.getName()).log(Level.SEVERE, null, e);
            ratingsData.put("averageRating", 0.0);
            ratingsData.put("totalRatings", 0.0);
        }

        return ratingsData;
    }

    public String getValueById(String bookId, String columnName) {
        String query = "SELECT " + columnName + " FROM BOOK WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, bookId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString(columnName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
