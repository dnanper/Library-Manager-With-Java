package ui.settings;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import java.io.File;
import java.net.URL;
import java.security.InvalidParameterException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

public class SettingsController implements Initializable {

    @FXML
    private JFXTextField nDaysWithoutFine;
    @FXML
    private JFXTextField finePerDay;
    @FXML
    private JFXTextField username;
    @FXML
    private JFXPasswordField password;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }
    @FXML
    private void handleSaveButtonAction(ActionEvent event) {

    }
    @FXML
    private void handleCancelButtonAction(ActionEvent event) {

    }
}
