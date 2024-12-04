package ui.listmember;

import alert.AlertMaker;
import com.google.gson.Gson;
import database.DataBaseHandler;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ui.addbook.AddBookController;
import ui.addmember.AddMemberController;
import ui.listbook.ListBookController;
import ui.main.MainController;
import ui.settings.UserPreferences;
import util.LibraryUtil;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
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

    private void loadData() {
        list.clear();

        DataBaseHandler handler = DataBaseHandler.getInstance();
        String qu = "SELECT * FROM MEMBER";
        ResultSet res = handler.execQuery(qu);
        try {
            while (res.next()) {
                String nam = res.getString("name");
                String pho = res.getString("phone");
                String idx = res.getString("id");
                String ema = res.getString("email");
                list.add(new Member(nam, idx, pho, ema));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ListMemberController.class.getName()).log(Level.SEVERE, null, ex);
        }

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

    @FXML
    void handleMemberDelete(ActionEvent event) {
        Member selectDel = tableView.getSelectionModel().getSelectedItem();
        if (selectDel == null) {
            AlertMaker.showErrorMessage("No member selected", "Please select a member for deletion!");
            return;
        }

        if (DataBaseHandler.getInstance().isMemberHasAnyBooks(selectDel))
        {
            AlertMaker.showErrorMessage("This Member has some books", "Please select another member for deletion!");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Deleting Member");
        alert.setContentText("Are you sure want to delete this member " + selectDel.getName() + "?");
        Optional<ButtonType> ans = alert.showAndWait();

        if (ans.get() == ButtonType.OK) {
            Boolean flag = DataBaseHandler.getInstance().deleteMember(selectDel);
            if (flag) {
                list.remove(selectDel);
                boolean userDel = deleteUserByMemberID(selectDel.getId());
                if (userDel) {
                    AlertMaker.showSimpleAlert("Member Deleted", selectDel.getName() + " was deleted successfully, including their user account.");
                } else {
                    AlertMaker.showErrorMessage("Member Deleted", selectDel.getName() + " was deleted, but user account could not be removed.");
                }
            } else {
                AlertMaker.showSimpleAlert("Failed ", selectDel.getName() + " could not be delete");

            }
        } else {
            AlertMaker.showSimpleAlert("Deletion Cancelled", "Deletion process cancelled");
        }
    }

    private boolean deleteUserByMemberID(String memberID) {
        List<UserPreferences.User> users = UserPreferences.loadUsers();
        boolean userFound = false;

        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equals(memberID)) {
                users.remove(i);
                userFound = true;
                break;
            }
        }

        if (userFound) {
            try (Writer writer = new FileWriter(UserPreferences.CONFIG_FILE)) {
                Gson gson = new Gson();
                for (UserPreferences.User user : users) {
                    writer.write(gson.toJson(user) + System.lineSeparator());
                }
                return true;
            } catch (IOException e) {
                System.out.println("Error writing updated user data: " + e.getMessage());
                return false;
            }
        }

        return false;
    }


    @FXML
    void handleMemberEdit(ActionEvent event) {

        Member selectEdit = tableView.getSelectionModel().getSelectedItem();
        if (selectEdit == null) {
            AlertMaker.showErrorMessage("No member selected", "Please select a member for edition");
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/addmember.fxml"));
            Parent parent = loader.load();

            AddMemberController controller = (AddMemberController) loader.getController();
            controller.inflateUI(selectEdit);

            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("Edit Member");
            stage.setScene(new Scene(parent));
            stage.show();
            LibraryUtil.setStageIcon(stage);

            stage.setOnCloseRequest((e)->{
                handleMemberRefresh(new ActionEvent());
            });
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    void handleMemberRefresh(ActionEvent event) {
        loadData();
    }

}
