package ui.login;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ui.settings.Preferences;
import org.apache.commons.codec.digest.DigestUtils;


public class LoginController implements Initializable{
    @FXML
    private JFXTextField username;
    @FXML
    private JFXPasswordField password;

    Preferences preference;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        preference = Preferences.getPreferences();
    }

    @FXML
    private void handleLoginButtonAction(ActionEvent event) throws IOException {
        String uname = (username.getText());
        String pword = DigestUtils.shaHex(password.getText());

        if (uname.equals(preference.getUsername()) && pword.equals(preference.getPassword())) {
            closeStage();
            loadMain();
        }
        else {
            username.getStyleClass().add("wrong-credentials");
            password.getStyleClass().add("wrong-credentials");
        }
    }
    @FXML
    private void handleCancelButtonAction(ActionEvent event) {
        System.exit(0);
    }

    private void closeStage() {
        ((Stage) username.getScene().getWindow()).close();
    }
    void loadMain() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/main.fxml"));
        Stage stage = new Stage(StageStyle.DECORATED);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

}
