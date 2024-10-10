package ui.main;

import database.DataBaseHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import util.LibraryUtil;

public class MainLoader extends Application{
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/main.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

        LibraryUtil.setStageIcon(stage);

        // A little multithreading to reduce latency
        new Thread(() -> {
            DataBaseHandler.getInstance();
        }).start();
    }

    public static void main(String[] args) {
        launch();
    }

}