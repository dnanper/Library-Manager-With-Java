package ui.listthesis;

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
import model.Thesis;
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

public class ListThesisController implements Initializable {

    ObservableList<Thesis> list = FXCollections.observableArrayList();

    @FXML
    private TableColumn<Thesis, String> authorCol;

    @FXML
    private TableColumn<Thesis, Boolean> availabilityCol;

    @FXML
    private TableColumn<Thesis, String> idCol;

    @FXML
    private TableColumn<Thesis, String> universityCol;

    @FXML
    private TableColumn<Thesis, String> departmentCol;

    @FXML
    private TableColumn<Thesis, String> genreCol;

    @FXML
    private AnchorPane rootAnchorPane;

    @FXML
    private StackPane rootPane;

    @FXML
    private JFXComboBox<String> searchTypeCBox;

    @FXML
    private JFXTextField searchText;

    @FXML
    private TableView<Thesis> tableView;

    @FXML
    private TableColumn<Thesis, String> titleCol;

    ObservableList<String> typeList = FXCollections.observableArrayList("ID", "Title", "Author", "University", "Department", "Genre");

    DataBaseHandler handler = DataBaseHandler.getInstance();
    Connection connection = handler.getConnection();
    GenericSearch<Thesis> thesisSearch = new GenericSearch<>(connection);

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        searchTypeCBox.setItems(typeList);
        initCol();
        loadData();
        setupTableClickHandler(tableView);
    }

    private void setupTableClickHandler(TableView<Thesis> tableView) {
        tableView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                Thesis selectedThesis = tableView.getSelectionModel().getSelectedItem();
                if (selectedThesis != null) {
                    // Handle thesis selection (e.g., load detailed view or edit form)
                    // loadThesis.handleThesisSelection(selectedThesis.getId());
                }
            }
        });
    }

    private void initCol() {
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        universityCol.setCellValueFactory(new PropertyValueFactory<>("university"));
        departmentCol.setCellValueFactory(new PropertyValueFactory<>("department"));
        genreCol.setCellValueFactory(new PropertyValueFactory<>("genre"));
        availabilityCol.setCellValueFactory(new PropertyValueFactory<>("availability"));
    }

    private void filterThesisList(String searchContent, String type) {
        if (searchContent == null || searchContent.isEmpty() || type == null) {
            tableView.setItems(list);
            return;
        }

        String columnName = null;

        switch (type) {
            case "ID":
                columnName = Thesis.getColumnName("id");
                break;
            case "Title":
                columnName = Thesis.getColumnName("title");
                break;
            case "Author":
                columnName = Thesis.getColumnName("author");
                break;
            case "University":
                columnName = Thesis.getColumnName("university");
                break;
            case "Department":
                columnName = Thesis.getColumnName("department");
                break;
            case "Genre":
                columnName = Thesis.getColumnName("genre");
                break;
            default:
                Logger.getLogger(ListThesisController.class.getName()).log(Level.WARNING,
                        "Invalid search type: {0}", type);
                tableView.setItems(list);
                return;
        }

        try {
            List<Thesis> filteredList = thesisSearch.search(
                    "THESIS",
                    "LOWER(" + columnName + ") LIKE ?",
                    new Object[]{"%" + searchContent.toLowerCase() + "%"},
                    Thesis.class
            );

            tableView.setItems(FXCollections.observableArrayList(filteredList));
        } catch (Exception e) {
            Logger.getLogger(ListThesisController.class.getName()).log(Level.SEVERE, "Error", e);
        }
    }

    private void loadData() {
        list.clear();
        String qu = "SELECT * FROM THESIS";
        ResultSet res = handler.execQuery(qu);
        try {
            while (res.next()) {
                String tit = res.getString("title");
                String aut = res.getString("author");
                String idx = res.getString("id");
                String uni = res.getString("university");
                String dep = res.getString("department");
                String gen = res.getString("genre");
                Boolean ava = res.getBoolean("isAvail");

                list.add(new Thesis(tit, aut, idx, gen, uni, dep, ava));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ListThesisController.class.getName()).log(Level.SEVERE, null, ex);
        }
        tableView.setItems(list);
        searchText.textProperty().addListener((observable, oldValue, newValue) -> {
            String type = searchTypeCBox.getValue();
            filterThesisList(newValue, type);
        });
    }

    @FXML
    void handleThesisDeleteOption(ActionEvent event) {
        Thesis selectDel = tableView.getSelectionModel().getSelectedItem();
        if (selectDel == null) {
            AlertMaker.showErrorMessage("No thesis selected", "Please select a thesis for deletion!");
            return;
        }

        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Deleting Thesis");
        alert.setContentText("Are you sure want to delete the thesis " + selectDel.getTitle() + " from library?");
        Optional<ButtonType> ans = alert.showAndWait();
        if (ans.get() == ButtonType.OK) {
            Boolean flag = DataBaseHandler.getInstance().deleteThesis(selectDel);
            if (flag) {
                list.remove(selectDel);
                AlertMaker.showSimpleAlert("Thesis Deleted", selectDel.getTitle() + " was deleted successfully!");
            } else {
                AlertMaker.showSimpleAlert("Failed", selectDel.getTitle() + " could not be deleted");
            }
        } else {
            AlertMaker.showSimpleAlert("Deletion Cancelled", "Deletion process cancelled");
        }
    }

    @FXML
    void handleThesisEditOption(ActionEvent event) {
        Thesis selectEdit = tableView.getSelectionModel().getSelectedItem();
        if (selectEdit == null) {
            AlertMaker.showErrorMessage("No thesis selected", "Please select a thesis for editing");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/adddocument.fxml"));
            Parent parent = loader.load();

            AddBookController controller = (AddBookController) loader.getController();
            controller.inflateUI(selectEdit);

            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("Edit Thesis");
            stage.setScene(new Scene(parent));
            stage.show();
            LibraryUtil.setStageIcon(stage);

            stage.setOnCloseRequest((e) -> {
                handleThesisRefreshOption(new ActionEvent());
            });
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    void handleThesisRefreshOption(ActionEvent event) {
        loadData();
    }
}
