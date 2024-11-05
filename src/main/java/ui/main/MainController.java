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
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javafx.event.ActionEvent;
import org.apache.derby.impl.tools.sysinfo.Main;
import ui.addbook.AddBookController;
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
    private Tab bookRenewTab;

    @FXML
    private Tab bookIssueTab;

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

    @FXML
    private StackPane bookInfoContainer;

    @FXML
    private StackPane memberInfoContainer;

    PieChart bookChart;

    PieChart memberChart;

    Boolean isReadyForSubmission = false;

    DataBaseHandler dataBaseHandler;

    private Parent rootMain;

    private static MainController instance;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        JFXDepthManager.setDepth(bookinfo, 1);
        JFXDepthManager.setDepth(memberinfo, 1);

        dataBaseHandler = DataBaseHandler.getInstance();

        initDrawer();
        intiGraphs();
    }

    public static MainController getInstance() {
        if (instance == null) {
            instance = new MainController();
        }
        return instance;
    }

    public void setRoot(Parent par) {
        rootMain = par;
    }

    public Parent getRoot() {
        return rootMain;
    }

    private void intiGraphs() {
        bookChart = new PieChart(dataBaseHandler.getBookGraphicStatics());
        memberChart = new PieChart(dataBaseHandler.getMemberGraphicStatics());
        bookInfoContainer.getChildren().add(bookChart);
        memberInfoContainer.getChildren().add(memberChart);

        bookIssueTab.setOnSelectionChanged((Event event) -> {
            clearIssueEntries();
            if (bookIssueTab.isSelected()) {
                refreshGraph();

                // Xóa biểu đồ hiện tại và thêm lại biểu đồ mới để đảm bảo nó được khởi tạo lại hoàn toàn
                bookInfoContainer.getChildren().clear();
                memberInfoContainer.getChildren().clear();

                bookChart = new PieChart(dataBaseHandler.getBookGraphicStatics());
                memberChart = new PieChart(dataBaseHandler.getMemberGraphicStatics());

                bookInfoContainer.getChildren().add(bookChart);
                memberInfoContainer.getChildren().add(memberChart);

                //displayData(true);
            }
        });
    }

    private void displayData(Boolean status) {
        if (status) {
            bookChart.setOpacity(1);
            memberChart.setOpacity(1);
        } else {
            bookChart.setOpacity(0);
            memberChart.setOpacity(0);
        }
//        bookChart.setVisible(status);
//        memberChart.setVisible(status);

        // Optionally, remove from layout when not visible
//        if (status) {
//            if (!bookInfoContainer.getChildren().contains(bookChart)) {
//                bookInfoContainer.getChildren().add(bookChart);
//            }
//            if (!memberInfoContainer.getChildren().contains(memberChart)) {
//                memberInfoContainer.getChildren().add(memberChart);
//            }
//        } else {
//            bookInfoContainer.getChildren().remove(bookChart);
//            memberInfoContainer.getChildren().remove(memberChart);
//        }
    }

    private void refreshGraph() {
        memberChart.setData(dataBaseHandler.getMemberGraphicStatics());
        bookChart.setData(dataBaseHandler.getBookGraphicStatics());
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
        displayData(false);

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
                    fineInfoHolder.setText("0");
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

    // clear information after press enter ( renew/submission )
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

    private void clearIssueEntries() {
        bookIDInput.clear();
        memberIDInput.clear();
        bookTitle.setText("");
        bookAuthor.setText("");
        bookStatus.setText("");
        memberPhone.setText("");
        memberName.setText("");
        displayData(true);
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
        displayData(false);
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

        // If issue book
        JFXButton buttonT = new JFXButton("YES");
        buttonT.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event1) -> {
            // Add this book-member to ISSUE table to manager this relationship
            String str = "INSERT INTO ISSUE(memberID,bookID) VALUES ( "
                    + "'" + memberID + "',"
                    + "'" + bookID + "')";
            // Now the book is unavailable
            String str2 = "UPDATE BOOK SET isAvail = false WHERE id = '" + bookID + "'";

            if (dataBaseHandler.execAction(str) && dataBaseHandler.execAction(str2)) {
                JFXButton button = new JFXButton("OK");
                AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(button), "Book Issue Complete!", null);
                refreshGraph();
            } else {
                JFXButton button = new JFXButton("OK");
                AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(button), "Book Issue Failed!", null);
            }
            clearIssueEntries();
        });

        // If canceled issue book
        JFXButton buttonF = new JFXButton("NO");
        buttonF.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event1) -> {
            JFXButton button = new JFXButton("OK");
            AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(button), "Book Issue Cancelled!", null);
            clearIssueEntries();
        });

        // Display the 2 buttons
        AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(buttonT, buttonF), "Confirm", "Are you sure want to issue the book" + bookTitle.getText() + "\n to " + memberName.getText() + "?");
    }

    void clearMemberCache() {
        memberName.setText("");
        memberPhone.setText("");
    }

    // function to return book
    @FXML
    private void loadSubmissionOP(ActionEvent event) {
        if (!isReadyForSubmission) {
            JFXButton btn = new JFXButton("OK");
            AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(btn), "Please select a book to submit", "Can't submit a null book");
            return;
        }

        JFXButton buttonT = new JFXButton("YES");
        buttonT.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent ev) -> {
            String id = bookID.getText();
            // Two statement: delete book information from ISSUE and change value in BOOK
            // Both are modify data, so this is execAction
            String ac1 = "DELETE FROM ISSUE WHERE bookID = '" + id + "'";
            String ac2 = "UPDATE BOOK SET isAvail = TRUE WHERE ID = '" + id + "'";

            // Submission success
            if (dataBaseHandler.execAction(ac1) && dataBaseHandler.execAction(ac2)) {
                JFXButton btn = new JFXButton("DONE");
                btn.setOnAction((actionEvent) -> {
                    bookID.requestFocus();
                });
                AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(btn), "Book has been submitted", null);
                disableEnableControls(false);
                submissionDataContainer.setOpacity(0);
            } else {
                JFXButton btn = new JFXButton("OK");
                AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(btn), "Submission Has Been Failed", null);
            }
        });
        JFXButton buttonF = new JFXButton("NO");
        buttonF.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent ev) -> {
            JFXButton btn = new JFXButton("OK");
            AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(btn), "Submission cancelled", null);
        });

        AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(buttonT, buttonF), "Confirm Submission", "Are you sure want to return the book ?");
    }

    // function to renew book: increase the days till return
    @FXML
    void loadRenewOP(ActionEvent event) {

        if (!isReadyForSubmission) {
            JFXButton btn = new JFXButton("OK");
            AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(btn), "Please select a book to renew", null);
            return;
        }

        JFXButton buttonT = new JFXButton("YES");
        buttonT.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event1) -> {
            String ac = "UPDATE ISSUE SET issueTime = CURRENT_TIMESTAMP, renew_count = renew_count + 1 WHERE bookID = '" + bookID.getText() + "'";

            if (dataBaseHandler.execAction(ac)) {
                JFXButton btn = new JFXButton("OK");
                AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(btn), "Book Has Been Renewed", null);
                disableEnableControls(false);
                submissionDataContainer.setOpacity(0);
            } else {
                JFXButton btn = new JFXButton("OK");
                AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(btn), "Renew Has Been Failed", null);
            }
        });
        JFXButton buttonF = new JFXButton("NO");
        buttonF.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event1) -> {
            JFXButton btn = new JFXButton("OK");
            AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(btn), "Renew cancelled", null);
        });
        AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(buttonT, buttonF), "Confirm Renew", "Are you sure want to renew the book ?");
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
