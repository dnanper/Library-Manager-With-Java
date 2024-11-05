package ui.addbook;

import alert.AlertMaker;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import database.DataBaseHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import ui.listbook.ListBookController;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AddBookController implements Initializable {
    @FXML
    private JFXTextField author;
    @FXML
    private JFXButton cancelButton;
    @FXML
    private JFXTextField id;
    @FXML
    private JFXTextField publisher;
    @FXML
    private JFXTextField genre; // New genre field
    @FXML
    private AnchorPane rootPane;
    @FXML
    private StackPane stackRootPane;
    @FXML
    private JFXButton saveButton;
    @FXML
    private JFXTextField title;

    private Boolean isEditMod = Boolean.FALSE;
    DataBaseHandler dataBaseHandler;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dataBaseHandler = DataBaseHandler.getInstance();
        checkData();
    }

    public static boolean isBookExists(String id) {
        try {
            String checkstmt = "SELECT COUNT(*) FROM BOOK WHERE id=?";
            PreparedStatement stmt = DataBaseHandler.getInstance().getConnection().prepareStatement(checkstmt);
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                return (count > 0);
            }
        } catch (SQLException ex) {
            Logger.getLogger(AddBookController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    // Run whenever user presses save
    @FXML
    public void addBook(ActionEvent event) {
        String bookID = id.getText();
        String bookPublisher = publisher.getText();
        String bookAuthor = author.getText();
        String bookTitle = title.getText();
        String bookGenre = genre.getText(); // Get genre from the new field

        if (bookID.isEmpty() || bookAuthor.isEmpty() || bookTitle.isEmpty() || bookGenre.isEmpty()) {
            AlertMaker.showMaterialDialog(stackRootPane, rootPane, new ArrayList<>(), "Insufficient Data", "Please enter data in all fields.");
            return;
        }

        if (isEditMod) {
            handleEditMod();
            return;
        }

        if (isBookExists(bookID)) {
            AlertMaker.showMaterialDialog(stackRootPane, rootPane, new ArrayList<>(), "Duplicate book id", "Book with same Book ID exists.\nPlease use new ID");
            return;
        }

        // Create SQL statement to save new book with genre
        String qu = "INSERT INTO BOOK (id, title, author, publisher, genre, isAvail) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement stmt = dataBaseHandler.getConnection().prepareStatement(qu);
            stmt.setString(1, bookID);
            stmt.setString(2, bookTitle);
            stmt.setString(3, bookAuthor);
            stmt.setString(4, bookPublisher);
            stmt.setString(5, bookGenre); // Set genre
            stmt.setBoolean(6, true);
            if (stmt.executeUpdate() > 0) {
                AlertMaker.showMaterialDialog(stackRootPane, rootPane, new ArrayList<>(), "New book added", bookTitle + " has been added");
                clearEntries();
            } else {
                AlertMaker.showMaterialDialog(stackRootPane, rootPane, new ArrayList<>(), "Failed to add new book", "Check all the entries and try again");
            }
        } catch (SQLException ex) {
            Logger.getLogger(AddBookController.class.getName()).log(Level.SEVERE, null, ex);
            AlertMaker.showMaterialDialog(stackRootPane, rootPane, new ArrayList<>(), "Database Error", "Could not connect to database.");
        }
    }

    private void clearEntries() {
        title.clear();
        id.clear();
        author.clear();
        publisher.clear();
        genre.clear(); // Clear genre field
    }

    @FXML
    public void cancel(ActionEvent event) {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }

    private void checkData() {
        String qu = "SELECT title FROM BOOK";
        ResultSet res = dataBaseHandler.execQuery(qu);
        try {
            while (res.next()) {
                String tit = res.getString("title");
            }
        } catch (SQLException ex) {
            Logger.getLogger(AddBookController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void inflateUI(ListBookController.Book book) {
        title.setText(book.getTitle());
        id.setText(book.getId());
        author.setText(book.getAuthor());
        publisher.setText(book.getPublisher());
        genre.setText(book.getGenre()); // Set genre in edit mode
        id.setEditable(false);
        isEditMod = Boolean.TRUE;
    }

    private void handleEditMod() {
        ListBookController.Book book = new ListBookController.Book(title.getText(), id.getText(), author.getText(), publisher.getText(), genre.getText(),true);
        // Update book details
        if (dataBaseHandler.updateBook(book)) {
            AlertMaker.showSimpleAlert("Success", "Book Updated");
        } else {
            AlertMaker.showErrorMessage("Failed", "Can Update Book");
        }
    }
}
