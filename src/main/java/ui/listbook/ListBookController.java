package ui.listbook;

import alert.AlertMaker;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import database.DataBaseHandler;
import database.GenericSearch;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Book;
import ui.addbook.AddBookController;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Alert.AlertType;
import ui.main.MainController;
import ui.theme.ThemeManager;
import user.BookController;
import util.LibraryUtil;

public class ListBookController implements Initializable {

    ObservableList<Book> list = FXCollections.observableArrayList();

    @FXML
    private TableColumn<Book, String> authorCol;

    @FXML
    private TableColumn<Book, Boolean> availabilityCol;

    @FXML
    private TableColumn<Book, String> idCol;

    @FXML
    private TableColumn<Book, String> publisherCol;

    @FXML
    private TableColumn<Book, String> genreCol;

    @FXML
    private AnchorPane rootAnchorPane;

    @FXML
    private StackPane rootPane;

    @FXML
    private JFXComboBox<String> searchTypeCBox;

    @FXML
    private JFXTextField searchText;

    // tables contain books
    @FXML
    private TableView<Book> tableView;

    @FXML
    private TableColumn<Book, String> titleCol;

    ObservableList<String> typeList = FXCollections.observableArrayList( "ID", "Title", "Author", "Genre");

    DataBaseHandler handler = DataBaseHandler.getInstance();
    Connection connection = handler.getConnection();
        GenericSearch<Book> bookSearch = new GenericSearch<>(connection);

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        searchTypeCBox.setItems(typeList);
        initCol();
        loadData();
        setupTableClickHandler(tableView);

    }

    private void setupTableClickHandler(TableView<Book> tableView) {
        BookController loadBook = new BookController();
        tableView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                Book selectedBook = tableView.getSelectionModel().getSelectedItem();
                if (selectedBook != null) {
                    loadBook.handleBookSelection(selectedBook.getId());
                }
            }
        });
    }

    // function to connect the columns in table with the tags of the book
    private void initCol() {
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        publisherCol.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        genreCol.setCellValueFactory(new PropertyValueFactory<>("genre"));
        availabilityCol.setCellValueFactory(new PropertyValueFactory<>("availability"));
    }

    private void filterBookList(String searchContent, String type) {
        if (searchContent == null || searchContent.isEmpty() || type == null) {
            tableView.setItems(list);
            return;
        }

        String columnName = null;

        switch (type) {
            case "ID":
                columnName = Book.getColumnName("id");
                break;
            case "Title":
                columnName = Book.getColumnName("title");
                break;
            case "Author":
                columnName = Book.getColumnName("author");
                break;
            case "Genre":
                columnName = Book.getColumnName("genre");
                break;
            default:
                Logger.getLogger(ListBookController.class.getName()).log(Level.WARNING,
                        "Invalid search type: {0}", type);
                tableView.setItems(list);
                return;
        }

        try {

            List<Book> filteredList = bookSearch.search(
                    "BOOK",
                    "LOWER(" + columnName + ") LIKE ?",
                    new Object[]{"%" + searchContent.toLowerCase() + "%"},
                    Book.class
            );

            tableView.setItems(FXCollections.observableArrayList(filteredList));
        } catch (Exception e) {
            Logger.getLogger(ListBookController.class.getName()).log(Level.SEVERE,
                    "Error", e);
        }
    }




    // function to extract data from database to put to table
    private void loadData() {
        list.clear();
        DataBaseHandler handler = DataBaseHandler.getInstance();
        String qu = "SELECT * FROM BOOK";
        ResultSet res = handler.execQuery(qu);
        try {
            // get all attributes of each book from database
            while (res.next()) {
                String tit = res.getString("title");
                String aut = res.getString("author");
                String idx = res.getString("id");
                String pub = res.getString("publisher");
                String gen = res.getString("genre");
                Boolean ava = res.getBoolean("isAvail");

                // add data of book to list
                list.add(new Book(tit, aut, idx, gen, pub, ava,null,null,null));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ListBookController.class.getName()).log(Level.SEVERE, null, ex);
        }
        // push all elements in list to table
        tableView.setItems(list);
        // Search Book
        searchText.textProperty().addListener((observable, oldValue, newValue) -> {
            String type = searchTypeCBox.getValue();
            filterBookList(newValue, type);
        });
    }

    @FXML
    void handleBookDeleteOption(ActionEvent event) {
        // Fetch the chosen row ( book )
        Book selectDel = tableView.getSelectionModel().getSelectedItem();
        if (selectDel == null) {
            AlertMaker.showErrorMessage("No book selected", "Please select a book for deletion!");
            return;
        }

        // If the book was issued before and hasn't been returned
        if (DataBaseHandler.getInstance().isIssued(selectDel)) {
            AlertMaker.showErrorMessage("This book is not available", "Please select another book for deletion!");
            return;
        }

        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Deleting Book");
        alert.setContentText("Are you sure want to delete the book " + selectDel.getTitle() + " from library?");
        Optional<ButtonType> ans = alert.showAndWait();
        // If user agree to delete
        if (ans.get() == ButtonType.OK) {
            Boolean flag = DataBaseHandler.getInstance().deleteBook(selectDel);
            if (flag) {
                list.remove(selectDel);
                AlertMaker.showSimpleAlert("Book Deleted ", selectDel.getTitle() + " was delete successfully!");
            } else {
                AlertMaker.showSimpleAlert("Failed ", selectDel.getTitle() + " could not be delete");
            }
        } else {
            AlertMaker.showSimpleAlert("Deletion Cancelled", "Deletion process cancelled");
        }
    }

    @FXML
    void handleBookEditOption(ActionEvent event) {
        // Fetch the chosen row ( book )
        Book selectEdit = tableView.getSelectionModel().getSelectedItem();
        if (selectEdit == null) {
            AlertMaker.showErrorMessage("No book selected", "Please select a book for edition");
            return;
        }

        // Display Edit Book Window
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/adddocument.fxml"));
            Parent parent = loader.load();

            AddBookController controller = loader.getController();
            controller.inflateUI(selectEdit);

            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("Edit Book");
            stage.setScene(new Scene(parent));
            stage.show();
            LibraryUtil.setStageIcon(stage);

            // Refresh after Edit
            stage.setOnCloseRequest((e) -> {
                handleBookRefreshOption(new ActionEvent());
            });
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    void handleBookRefreshOption(ActionEvent event) {
        loadData();
    }

}

