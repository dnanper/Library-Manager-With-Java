package ui.settings;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ui.login.LoginController;
import ui.theme.ThemeManager;

public class SettingsLoader extends Application {
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/mainsettings.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        ThemeManager.setTheme(scene);
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}

