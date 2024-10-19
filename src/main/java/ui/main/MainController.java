package ui.main;

import alert.AlertMaker;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.effects.JFXDepthManager;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import database.DataBaseHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javafx.event.ActionEvent;
import util.LibraryUtil;
//import org.apache.derby.impl.tools.sysinfo.Main;
//import org.apache.derby.iapi.sql.dictionary.OptionalTool;

//import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.input.MouseEvent;

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

    @FXML
    private JFXDrawer drawer;

    @FXML
    private JFXHamburger hamburger;

    @FXML
    private JFXButton renewButton;

    @FXML
    private JFXButton submissionButton;

    @FXML
    private HBox submissionDataContainer;

    @FXML
    private Text memberNameHolder;

    @FXML
    private Text memberEmailHolder;

    @FXML
    private Text memberContactHolder;

    @FXML
    private Text bookNameHolder;

    @FXML
    private Text bookAuthorHolder;

    @FXML
    private Text bookPublisherHolder;

    @FXML
    private Text issueDateHolder;

    @FXML
    private Text numberDaysHolder;

    @FXML
    private Text fineInfoHolder;

    @FXML
    private AnchorPane rootAnchorPane;

    Boolean isReadyForSubmission = false;

    DataBaseHandler dataBaseHandler;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        JFXDepthManager.setDepth(bookinfo, 1);
        JFXDepthManager.setDepth(memberinfo, 1);

        dataBaseHandler = DataBaseHandler.getInstance();

        initDrawer();
    }

    private void initDrawer() {
        // Display toolbar
        try {
            VBox toolBar = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/toolbar.fxml")));
            drawer.setSidePane(toolBar);
        } catch (IOException e) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, e);
        }
        HamburgerSlideCloseTransition task = new HamburgerSlideCloseTransition(hamburger);
        task.setRate(-1);
        hamburger.addEventHandler(MouseEvent.MOUSE_CLICKED, (Event event) -> {
            task.setRate(task.getRate()*-1);
            task.play();
            if (drawer.isHidden()) {
                drawer.open();
                drawer.setMinWidth(200);
            } else {
                drawer.close();
                drawer.setMinWidth(0);
            }
        });
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
        clearEntries();
        ObservableList<String> issueData = FXCollections.observableArrayList();
        isReadyForSubmission = false;

        try {
            String id = bookID.getText();
            String myQuery = "SELECT ISSUE.bookID, ISSUE.memberID, ISSUE.issueTime, ISSUE.renew_count,\n"
                    + "MEMBER.name, MEMBER.phone, MEMBER.email,\n"
                    + "BOOK.title, BOOK.author, BOOK.publisher\n"
                    + "FROM ISSUE\n"
                    + "LEFT JOIN MEMBER\n"
                    + "ON ISSUE.memberID=MEMBER.ID\n"
                    + "LEFT JOIN BOOK\n"
                    + "ON ISSUE.bookID=BOOK.ID\n"
                    + "WHERE ISSUE.bookID='" + id + "'";
            ResultSet rs = dataBaseHandler.execQuery(myQuery);
            if (rs.next()) {
                memberNameHolder.setText(rs.getString("name"));
                memberContactHolder.setText(rs.getString("phone"));
                memberEmailHolder.setText(rs.getString("email"));

                bookNameHolder.setText(rs.getString("title"));
                bookAuthorHolder.setText(rs.getString("author"));
                bookPublisherHolder.setText(rs.getString("publisher"));

                Timestamp mIssueTime = rs.getTimestamp("issueTime");
                Date dateOfIssue = new Date(mIssueTime.getTime());
                issueDateHolder.setText(LibraryUtil.formatDateTimeString(dateOfIssue));
                Long timeElapsed = System.currentTimeMillis() - mIssueTime.getTime();
                Long days = TimeUnit.DAYS.convert(timeElapsed, TimeUnit.MILLISECONDS) + 1;
                String daysElapsed = String.format("Used %d days", days);
                numberDaysHolder.setText(daysElapsed);
                Float fine = LibraryUtil.getFineAmount(days.intValue());
                if (fine > 0) {
                    fineInfoHolder.setText(String.format("Fine : %.2f", LibraryUtil.getFineAmount(days.intValue())));
                } else {
                    fineInfoHolder.setText("");
                }

                isReadyForSubmission = true;
                disableEnableControls(true);
                submissionDataContainer.setOpacity(1);
            } else {
                JFXButton button = new JFXButton("Okay.I'll Check");
                AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(button), "No such Book Exists in Issue Database", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearEntries() {
        memberNameHolder.setText("");
        memberEmailHolder.setText("");
        memberContactHolder.setText("");

        bookNameHolder.setText("");
        bookAuthorHolder.setText("");
        bookPublisherHolder.setText("");

        issueDateHolder.setText("");
        numberDaysHolder.setText("");
        fineInfoHolder.setText("");

        disableEnableControls(false);
        submissionDataContainer.setOpacity(0);
    }

    private void disableEnableControls(Boolean enableFlag) {
        if (enableFlag) {
            renewButton.setDisable(false);
            submissionButton.setDisable(false);
        } else {
            renewButton.setDisable(true);
            submissionButton.setDisable(true);
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
        LibraryUtil.loadWindow(getClass().getResource("/fxml/addbook.fxml"), "Add New Book", null);
    }

    @FXML
    private void handleMenuAddMember(ActionEvent event) {
        LibraryUtil.loadWindow(getClass().getResource("/fxml/addmember.fxml"), "Add New Member", null);
    }

    @FXML
    private void handleMenuLoadBook(ActionEvent event) {
        LibraryUtil.loadWindow(getClass().getResource("/fxml/listbook.fxml"), "View Book", null);
    }

    @FXML
    private void handleMenuLoadMember(ActionEvent event) {
        LibraryUtil.loadWindow(getClass().getResource("/fxml/listmember.fxml"), "View Member", null);
    }

    @FXML
    private void handleMenuFullScreen(ActionEvent event) {
        Stage stage = ((Stage) rootPane.getScene().getWindow());
        stage.setFullScreen(!stage.isFullScreen());
    }
}
