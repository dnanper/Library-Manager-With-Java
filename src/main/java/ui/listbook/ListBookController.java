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

    /**
     * An instance of the `GenericSearch` class specialized for searching `Book` objects in the database.
     */
    GenericSearch<Book> bookSearch = new GenericSearch<>(connection);

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        searchTypeCBox.setItems(typeList);
        initCol();
        loadData();
        setupTableClickHandler(tableView);

    }

    /**
     * Sets up the click handler for the table view. When a row is clicked once with the left mouse button, it retrieves the selected book and passes its ID
     * to the `BookController` for further handling (e.g., loading detailed information about the book).
     *
     * @param tableView The `TableView` object representing the table displaying the book list.
     */
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

    private void initCol() {
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        publisherCol.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        genreCol.setCellValueFactory(new PropertyValueFactory<>("genre"));
        availabilityCol.setCellValueFactory(new PropertyValueFactory<>("availability"));
    }

    /**
     * Filters the book list based on the provided search content and search type. If the search content or type is invalid or empty, it displays the full list.
     * Otherwise, it performs a database search using the `GenericSearch` instance to find books that match the search criteria and updates the table view with the filtered list.
     *
     * @param searchContent The text entered by the user for searching (e.g., a book title, author name, etc.).
     * @param type The type of search selected by the user (e.g., "ID", "Title", "Author", "Genre").
     */
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

    /**
     * Loads the book data from the database and populates the observable list (`list`) with `Book` objects.
     * It then sets the items of the table view to display the loaded list. Also, it adds a listener to the search text field
     * so that the book list is filtered automatically as the user types in the search text.
     */
    private void loadData() {
        list.clear();
        DataBaseHandler handler = DataBaseHandler.getInstance();
        String qu = "SELECT * FROM BOOK";
        ResultSet res = handler.execQuery(qu);
        try {
            while (res.next()) {
                String tit = res.getString("title");
                String aut = res.getString("author");
                String idx = res.getString("id");
                String pub = res.getString("publisher");
                String gen = res.getString("genre");
                Boolean ava = res.getBoolean("isAvail");
                list.add(new Book(tit, aut, idx, gen, pub, ava,null,null,null));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ListBookController.class.getName()).log(Level.SEVERE, null, ex);
        }
        tableView.setItems(list);
        searchText.textProperty().addListener((observable, oldValue, newValue) -> {
            String type = searchTypeCBox.getValue();
            filterBookList(newValue, type);
        });
    }

    @FXML
    void handleBookDeleteOption(ActionEvent event) {
        Book selectDel = tableView.getSelectionModel().getSelectedItem();
        if (selectDel == null) {
            AlertMaker.showErrorMessage("No book selected", "Please select a book for deletion!");
            return;
        }
        if (DataBaseHandler.getInstance().isIssued(selectDel)) {
            AlertMaker.showErrorMessage("This book is not available", "Please select another book for deletion!");
            return;
        }

        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Deleting Book");
        alert.setContentText("Are you sure want to delete the book " + selectDel.getTitle() + " from library?");
        Optional<ButtonType> ans = alert.showAndWait();
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
        Book selectEdit = tableView.getSelectionModel().getSelectedItem();
        if (selectEdit == null) {
            AlertMaker.showErrorMessage("No book selected", "Please select a book for edition");
            return;
        }

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

