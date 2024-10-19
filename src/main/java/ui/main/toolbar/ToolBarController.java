package ui.main.toolbar;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import util.LibraryUtil;

import java.net.URL;
import java.util.ResourceBundle;

public class ToolBarController implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    private void loadAddBook(ActionEvent event) {
        LibraryUtil.loadWindow(getClass().getResource("/fxml/addbook.fxml"), "Add New Book", null);
    }

    @FXML
    private void loadAddMember(ActionEvent event) {
        LibraryUtil.loadWindow(getClass().getResource("/fxml/addmember.fxml"), "Add New Member", null);
    }

    @FXML
    private void loadBookTable(ActionEvent event) {
        LibraryUtil.loadWindow(getClass().getResource("/fxml/listbook.fxml"), "View Book", null);
    }

    @FXML
    private void loadMemberTable(ActionEvent event) {
        LibraryUtil.loadWindow(getClass().getResource("/fxml/listmember.fxml"), "View Member", null);
    }

    @FXML
    private void loadSettings(ActionEvent event) {
        LibraryUtil.loadWindow(getClass().getResource("/fxml/settings.fxml"), "Settings", null);
    }

}
