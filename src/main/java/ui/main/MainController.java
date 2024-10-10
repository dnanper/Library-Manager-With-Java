package ui.main;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.effects.JFXDepthManager;
import database.DataBaseHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javafx.event.ActionEvent;
import util.LibraryUtil;
//import org.apache.derby.impl.tools.sysinfo.Main;
//import org.apache.derby.iapi.sql.dictionary.OptionalTool;

import java.io.IOException;
import java.net.URL;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;
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
    private JFXTextField bookID;

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

    @FXML
    private ListView<String> issueDataList;

    @FXML
    private StackPane rootPane;

    Boolean isReadyForSubmission = false;

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

    // function to display all the information from ISSUE, BOOK and MEMBER TABLE about one book
    @FXML
    void loadBookInfor2(ActionEvent event) {

        ObservableList<String> issueData = FXCollections.observableArrayList();
        isReadyForSubmission = false;

        String id = bookID.getText();
        String qu = "SELECT * FROM ISSUE WHERE bookID = '" + id + "'";
        ResultSet rs = dataBaseHandler.execQuery(qu);
        // fetch data to the data list to display
        try {
            while (rs.next()) {
                String iBookID = id;
                String iMemberID = rs.getString("memberID");
                Timestamp iTime = rs.getTimestamp("issueTime");
                int iRenewCount = rs.getInt("renew_count");

                issueData.add("Issue Date and Time: " + iTime.toGMTString());
                issueData.add("Renew Count: " + iRenewCount);

                issueData.add("Book Information: ");
                qu = "SELECT * FROM BOOK WHERE ID = '" + iBookID + "'";
                ResultSet r1 = dataBaseHandler.execQuery(qu);

                // take data from BOOK TABLE
                while (r1.next()) {
                    issueData.add("Book Name: " + r1.getString("title"));
                    issueData.add("Book ID: " + r1.getString("id"));
                    issueData.add("Book Author: " + r1.getString("author"));
                    issueData.add("Book Publisher: " + r1.getString("publisher"));
                }

                issueData.add("Issuer Information: ");
                qu = "SELECT * FROM MEMBER WHERE ID = '" + iMemberID + "'";
                r1 = dataBaseHandler.execQuery(qu);

                // take data from MEMBER TABLE
                while (r1.next()) {
                    issueData.add("Member Name: " + r1.getString("name"));
                    issueData.add("Member ID: " + r1.getString("id"));
                    issueData.add("Member Phone: " + r1.getString("phone"));
                    issueData.add("Member Email: " + r1.getString("email"));
                }
                isReadyForSubmission = true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }

        issueDataList.getItems().setAll(issueData);
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

    // add new relation between book-member
    @FXML
    private void loadIssueOperation(ActionEvent event) {
        String memberID = memberIDInput.getText();
        String bookID = bookIDInput.getText();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Issue Operation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure want to issue the book" + bookTitle.getText() + "\n to" + memberName.getText() + "?");

        Optional<ButtonType> response = alert.showAndWait();
        // If we (manager) sure to issue the book to a member then
        if (response.get() == ButtonType.OK) {
            // Add this book-member to ISSUE table to manager this relationship
            String str = "INSERT INTO ISSUE(memberID,bookID) VALUES ( "
                    + "'" + memberID + "',"
                    + "'" + bookID + "')";
            // Now the book is unavailable
            String str2 = "UPDATE BOOK SET isAvail = false WHERE id = '" + bookID + "'";
            System.out.println(str + " and " + str2);

            if (dataBaseHandler.execAction(str) && dataBaseHandler.execAction(str2)) {
                Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                alert1.setTitle("Success!");
                alert1.setHeaderText(null);
                alert1.setContentText("Book Issue Complete!");
                alert1.showAndWait();
            } else {
                Alert alert2 = new Alert(Alert.AlertType.ERROR  );
                alert2.setTitle("Failed!");
                alert2.setHeaderText(null);
                alert2.setContentText("Book Issue Failed!");
                alert2.showAndWait();
            }
        } else {
            Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
            alert1.setTitle("Cancelled!");
            alert1.setHeaderText(null);
            alert1.setContentText("Book Issue Cancelled!");
            alert1.showAndWait();
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
            LibraryUtil.setStageIcon(stage);

        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // function to return book
    @FXML
    private void loadSubmissionOP(ActionEvent event) {

        if (!isReadyForSubmission) {
            Alert alert1 = new Alert(Alert.AlertType.ERROR);
            alert1.setTitle("Failed!");
            alert1.setHeaderText(null);
            alert1.setContentText("Please select a valid Book to summit!");
            alert1.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Issue Operation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure want to return the book?");

        Optional<ButtonType> response = alert.showAndWait();
        // If we (member) sure to return the book
        if (response.get() == ButtonType.OK) {
            String id = bookID.getText();
            // Two statement: delete book information from ISSUE and change value in BOOK
            // Both are modify data, so this is execAction
            String ac1 = "DELETE FROM ISSUE WHERE bookID = '" + id + "'";
            String ac2 = "UPDATE BOOK SET isAvail = TRUE WHERE ID = '" + id + "'";

            // Submission success
            if (dataBaseHandler.execAction(ac1) && dataBaseHandler.execAction(ac2)) {
                Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                alert1.setTitle("Success!");
                alert1.setHeaderText(null);
                alert1.setContentText("Book has been submitted!");
                alert1.showAndWait();
            } else {
                Alert alert1 = new Alert(Alert.AlertType.ERROR);
                alert1.setTitle("Failed!");
                alert1.setHeaderText(null);
                alert1.setContentText("Submission has been Failed!");
                alert1.showAndWait();
            }
        } else {
            Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
            alert1.setTitle("Cancelled!");
            alert1.setHeaderText(null);
            alert1.setContentText("Book Return Cancelled!");
            alert1.showAndWait();
        }
    }

    // function to renew book: increase the days till return
    @FXML
    void loadRenewOP(ActionEvent event) {

        if (!isReadyForSubmission) {
            Alert alert1 = new Alert(Alert.AlertType.ERROR);
            alert1.setTitle("Failed!");
            alert1.setHeaderText(null);
            alert1.setContentText("Please select a valid Book to renew!");
            alert1.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Renew Operation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure want to renew the book?");

        Optional<ButtonType> response = alert.showAndWait();
        // If we (member) sure to return the book
        if (response.get() == ButtonType.OK) {
            // also increase the number of renew
            String ac = "UPDATE ISSUE SET issueTime = CURRENT_TIMESTAMP, renew_count = renew_count + 1 WHERE bookID = '" + bookID.getText() + "'";
            System.out.println(ac);
            if (dataBaseHandler.execAction(ac)) {
                Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                alert1.setTitle("Success!");
                alert1.setHeaderText(null);
                alert1.setContentText("Book has been renewed!");
                alert1.showAndWait();
            } else {
                Alert alert1 = new Alert(Alert.AlertType.ERROR);
                alert1.setTitle("Failed!");
                alert1.setHeaderText(null);
                alert1.setContentText("Renew has been Failed!");
                alert1.showAndWait();
            }
        } else {
            Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
            alert1.setTitle("Failed!");
            alert1.setHeaderText(null);
            alert1.setContentText("Renew has been Failed!");
            alert1.showAndWait();
        }
    }

    @FXML
    private void handleMenuClose(ActionEvent event) {
        ((Stage)rootPane.getScene().getWindow()).close();
    }

    @FXML
    private void handleMenuAddBook(ActionEvent event) {
        loadWindow("/fxml/addbook.fxml", "Add New Book");
    }

    @FXML
    private void handleMenuAddMember(ActionEvent event) {
        loadWindow("/fxml/addmember.fxml", "Add New Member");
    }

    @FXML
    private void handleMenuLoadBook(ActionEvent event) {
        loadWindow("/fxml/listbook.fxml", "View Books");
    }

    @FXML
    private void handleMenuLoadMember(ActionEvent event) {
        loadWindow("/fxml/listmember.fxml", "View Memebers");
    }

    @FXML
    private void handleMenuFullScreen(ActionEvent event) {
        Stage stage = ((Stage) rootPane.getScene().getWindow());
        stage.setFullScreen(!stage.isFullScreen());
    }
}
