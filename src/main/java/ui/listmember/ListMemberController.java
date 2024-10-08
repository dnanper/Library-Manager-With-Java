package ui.listmember;

import database.DataBaseHandler;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ListMemberController implements Initializable {

    ObservableList<ListMemberController.Member> list = FXCollections.observableArrayList();

    @FXML
    private TableColumn<Member, String> emailCol;

    @FXML
    private TableColumn<Member, String> idCol;

    @FXML
    private TableColumn<Member, String> nameCol;

    @FXML
    private TableColumn<Member, String> phoneCol;

    @FXML
    private TableView<Member> tableView;

    // SAME AS LISTBOOK

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initCol();
        loadData();
    }

    private void initCol() {
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
    }

    // function to extract data from database to put to table
    private void loadData() {
        DataBaseHandler handler = DataBaseHandler.getInstance();
        String qu = "SELECT * FROM MEMBER";
        ResultSet res = handler.execQuery(qu);
        try {
            // get all attributes of each book from database
            while (res.next()) {
                String nam = res.getString("name");
                String pho = res.getString("phone");
                String idx = res.getString("id");
                String ema = res.getString("email");

                // add data of book to list
                list.add(new Member(nam, idx, pho, ema));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ListMemberController.class.getName()).log(Level.SEVERE, null, ex);
        }

        // push all elements in list to table
        tableView.getItems().setAll(list);
    }

    public static class Member {
        private final SimpleStringProperty name;
        private final SimpleStringProperty id;
        private final SimpleStringProperty phone;
        private final SimpleStringProperty email;

        public Member(String name, String id, String phone, String email) {
            this.name = new SimpleStringProperty(name);
            this.id = new SimpleStringProperty(id);
            this.phone = new SimpleStringProperty(phone);
            this.email = new SimpleStringProperty(email);
        }

        public String getName() {
            return name.get();
        }

        public String getId() {
            return id.get();
        }

        public String getPhone() {
            return phone.get();
        }

        public String getEmail() {
            return email.get();
        }

    }

}
