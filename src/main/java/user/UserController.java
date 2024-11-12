package user;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;

public class UserController implements Initializable {

    @FXML
    private TableColumn<?, ?> authorCol;

    @FXML
    private TableColumn<?, ?> authorCol1;

    @FXML
    private TableColumn<?, ?> availabilityCol;

    @FXML
    private TableColumn<?, ?> availabilityCol1;

    @FXML
    private Pane borrowPane;

    @FXML
    private TableColumn<?, ?> dayCol;

    @FXML
    private TableColumn<?, ?> genreCol;

    @FXML
    private TableColumn<?, ?> genreCol1;

    @FXML
    private TableColumn<?, ?> idCol;

    @FXML
    private TableColumn<?, ?> idCol1;

    @FXML
    private Pane libraryPane;

    @FXML
    private TableColumn<?, ?> publisherCol;

    @FXML
    private TableColumn<?, ?> publisherCol1;

    @FXML
    private Pane settingPane;

    @FXML
    private JFXButton mailButton;

    @FXML
    private Pane mailPane;

    @FXML
    private TableView<?> tableView;

    @FXML
    private TableView<?> tableView1;

    @FXML
    private TableColumn<?, ?> titleCol;

    @FXML
    private TableColumn<?, ?> titleCol1;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    void confirmEmailHandle(ActionEvent event) {

    }
}
