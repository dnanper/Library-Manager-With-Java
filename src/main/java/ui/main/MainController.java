package ui.main;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.effects.JFXDepthManager;
import database.DataBaseHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javafx.event.ActionEvent;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

// Main Window to Manage Add/View Object
public class MainController implements Initializable {

    @FXML
    private Text bookAuthor;

    @FXML
    private JFXTextField bookIDInput;

    @FXML
    private Text bookStatus;

    @FXML
    private Text bookTitle;

    @FXML
    private HBox bookinfo;

    @FXML
    private HBox memberinfo;

    @FXML
    private JFXTextField memberIDInput;

    @FXML
    private Text memberName;

    @FXML
    private Text memberPhone;

    DataBaseHandler dataBaseHandler;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        JFXDepthManager.setDepth(bookinfo, 1);
        JFXDepthManager.setDepth(memberinfo, 1);

        dataBaseHandler = DataBaseHandler.getInstance();
    }

    // find book information in library from book ID

    @FXML
    private void loadBookInfo(ActionEvent event) {

        clearBookCache();

        String id = bookIDInput.getText();
        // SQL query to get some information of book from BOOK TABLE in SQL
        String qu = "SELECT * FROM BOOK WHERE id = '" + id + "'";
        ResultSet rs = dataBaseHandler.execQuery(qu);

        // flag to check if query is oke
        Boolean a = false;

        try {
            while (rs.next()) {

                // get book information from database to feed into blank
                String bTitle = rs.getString("title");
                String bAuthor = rs.getString("author");
                Boolean bAvail = rs.getBoolean("isAvail");

                bookTitle.setText(bTitle);
                bookAuthor.setText(bAuthor);
                String status = (bAvail) ? "Available" : "Not Available";
                bookStatus.setText(status);
                a = true;
            }

            if (!a) {
                bookTitle.setText("No such book Available");
            }
        } catch (Exception e) {
        Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, e);
        }

    }

    void clearBookCache() {
        bookTitle.setText("");
        bookAuthor.setText("");
        bookStatus.setText("");
    }

    @FXML
    private void loadMemberInfo(ActionEvent event) {

        clearMemberCache();

        String id = memberIDInput.getText();
        // SQL query to get some information of book from BOOK TABLE in SQL
        String qu = "SELECT * FROM MEMBER WHERE id = '" + id + "'";
        ResultSet rs = dataBaseHandler.execQuery(qu);

        // flag to check if query is oke
        Boolean a = false;

        try {
            while (rs.next()) {
                // get member information from database to feed into blank
                String mName = rs.getString("name");
                String mPhone = rs.getString("phone");

                memberName.setText(mName);
                memberPhone.setText(mPhone);
                a = true;
            }

            if (!a) {
                memberName.setText("No such member Available");
            }
        } catch (Exception e) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    void clearMemberCache() {
        memberName.setText("");
        memberPhone.setText("");
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
