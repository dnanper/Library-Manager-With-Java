package ui.main;

import api.Api;
import database.DataBaseHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ui.theme.ThemeManager;
import util.LibraryUtil;

public class MainLoader extends Application{
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
        Scene scene = new Scene(root);
        ThemeManager.setTheme(scene);
        stage.setScene(scene);
        stage.show();

        LibraryUtil.setStageIcon(stage);

        new Thread(() -> {
            DataBaseHandler.getInstance();
        }).start();

        new Thread(() -> {
            Api.getInstance();
        }).start();
    }

    public static void main(String[] args) {
        launch();
    }

}