package ui.main;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javafx.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

// Main Window to Manage Add/View Object
public class MainController implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    @FXML
    public void loadAddBook(ActionEvent event) {
        loadWindow("/fxml/addbook.fxml", "Add New Book");
    }

    @FXML
    public void loadAddMember(ActionEvent event) {
        loadWindow("/fxml/addmember.fxml", "Add New Member");
    }

    @FXML
    public void loadBookTable(ActionEvent event) {
        loadWindow("/fxml/listbook.fxml", "View Books");
    }

    @FXML
    public void loadMemberTable(ActionEvent event) {
        loadWindow("/fxml/listmember.fxml", "View Memebers");
    }

    // function to load window of each method
    void loadWindow(String loc, String title) {
        try {
            // for each of FXML file, parent will be different
            Parent parent = FXMLLoader.load(getClass().getResource(loc));
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle(title);
            // Create scene from FXML file that stored in parent
            stage.setScene(new Scene(parent));
            stage.show();

        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
