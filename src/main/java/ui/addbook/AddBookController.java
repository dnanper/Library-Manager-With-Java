package ui.addbook;

import Factory.BookFactory;
import Factory.DocumentFactory;
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
import model.Book;
import model.Document;
import model.Paper;
import model.Thesis;
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
    private JFXTextField university;
    @FXML
    private JFXTextField department;
    @FXML
    private JFXTextField conference;
    @FXML
    private JFXTextField year;
    @FXML
    private AnchorPane rootPane;
    @FXML
    private StackPane stackRootPane;
    @FXML
    private JFXButton saveButton;
    @FXML
    private JFXTextField title;

    private static AddBookController instance;

    private Boolean isEditMod = Boolean.FALSE;
    DataBaseHandler dataBaseHandler;

    private DocumentFactory documentFactory;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dataBaseHandler = DataBaseHandler.getInstance();
        checkData("Book");
//        checkData("Thesis");
//        checkData("Paper");
        documentFactory = new BookFactory();
    }

    public static boolean isDocumentExists(String id, String type) {
        try {
            String checkstmt = "SELECT COUNT(*) FROM " + type.toUpperCase() + " WHERE id=?";
            if (checkstmt!= null) {
                PreparedStatement stmt = DataBaseHandler.getInstance().getConnection().prepareStatement(checkstmt);
                stmt.setString(1, id);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return (count > 0);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(AddBookController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    // Run whenever user presses save
    @FXML
    public void addDocument(ActionEvent event) {
        String documentID = id.getText();
        String documentAuthor = author.getText();
        String documentTitle = title.getText();
        String documentGerne = genre.getText();
        Boolean documentIsAvail = true;

        if (documentID.isEmpty() || documentAuthor.isEmpty() || documentTitle.isEmpty() || documentGerne.isEmpty()) {
            AlertMaker.showMaterialDialog(stackRootPane, rootPane, new ArrayList<>(), "Insufficient Data", "Please enter data in all fields.");
            return;
        }

        if (isEditMod) {
            handleEditMod();
            return;
        }

        String documentType = "Book";
        if (publisher.isVisible() && genre.isVisible()) {
            documentType = "Book";
        } else if (university.isVisible() && department.isVisible()) {
            documentType = "Thesis";
        } else if (conference.isVisible() && year.isVisible()) {
            documentType = "Paper";
        }

        String[] additionalInfo = new String[4];
        if ("Book".equals(documentType)) {
            additionalInfo[0] = publisher.getText();
            additionalInfo[1] = ""; // url
        } else if ("Thesis".equals(documentType)) {
            additionalInfo[0] = university.getText();
            additionalInfo[1] = department.getText();
        } else if ("Paper".equals(documentType)) {
            additionalInfo[0] = conference.getText();
            additionalInfo[1] = year.getText();
        }

        Document document = documentFactory.createDocument(documentType, documentTitle, documentAuthor, documentID, documentGerne , documentIsAvail, additionalInfo);

        if (isDocumentExists(documentID, documentType)) {
            AlertMaker.showMaterialDialog(stackRootPane, rootPane, new ArrayList<>(), "Duplicate document id", "document with same Document ID exists.\nPlease use new ID");
            return;
        }

        if (document!= null) {
            String insertQuery = null;
            if (document instanceof Book) {
                insertQuery = "INSERT INTO BOOK (id, title, author, publisher, genre, isAvail) VALUES (?, ?, ?, ?, ?, ?)";
            } else if (document instanceof Thesis) {
                insertQuery = "INSERT INTO THESIS (id, title, author, university, genre, department, isAvail) VALUES (?, ?, ?, ?, ?, ?, ?)";
            } else if (document instanceof Paper) {
                insertQuery = "INSERT INTO PAPER (id, title, author, conference, genre, year, isAvail) VALUES (?, ?, ?, ?, ?, ?, ?)";
            }
            try {
                PreparedStatement stmt = dataBaseHandler.getConnection().prepareStatement(insertQuery);
                if (document instanceof Book) {
                    Book book = (Book) document;
                    stmt.setString(1, documentID);
                    stmt.setString(2, documentTitle);
                    stmt.setString(3, documentAuthor);
                    stmt.setString(4, book.getPublisher());
                    stmt.setString(5, documentGerne);
                    stmt.setBoolean(6, documentIsAvail);
                } else if (document instanceof Thesis) {
                    Thesis thesis = (Thesis) document;
                    stmt.setString(1, documentID);
                    stmt.setString(2, documentTitle);
                    stmt.setString(3, documentAuthor);
                    stmt.setString(4, thesis.getUniversity());
                    stmt.setString(5, documentGerne);
                    stmt.setString(6, thesis.getDepartment());
                    stmt.setBoolean(7, documentIsAvail);
                } else if (document instanceof Paper) {
                    Paper paper = (Paper) document;
                    stmt.setString(1, documentID);
                    stmt.setString(2, documentTitle);
                    stmt.setString(3, documentAuthor);
                    stmt.setString(4, paper.getConference());
                    stmt.setString(5, documentGerne);
                    stmt.setString(6, paper.getYear());
                    stmt.setBoolean(7, documentIsAvail);
                }
                if (stmt.executeUpdate() > 0) {
                    AlertMaker.showMaterialDialog(stackRootPane, rootPane, new ArrayList<>(), "New document added", documentTitle + " has been added");
                    clearEntries();
                } else {
                    AlertMaker.showMaterialDialog(stackRootPane, rootPane, new ArrayList<>(), "Failed to add new document", "Check all the entries and try again");
                }
            } catch (SQLException ex) {
                Logger.getLogger(AddBookController.class.getName()).log(Level.SEVERE, null, ex);
                AlertMaker.showMaterialDialog(stackRootPane, rootPane, new ArrayList<>(), "Database Error", "Could not connect to database.");
            }
        } else {
            AlertMaker.showErrorMessage("Invalid Document Type", "Please select a valid document type.");
        }
    }

    private void clearEntries() {
        title.clear();
        id.clear();
        author.clear();
        publisher.clear();
        genre.clear();
    }

    @FXML
    public void cancel(ActionEvent event) {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }

    private void checkData(String type) {
        String qu = "SELECT title FROM " + type.toUpperCase();
        ResultSet res = dataBaseHandler.execQuery(qu);
        try {
            while (res.next()) {
                String tit = res.getString("title");
            }
        } catch (SQLException ex) {
            Logger.getLogger(AddBookController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void inflateUI(Document document) {
        if (document instanceof Book) {
            Book book = (Book) document;
            title.setText(book.getTitle());
            id.setText(book.getId());
            author.setText(book.getAuthor());
            publisher.setText(book.getPublisher());
            genre.setText(book.getGenre());
            university.setVisible(false);
            department.setVisible(false);
            conference.setVisible(false);
            year.setVisible(false);
        } else if (document instanceof Thesis) {
            Thesis thesis = (Thesis) document;
            title.setText(thesis.getTitle());
            id.setText(thesis.getId());
            author.setText(thesis.getAuthor());
            university.setText(thesis.getUniversity());
            department.setText(thesis.getDepartment());
            genre.setText(thesis.getGenre());
            publisher.setVisible(false);
            conference.setVisible(false);
            year.setVisible(false);
        } else if (document instanceof Paper) {
            Paper paper = (Paper) document;
            title.setText(paper.getTitle());
            id.setText(paper.getId());
            author.setText(paper.getAuthor());
            conference.setText(paper.getConference());
            year.setText(paper.getYear());
            genre.setText(paper.getGenre());
            publisher.setVisible(false);
            university.setVisible(false);
            department.setVisible(false);
        }
        id.setEditable(false);
        isEditMod = Boolean.TRUE;
    }

    private void handleEditMod() {
        Document document = null;
        if (publisher.isVisible()) {
            document = new Book(title.getText(), author.getText(), id.getText(), genre.getText(), publisher.getText(),true, "", "", "");
        } else if (university.isVisible()) {
            document = new Thesis(title.getText(), author.getText(), id.getText(),genre.getText(), university.getText(), department.getText(), true);
        } else if (conference.isVisible()) {
            document = new Paper(title.getText(), author.getText(), id.getText(), genre.getText(), conference.getText(), year.getText(), true);
        }

        if (document!= null) {
            if (dataBaseHandler.updateDocument(document)) {
                AlertMaker.showSimpleAlert("Success", "Document Updated");
            } else {
                AlertMaker.showErrorMessage("Failed", "Can't Update Document");
            }
        }
    }
}
