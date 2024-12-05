import database.DataBaseHandler;
import model.Book;
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
    }

    // clean after each test
    @AfterEach
    void cleanup() {
        dbHandler.execAction("DELETE FROM BOOK");
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
