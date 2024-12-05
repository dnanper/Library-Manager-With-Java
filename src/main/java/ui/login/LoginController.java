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

    /**
     * Loads the main application UI for admins. It sets the root of the `MainController` to the loaded FXML layout for the main UI,
     * configures the stage with the appropriate scene, sets the theme, initializes the media player to play background music,
     * and shows the stage. Also, it sets the application icon for the stage.
     *
     * @throws IOException If there is an issue loading the main UI FXML file or creating the media object.
     */
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

    /**
     * Loads the main application UI for regular users. It sets the username in the `UserController`, sets the root of the `MainController` to the loaded FXML layout
     * for the user main UI, configures the stage with the appropriate scene, sets the theme, initializes the media player to play background music,
     * and shows the stage. Also, it sets the application icon for the stage.
     *
     * @throws IOException If there is an issue loading the user main UI FXML file or creating the media object.
     */
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
