package ui.login;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.Media;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ui.main.MainController;
import ui.settings.Preferences;
import org.apache.commons.codec.digest.DigestUtils;
import ui.settings.UserPreferences;
import ui.theme.ThemeManager;
import user.UserController;
import util.LibraryUtil;


public class LoginController implements Initializable{
    @FXML
    private JFXTextField username;
    @FXML
    private JFXPasswordField password;

    public static MediaPlayer mediaPlayer;

    Preferences preference;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        preference = Preferences.getPreferences();
    }

    @FXML
    private void handleLoginAdminButtonAction(ActionEvent event) throws IOException {
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
    void handleLoginUserButtonAction(ActionEvent event) throws IOException {
        String uname = (username.getText());
        String pword = DigestUtils.shaHex(password.getText());

        if (UserPreferences.checkUser(uname, pword)) {
            closeStage();
            loadUserMain();
        } else {
            System.out.println("false");
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

    public static MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    void loadMain() throws IOException {
        MainController main = MainController.getInstance();
        main.setRoot(FXMLLoader.load(getClass().getResource("/fxml/main.fxml")));
        Stage stage = new Stage(StageStyle.DECORATED);
        Scene scene = new Scene(main.getRoot());

        ThemeManager.setTheme(scene);

        String bmpath = getClass().getResource("/MB1.mp3").getPath();
        Media media = new Media(getClass().getResource("/MB1.mp3").toExternalForm());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.play();

        stage.setScene(scene);
        stage.show();
        LibraryUtil.setStageIcon(stage);
    }

    void loadUserMain() throws IOException {
        String uname = username.getText();
        UserController.setUsername(uname);

        MainController main = MainController.getInstance();
        main.setRoot(FXMLLoader.load(getClass().getResource("/fxml/user.fxml")));
        Stage stage = new Stage(StageStyle.DECORATED);
        Scene scene = new Scene(main.getRoot());

        ThemeManager.setTheme(scene);

        String bmpath = getClass().getResource("/MB1.mp3").getPath();
        Media media = new Media(getClass().getResource("/MB1.mp3").toExternalForm());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.play();

        stage.setScene(scene);
        stage.show();
        LibraryUtil.setStageIcon(stage);
    }

}
