package ui.addbook;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import database.DataBaseHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.util.ResourceBundle;

public class AddBookController{
    @FXML
    private JFXTextField author;

    @FXML
    private JFXButton cancelButton;

    @FXML
    private JFXTextField id;

    @FXML
    private JFXTextField publisher;

    @FXML
    private JFXButton saveButton;

    @FXML
    private JFXTextField title;

    DataBaseHandler dataBaseHandler;

    //@Override
    public void initialize(URL url, ResourceBundle rb) {
        dataBaseHandler = new DataBaseHandler();
    }

    @FXML
    void addBook(ActionEvent event) {

    }

    @FXML
    void cancel(ActionEvent event) {

    }

}
