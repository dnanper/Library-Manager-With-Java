package ui.settings;

import alert.AlertMaker;
import com.jfoenix.controls.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.InvalidParameterException;
import java.util.ResourceBundle;

import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import ui.addbook.AddBookController;
import ui.login.LoginController;
import ui.main.MainController;
import ui.theme.ThemeManager;

public class SettingsController implements Initializable {

    @FXML
    private JFXTextField nDaysWithoutFine;
    @FXML
    private JFXTextField finePerDay;
    @FXML
    private JFXTextField username;
    @FXML
    private JFXPasswordField password;

    @FXML
    private JFXTextField email;

    @FXML
    private JFXPasswordField emailpassword;

    @FXML
    private JFXSlider volumeSlider;
    @FXML
    private JFXComboBox<String> audioComboBox;

    private MediaPlayer mediaPlayer;

    @FXML
    private AnchorPane rootAnchorPane;

    @FXML
    private StackPane rootPane;

    @FXML
    private JFXButton faceButton;

    @FXML
    private JFXButton gitButton;

    @FXML
    private JFXButton guideButton;

    ObservableList<String> musicList = FXCollections.observableArrayList("MB1", "MB2");
    private HostServices hostServices;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initDefaultValues();
        mediaPlayer = LoginController.getMediaPlayer();
        audioComboBox.setItems(musicList);
        audioComboBox.setOnAction(this::handleAudioSelection);
        setupVolumeControl();
    }
    @FXML
    private void handleSaveButtonAction(ActionEvent event) {
        try {
            int ndays = Integer.parseInt(nDaysWithoutFine.getText());
            float fine = Float.parseFloat(finePerDay.getText());
            String uname = username.getText();
            String pass = password.getText();
            String em = email.getText();
            String epass = emailpassword.getText();

            Preferences preferences = Preferences.getPreferences();
            preferences.setnDaysWithoutFine(ndays);
            preferences.setFinePerDay(fine);
            preferences.setUsername(uname);
            preferences.setPassword(pass);
            preferences.setEmail(em);
            preferences.setEmailPassword(epass);
            Preferences.writePreferenceToFile(preferences);
            ((Stage) nDaysWithoutFine.getScene().getWindow()).close();

        } catch (NumberFormatException e) {
            AlertMaker.showErrorMessage("Invalid Input", "Please enter valid numbers for days and fine.");
        }
    }

    @FXML
    private void handleCancelButtonAction(ActionEvent event) {
        ((Stage) nDaysWithoutFine.getScene().getWindow()).close();
    }

    private void initDefaultValues() {
        Preferences preferences = Preferences.getPreferences();
        nDaysWithoutFine.setText(String.valueOf(preferences.getnDaysWithoutFine()));
        finePerDay.setText(String.valueOf(preferences.getFinePerDay()));
        username.setText(String.valueOf(preferences.getUsername()));
        password.setText(String.valueOf(preferences.getPassword()));
        email.setText(String.valueOf(preferences.getEmail()));
        emailpassword.setText(String.valueOf(preferences.getEmailPassword()));
    }

    @FXML
    void handleTheme(ActionEvent event) {
        ThemeManager.toggleTheme();
        ThemeManager.setTheme(rootPane.getScene());
        ThemeManager.setTheme(MainController.getInstance().getRoot().getScene());
    }

    private void setupVolumeControl() {
        if (mediaPlayer != null) {
            volumeSlider.setValue(mediaPlayer.getVolume() * 100);
            volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                mediaPlayer.setVolume(newValue.doubleValue() / 100.0);
            });
        } else {
            System.out.println("mediaPlayer chưa được khởi tạo");
        }
    }

    private void handleAudioSelection(ActionEvent event) {
        String selectedAudio = audioComboBox.getValue();
        String audioPath = "";

        if ("MB1".equals(selectedAudio)) {
            audioPath = "/MB1.mp3";
        } else if ("MB2".equals(selectedAudio)) {
            audioPath = "/MB2.mp3";
        }

        Media media = new Media(getClass().getResource(audioPath).toExternalForm());
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.play();
    }

    @FXML
    void clickFaceHandle(ActionEvent event) {
        try {
            URI facebookUri = new URI("https://www.facebook.com/UET.VNUH");
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(facebookUri);
            } else {
                if (hostServices!= null) {
                    hostServices.showDocument(facebookUri.toString());
                }
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void clickGitHandle(ActionEvent event) {
        try {
            URI githubUri = new URI("https://github.com/dnanper");
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(githubUri);
            } else {
                if (hostServices!= null) {
                    hostServices.showDocument(githubUri.toString());
                }
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }
}
