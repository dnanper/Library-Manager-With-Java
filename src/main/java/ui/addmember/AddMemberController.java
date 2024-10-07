package ui.addmember;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import database.DataBaseHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;

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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        handler = new DataBaseHandler();
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

    }

}
