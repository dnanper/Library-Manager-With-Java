package ui.listmember;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ui.theme.ThemeManager;

public class ListMemberLoader extends Application{

    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/listmember.fxml"));
        Scene scene = new Scene(root);
//        ThemeManager.setTheme(scene);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}