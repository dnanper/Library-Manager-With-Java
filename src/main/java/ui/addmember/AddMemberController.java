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
import javafx.stage.Stage;
import ui.listbook.ListBookController;
import ui.listmember.ListMemberController;

import java.net.URL;
import java.util.ResourceBundle;

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

    private Boolean isEditMod = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        handler = DataBaseHandler.getInstance();
    }

    @FXML
    void addMember(ActionEvent event) {
        String mName = name.getText();
        String mEmail = email.getText();
        String mPhone = phone.getText();
        String mID = id.getText();

        if (mName.isEmpty() || mID.isEmpty() || mPhone.isEmpty() || mEmail.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Please Finish all Fields!");
            alert.showAndWait();
            return;
        }

        if (isEditMod) {
            handleEditMod();
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
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("Add Member Successfully!");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Error Occurred!");
            alert.showAndWait();
        }
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
        // push data after edit to new book and use that book to update
        ListMemberController.Member member = new ListMemberController.Member(name.getText(), id.getText(), phone.getText(), email.getText());
        // update here
        if (DataBaseHandler.getInstance().updateMember(member)) {
            AlertMaker.showSimpleAlert("Success", "Member Updated");
        } else {
            AlertMaker.showErrorMessage("Failed", "Can Update Member");
        }
    }

}
