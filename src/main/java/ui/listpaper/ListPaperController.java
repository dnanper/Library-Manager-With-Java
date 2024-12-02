package ui.listpaper;

import alert.AlertMaker;
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
import model.Paper;
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

import util.LibraryUtil;

public class ListPaperController implements Initializable {

    ObservableList<Paper> list = FXCollections.observableArrayList();

    @FXML
    private TableColumn<Paper, String> authorCol;

    @FXML
    private TableColumn<Paper, Boolean> availabilityCol;

    @FXML
    private TableColumn<Paper, String> idCol;

    @FXML
    private TableColumn<Paper, String> conferenceCol;

    @FXML
    private TableColumn<Paper, String> yearCol;

    @FXML
    private TableColumn<Paper, String> genreCol;

    @FXML
    private AnchorPane rootAnchorPane;

    @FXML
    private StackPane rootPane;

    @FXML
    private JFXComboBox<String> searchTypeCBox;

    @FXML
    private JFXTextField searchText;

    @FXML
    private TableView<Paper> tableView;

    @FXML
    private TableColumn<Paper, String> titleCol;

    ObservableList<String> typeList = FXCollections.observableArrayList("ID", "Title", "Author", "Conference", "Year");

    DataBaseHandler handler = DataBaseHandler.getInstance();
    Connection connection = handler.getConnection();
    GenericSearch<Paper> paperSearch = new GenericSearch<>(connection);

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        searchTypeCBox.setItems(typeList);
        initCol();
        loadData();
        setupTableClickHandler(tableView);
    }

    private void setupTableClickHandler(TableView<Paper> tableView) {
        tableView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                Paper selectedPaper = tableView.getSelectionModel().getSelectedItem();
                if (selectedPaper != null) {
                    selectedPaper.displayInfo();

                }
            }
        });
    }

    private void initCol() {
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        conferenceCol.setCellValueFactory(new PropertyValueFactory<>("conference"));
        yearCol.setCellValueFactory(new PropertyValueFactory<>("year"));
        genreCol.setCellValueFactory(new PropertyValueFactory<>("genre"));
        availabilityCol.setCellValueFactory(new PropertyValueFactory<>("availability"));
    }

    private void filterPaperList(String searchContent, String type) {
        if (searchContent == null || searchContent.isEmpty() || type == null) {
            tableView.setItems(list);
            return;
        }

        String columnName = null;

        switch (type) {
            case "ID":
                columnName = Paper.getColumnName("id");
                break;
            case "Title":
                columnName = Paper.getColumnName("title");
                break;
            case "Author":
                columnName = Paper.getColumnName("author");
                break;
            case "Conference":
                columnName = Paper.getColumnName("conference");
                break;
            case "Year":
                columnName = Paper.getColumnName("year");
                break;
            default:
                Logger.getLogger(ListPaperController.class.getName()).log(Level.WARNING,
                        "Invalid search type: {0}", type);
                tableView.setItems(list);
                return;
        }

        try {
            List<Paper> filteredList = paperSearch.search(
                    "PAPER",
                    "LOWER(" + columnName + ") LIKE ?",
                    new Object[]{"%" + searchContent.toLowerCase() + "%"},
                    Paper.class
            );

            tableView.setItems(FXCollections.observableArrayList(filteredList));
        } catch (Exception e) {
            Logger.getLogger(ListPaperController.class.getName()).log(Level.SEVERE,
                    "Error", e);
        }
    }

    private void loadData() {
        list.clear();
        DataBaseHandler handler = DataBaseHandler.getInstance();
        String qu = "SELECT * FROM PAPER";
        ResultSet res = handler.execQuery(qu);
        try {
            while (res.next()) {
                String tit = res.getString("title");
                String aut = res.getString("author");
                String idx = res.getString("id");
                String conf = res.getString("conference");
                String yr = res.getString("release_year");
                String gen = res.getString("genre");
                Boolean ava = res.getBoolean("isAvail");

                list.add(new Paper(tit, aut, idx, gen, conf, yr, ava));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ListPaperController.class.getName()).log(Level.SEVERE, null, ex);
        }
        tableView.setItems(list);

        searchText.textProperty().addListener((observable, oldValue, newValue) -> {
            String type = searchTypeCBox.getValue();
            filterPaperList(newValue, type);
        });
    }

    @FXML
    void handlePaperDeleteOption(ActionEvent event) {
        Paper selectDel = tableView.getSelectionModel().getSelectedItem();
        if (selectDel == null) {
            AlertMaker.showErrorMessage("No paper selected", "Please select a paper for deletion!");
            return;
        }

        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Deleting Paper");
        alert.setContentText("Are you sure you want to delete the paper " + selectDel.getTitle() + " from the library?");
        Optional<ButtonType> ans = alert.showAndWait();

        if (ans.get() == ButtonType.OK) {
            Boolean flag = DataBaseHandler.getInstance().deletePaper(selectDel);
            if (flag) {
                list.remove(selectDel);
                AlertMaker.showSimpleAlert("Paper Deleted", selectDel.getTitle() + " was deleted successfully!");
            } else {
                AlertMaker.showSimpleAlert("Failed", selectDel.getTitle() + " could not be deleted");
            }
        } else {
            AlertMaker.showSimpleAlert("Deletion Cancelled", "Deletion process cancelled");
        }
    }

    @FXML
    void handlePaperEditOption(ActionEvent event) {
        Paper selectEdit = tableView.getSelectionModel().getSelectedItem();
        if (selectEdit == null) {
            AlertMaker.showErrorMessage("No paper selected", "Please select a paper for editing!");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/addpaper.fxml"));
            Parent parent = loader.load();

            AddBookController controller = loader.getController();
            controller.inflateUI(selectEdit);

            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("Edit Paper");
            stage.setScene(new Scene(parent));
            stage.show();
            LibraryUtil.setStageIcon(stage);

            stage.setOnCloseRequest((e) -> {
                handlePaperRefreshOption(new ActionEvent());
            });
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    void handlePaperRefreshOption(ActionEvent event) {
        loadData();
    }
}
