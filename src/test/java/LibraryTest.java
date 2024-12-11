import database.DataBaseHandler;
import model.Book;
import model.Paper;
import model.Thesis;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class LibraryTest {
    private static DataBaseHandler dbHandler;

    @BeforeAll
    static void setup() {
        dbHandler = DataBaseHandler.getInstance();
        dbHandler.execAction("DELETE FROM BOOK");
        dbHandler.execAction("DELETE FROM THESIS");
        dbHandler.execAction("DELETE FROM PAPER");
    }

    @AfterEach
    void cleanup() {
        dbHandler.execAction("DELETE FROM BOOK");
        dbHandler.execAction("DELETE FROM THESIS");
        dbHandler.execAction("DELETE FROM PAPER");
    }

    @Test
    @DisplayName("Test Thesis Insertion")
    public void testAddThesis() {
        // add thesis
        Thesis thesis = new Thesis("Thesis Title", "Thesis Author", "12345", "Computer Science", "Test University", "Test Department", true);
        boolean result = dbHandler.execAction("INSERT INTO THESIS (id, title, author, department, university, publisher) VALUES ('" +
                thesis.getId() + "', '" + thesis.getTitle() + "', '" + thesis.getAuthor() + "', '" + thesis.getDepartment() + "', '" +
                thesis.getUniversity() + "', '" + thesis.getDepartment() + "')");
        assertTrue(result, "Thesis insertion should succeed");

        // test
        ResultSet rs = dbHandler.execQuery("SELECT * FROM THESIS WHERE id = '12345'");
        try {
            assertTrue(rs.next(), "Thesis should exist in the database");
            assertEquals("Thesis Title", rs.getString("title"), "Thesis title should match");
        } catch (SQLException e) {
            fail("SQLException occurred while verifying thesis insertion: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
            } catch (SQLException ignored) {
            }
        }
    }




    @Test
    @DisplayName("Test Thesis Deletion")
    public void testDeleteThesis() {
        // add thesis
        Thesis thesis = new Thesis("Thesis Title", "Thesis Author", "12345", "Computer Science", "Test University", "Test Department", true);
        dbHandler.execAction("INSERT INTO THESIS (id, title, author, department, university, publisher) VALUES ('" +
                thesis.getId() + "', '" + thesis.getTitle() + "', '" + thesis.getAuthor() + "', '" + thesis.getDepartment() + "', '" +
                thesis.getUniversity() + "', '" + thesis.getDepartment() + "')");

        // delete
        boolean result = dbHandler.execAction("DELETE FROM THESIS WHERE id = '" + thesis.getId() + "'");
        assertTrue(result, "Thesis deletion should succeed");

        // test
        ResultSet rs = dbHandler.execQuery("SELECT * FROM THESIS WHERE id = '12345'");
        try {
            assertFalse(rs.next(), "Thesis should no longer exist in the database");
        } catch (SQLException e) {
            fail("SQLException occurred while verifying thesis deletion: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
            } catch (SQLException ignored) {
            }
        }
    }

    @Test
    @DisplayName("Test Paper Update")
    public void testUpdatePaper() {
        // add paper
        dbHandler.execAction("INSERT INTO PAPER (id, title, author, conference, release_year, genre) VALUES ('12345', 'Original Title', 'Original Author', 'Original Conference', 2023, 'Original Genre')");

        // update paper
        Paper updatedPaper = new Paper("Updated Title", "Updated Author", "12345", "Updated Genre", "Updated Conference", "2024", true);
        boolean result = dbHandler.execAction("UPDATE PAPER SET title = '" + updatedPaper.getTitle() + "', author = '" +
                updatedPaper.getAuthor() + "', conference = '" + updatedPaper.getConference() + "', release_year = " + updatedPaper.getYear() +
                ", genre = '" + updatedPaper.getGenre() + "' WHERE id = '" + updatedPaper.getId() + "'");
        assertTrue(result, "Paper update should succeed");

        // test
        ResultSet rs = dbHandler.execQuery("SELECT * FROM PAPER WHERE id = '12345'");
        try {
            assertTrue(rs.next(), "Updated paper should exist in the database");
            assertEquals("Updated Title", rs.getString("title"), "Paper title should be updated");
            assertEquals(2024, rs.getInt("release_year"), "Paper year should be updated");
        } catch (SQLException e) {
            fail("SQLException occurred while verifying paper update: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
            } catch (SQLException ignored) {
            }
        }
    }

    @Test
    @DisplayName("Test Paper Insertion")
    public void testAddPaper() {
        // add paper
        Paper paper = new Paper("Paper Title", "Paper Author", "12345", "AI Genre", "AI Conference", "2024", true);
        boolean result = dbHandler.execAction("INSERT INTO PAPER (id, title, author, conference, release_year, genre) VALUES ('" +
                paper.getId() + "', '" + paper.getTitle() + "', '" + paper.getAuthor() + "', '" + paper.getConference() + "', " +
                paper.getYear() + ", '" + paper.getGenre() + "')");
        assertTrue(result, "Paper insertion should succeed");

        // test
        ResultSet rs = dbHandler.execQuery("SELECT * FROM PAPER WHERE id = '12345'");
        try {
            assertTrue(rs.next(), "Paper should exist in the database");
            assertEquals("Paper Title", rs.getString("title"), "Paper title should match");
            assertEquals("Paper Author", rs.getString("author"), "Paper author should match");
            assertEquals("AI Conference", rs.getString("conference"), "Paper conference should match");
            assertEquals(2024, rs.getInt("release_year"), "Paper year should match");
            assertEquals("AI Genre", rs.getString("genre"), "Paper genre should match");
        } catch (SQLException e) {
            fail("SQLException occurred while verifying paper insertion: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
            } catch (SQLException ignored) {
            }
        }
    }



    @Test
    @DisplayName("Test Book Insertion")
    public void testAddBook() {
        // add
        Book book = new Book("Test Book", "Test Author", "12345", "Test Genre", "Test Publisher", true, null, null, null);
        boolean result = dbHandler.execAction("INSERT INTO BOOK (id, title, author, publisher, genre, isAvail) VALUES ('" +
                book.getId() + "', '" + book.getTitle() + "', '" + book.getAuthor() + "', '" + book.getPublisher() + "', '" +
                book.getGenre() + "', true)");
        assertTrue(result, "Book insertion should succeed");

        // test
        ResultSet rs = dbHandler.execQuery("SELECT * FROM BOOK WHERE id = '12345'");
        try {
            assertTrue(rs.next(), "Book should exist in the database");
            assertEquals("Test Book", rs.getString("title"), "Book title should match");
        } catch (SQLException e) {
            fail("SQLException occurred while verifying book insertion: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
            } catch (SQLException ignored) {
            }
        }
    }

    @Test
    @DisplayName("Test Book Update")
    public void testUpdateBook() {
        // add book
        dbHandler.execAction("INSERT INTO BOOK (id, title, author, publisher, genre, isAvail) VALUES ('12345', 'Original Title', 'Original Author', 'Original Publisher', 'Original Genre', true)");

        // update
        Book updatedBook = new Book("Updated Title", "Updated Author", "12345", "Updated Genre", "Updated Publisher", true, null, null, null);
        boolean result = dbHandler.execAction("UPDATE BOOK SET title = '" + updatedBook.getTitle() +
                "', author = '" + updatedBook.getAuthor() + "', publisher = '" + updatedBook.getPublisher() +
                "', genre = '" + updatedBook.getGenre() + "' WHERE id = '" + updatedBook.getId() + "'");
        assertTrue(result, "Book update should succeed");

        //test
        ResultSet rs = dbHandler.execQuery("SELECT * FROM BOOK WHERE id = '12345'");
        try {
            assertTrue(rs.next(), "Updated book should exist in the database");
            assertEquals("Updated Title", rs.getString("title"), "Book title should be updated");
        } catch (SQLException e) {
            fail("SQLException occurred while verifying book update: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
            } catch (SQLException ignored) {
            }
        }
    }

    @Test
    @DisplayName("Test Book Deletion")
    public void testDeleteBook() {
        // add book
        Book book = new Book("Test Book", "Test Author", "12345", "Test Genre", "Test Publisher", true, null, null, null);
        dbHandler.execAction("INSERT INTO BOOK (id, title, author, publisher, genre, isAvail) VALUES ('" +
                book.getId() + "', '" + book.getTitle() + "', '" + book.getAuthor() + "', '" + book.getPublisher() + "', '" +
                book.getGenre() + "', true)");

        // delete
        boolean result = dbHandler.execAction("DELETE FROM BOOK WHERE id = '" + book.getId() + "'");
        assertTrue(result, "Book deletion should succeed");

        //test
        ResultSet rs = dbHandler.execQuery("SELECT * FROM BOOK WHERE id = '12345'");
        try {
            assertFalse(rs.next(), "Book should no longer exist in the database");
        } catch (SQLException e) {
            fail("SQLException occurred while verifying book deletion: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
            } catch (SQLException ignored) {
            }
        }
    }
}
