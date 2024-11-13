package user;

import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public class pscfController {

    @FXML
    private JFXTextField emailAccInput;

    @FXML
    private JFXTextField emailPasInput;

    private UserController userController;

    public void setUserController(UserController userController) {
        this.userController = userController;
    }

    @FXML
    void handleCancle(ActionEvent event) {
        Stage stage = (Stage) emailAccInput.getScene().getWindow();
        stage.close();
    }

    @FXML
    void handleConfirm(ActionEvent event) {
        String email = emailAccInput.getText();
        String pass = emailPasInput.getText();
        if (userController != null) {
            userController.receiveEmail(email, pass);
        }
        Stage stage = (Stage) emailAccInput.getScene().getWindow();
        stage.close();
    }

}

