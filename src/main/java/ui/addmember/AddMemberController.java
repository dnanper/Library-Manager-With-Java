package ui.addmember;

import alert.AlertMaker;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import database.DataBaseHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import ui.addbook.AddBookController;
import ui.listbook.ListBookController;
import ui.listmember.ListMemberController;
import ui.settings.UserPreferences;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AddMemberController implements Initializable {

    DataBaseHandler handler;

    @FXML
    private JFXButton cancelButton;

    @FXML
    private JFXTextField email;

    @FXML
    private JFXTextField id;

    @FXML
    private JFXTextField name;

    @FXML
    private JFXTextField phone;

    @FXML
    private JFXButton saveButton;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private StackPane stackRootPane;

    private Boolean isEditMod = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        handler = DataBaseHandler.getInstance();
    }

    public static boolean isMemberExists(String id) {
        try {
            String checkstmt = "SELECT COUNT(*) FROM MEMBER WHERE id=?";
            PreparedStatement stmt = DataBaseHandler.getInstance().getConnection().prepareStatement(checkstmt);
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println(count);
                return (count > 0);
            }
        } catch (SQLException ex) {
            Logger.getLogger(AddMemberController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @FXML
    void addMember(ActionEvent event) {
        String mName = name.getText();
        String mEmail = email.getText();
        String mPhone = phone.getText();
        String mID = id.getText();

        if (mName.isEmpty() || mID.isEmpty() || mPhone.isEmpty() || mEmail.isEmpty()) {
            AlertMaker.showMaterialDialog(stackRootPane, rootPane, new ArrayList<>(), "Insufficient Data", "Please enter data in all fields.");
            return;
        }

        if (isEditMod) {
            handleEditMod();
            return;
        }

        if (isMemberExists(mID)) {
            AlertMaker.showMaterialDialog(stackRootPane, rootPane, new ArrayList<>(), "Duplicate member id", "Member with same id exists.\nPlease use new ID");
            return;
        }

//        stmt.execute("CREATE TABLE " + TABLE_NAME + "("
//                + "         id varchar(200) primary key,\n"
//                + "         name varchar(200),\n"
//                + "         phone varchar(20),\n"
//                + "         email varchar(100),\n"
//                + " )");

        String st = "INSERT INTO MEMBER VALUES (" +
                "'" + mID + "'," +
                "'" + mName + "'," +
                "'" + mPhone + "'," +
                "'" + mEmail + "'" +
                " )";
        System.out.println(st);
        if (handler.execAction(st)) {
            UserPreferences.User newUser = new UserPreferences().new User(mID, mID, "0");
            UserPreferences.appendUserToFile(newUser);

            AlertMaker.showMaterialDialog(stackRootPane, rootPane, new ArrayList<>(), "New member added", mName + " has been added");
            clearEntries();
        } else {
            AlertMaker.showMaterialDialog(stackRootPane, rootPane, new ArrayList<>(), "Failed to add new member", "Check you entries and try again.");
        }
    }

    private void clearEntries() {
        name.clear();
        id.clear();
        phone.clear();
        email.clear();
    }

    @FXML
    void cancel(ActionEvent event) {
        Stage stage = (Stage)name.getScene().getWindow();
        stage.close();
    }

    public void inflateUI(ListMemberController.Member member) {
        name.setText(member.getName());
        id.setText(member.getId());
        phone.setText(member.getPhone());
        email.setText(member.getEmail());
        id.setEditable(false);
        isEditMod = Boolean.TRUE;
    }

    private void handleEditMod() {
        ListMemberController.Member member = new ListMemberController.Member(name.getText(), id.getText(), phone.getText(), email.getText());
        if (DataBaseHandler.getInstance().updateMember(member)) {
            AlertMaker.showSimpleAlert("Success", "Member Updated");
        } else {
            AlertMaker.showErrorMessage("Failed", "Can Update Member");
        }
    }

}
