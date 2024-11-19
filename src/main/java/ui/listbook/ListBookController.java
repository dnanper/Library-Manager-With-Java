package ui.listbook;

import alert.AlertMaker;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import database.DataBaseHandler;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ui.addbook.AddBookController;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Alert.AlertType;
import ui.main.MainController;
import ui.theme.ThemeManager;
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
    private TableColumn<Book, String> genreCol; // Thêm cột genre

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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        searchTypeCBox.setItems(typeList);
        initCol();
        loadData();
    }

    // function to connect the columns in table with the tags of the book
    private void initCol() {
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        publisherCol.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        genreCol.setCellValueFactory(new PropertyValueFactory<>("genre")); // Kết nối cột genre
        availabilityCol.setCellValueFactory(new PropertyValueFactory<>("availability"));
    }

    private void filterBookList(String searchContent, String type) {
        if (searchContent == null || searchContent.isEmpty()) {
            tableView.setItems(list);
            return;
        }
        ObservableList<ListBookController.Book> filterList = FXCollections.observableArrayList();
        for (ListBookController.Book book : list) {
            if (type.equals("ID")) {
                if (book.getId().toLowerCase().contains(searchContent.toLowerCase())) {
                    filterList.add(book);
                }
            } else if (type.equals("Title")) {
                if (book.getTitle().toLowerCase().contains(searchContent.toLowerCase())) {
                    filterList.add(book);
                }
            } else if (type.equals("Author")) {
                if (book.getAuthor().toLowerCase().contains(searchContent.toLowerCase())) {
                    filterList.add(book);
                }
            } else {
                if (book.getGenre() != null && book.getGenre().toLowerCase().contains(searchContent.toLowerCase())) {
                    filterList.add(book);
                }
            }

        }
        tableView.setItems(filterList);
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
                String gen = res.getString("genre"); // Lấy genre từ kết quả truy vấn
                Boolean ava = res.getBoolean("isAvail");

                // add data of book to list
                list.add(new Book(tit, idx, aut, pub, gen, ava,null,null,null));
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

    public static class Book {
        private final SimpleStringProperty title;
        private final SimpleStringProperty id;
        private final SimpleStringProperty author;
        private final SimpleStringProperty publisher;
        private final SimpleStringProperty genre;
        private final SimpleBooleanProperty availability;
        private final SimpleStringProperty url;
        private final SimpleStringProperty urlCoverImage;
        private final SimpleStringProperty description;


        public Book(String title, String id, String author, String publisher, String genre, Boolean avail, String url, String urlCoverImage, String description) {
            this.title = new SimpleStringProperty(title);
            this.id = new SimpleStringProperty(id);
            this.author = new SimpleStringProperty(author);
            this.publisher = new SimpleStringProperty(publisher);
            this.genre = new SimpleStringProperty(genre);
            this.availability = new SimpleBooleanProperty(avail);
            this.url = new SimpleStringProperty(url);
            this.urlCoverImage = new SimpleStringProperty(urlCoverImage);
            this.description = new SimpleStringProperty(description);

        }

        public String getTitle() {
            return title.get();
        }

        public String getId() {
            return id.get();
        }

        public String getAuthor() {
            return author.get();
        }

        public String getPublisher() {
            return publisher.get();
        }

        public String getGenre() {
            return genre.get();
        }

        public Boolean getAvailability() {
            return availability.get();
        }

        public String getUrl() {
            return url.get();
        }

        public String getUrlCoverImage() {
            return urlCoverImage.get();
        }

        public String getDescription() {
            return description.get();
        }
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/addbook.fxml"));
            Parent parent = loader.load();

            AddBookController controller = (AddBookController) loader.getController();
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

