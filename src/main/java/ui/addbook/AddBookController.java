package ui.addbook;

import alert.AlertMaker;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import database.DataBaseHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import ui.listbook.ListBookController;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AddBookController implements Initializable{
    @FXML
    private JFXTextField author;

    @FXML
    private JFXButton cancelButton;

    @FXML
    private JFXTextField id;

    @FXML
    private JFXTextField publisher;

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
                System.out.println(count);
                return (count > 0);
            }
        } catch (SQLException ex) {
            Logger.getLogger(AddBookController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    // run whenever user press save
    @FXML
    public void addBook(ActionEvent event) {
        String bookID = id.getText();
        String bookPublisher = publisher.getText();
        String bookAuthor = author.getText();
        String bookTitle = title.getText();

        if (bookID.isEmpty() || bookAuthor.isEmpty() || bookTitle.isEmpty()) {
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

        // create SQL statement to save new book.
        //        stmt.execute("CREATE TABLE " + TABLE_NAME + "("
        //        + "         id varchar(200) primary key,\n"
        //        + "         title varchar(200),\n"
        //        + "         author varchar(200),\n"
        //        + "         publisher varchar(100),\n"
        //        + "         isAvail boolean default true"
        //        + " )");
        String qu = "INSERT INTO BOOK VALUES ("+
                "'" + bookID + "'," +
                "'" + bookTitle + "'," +
                "'" + bookAuthor + "'," +
                "'" + bookPublisher + "'," +
                "" +  true + "" +
                " )";

        // Check if statement work well with database
        if (dataBaseHandler.execAction(qu)) {
            AlertMaker.showMaterialDialog(stackRootPane, rootPane, new ArrayList<>(), "New book added", bookTitle + " has been added");
            clearEntries();
        } else { // Error
            AlertMaker.showMaterialDialog(stackRootPane, rootPane, new ArrayList<>(), "Failed to add new book", "Check all the entries and try again");
        }
    }

    private void clearEntries() {
        title.clear();
        id.clear();
        author.clear();
        publisher.clear();
    }

    // function to clean screen
    @FXML
    public void cancel(ActionEvent event) {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }

    // function to display all the book has been added
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

    // Function to feed the information of required book from ListBookController
    public void inflateUI(ListBookController.Book book) {
        title.setText(book.getTitle());
        id.setText(book.getId());
        author.setText(book.getAuthor());
        publisher.setText(book.getPublisher());
        id.setEditable(false);
        isEditMod = Boolean.TRUE;
    }

    private void handleEditMod() {
        // push data after edit to new book and use that book to update
        ListBookController.Book book = new ListBookController.Book(title.getText(), id.getText(), author.getText(), publisher.getText(), true);
        // update here
        if (dataBaseHandler.updateBook(book)) {
            AlertMaker.showSimpleAlert("Success", "Book Updated");
        } else {
            AlertMaker.showErrorMessage("Failed", "Can Update Book");
        }
    }

    @FXML
    void searchBookByAPI(ActionEvent event) {

    }

}
