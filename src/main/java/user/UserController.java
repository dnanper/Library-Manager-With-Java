package user;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextArea;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import database.DataBaseHandler;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ui.theme.ThemeManager;
import util.LibraryUtil;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import javax.mail.search.FlagTerm;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class UserController implements Initializable {

    @FXML
    private TableColumn<?, ?> authorCol;

    @FXML
    private TableColumn<?, ?> authorCol1;

    @FXML
    private TableColumn<?, ?> availabilityCol;

    @FXML
    private TableColumn<?, ?> availabilityCol1;

    @FXML
    private Pane borrowPane;

    @FXML
    private TableColumn<?, ?> dayCol;

    @FXML
    private TableColumn<?, ?> genreCol;

    @FXML
    private TableColumn<?, ?> genreCol1;

    @FXML
    private TableColumn<?, ?> idCol;

    @FXML
    private TableColumn<?, ?> idCol1;

    @FXML
    private Pane libraryPane;

    @FXML
    private TableColumn<?, ?> publisherCol;

    @FXML
    private TableColumn<?, ?> publisherCol1;

    @FXML
    private Pane settingPane;

    @FXML
    private JFXButton mailButton;

    @FXML
    private Pane mailPane;

    @FXML
    private TableView<?> tableView;

    @FXML
    private TableView<?> tableView1;

    @FXML
    private TableColumn<?, ?> titleCol;

    @FXML
    private TableColumn<?, ?> titleCol1;

    @FXML
    private TextArea contentTextArea;

    @FXML
    private JFXListView<String> emailListView;

    private ObservableList<String> emailList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    void confirmEmailHandle(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/pscf.fxml"));
            Parent root = loader.load();

            pscfController pscfController = loader.getController();
            pscfController.setUserController(this);

            Stage stage = new Stage(StageStyle.DECORATED);
            Scene scene = new Scene(root);
            ThemeManager.setTheme(scene);

            stage.setScene(scene);
            stage.show();
            LibraryUtil.setStageIcon(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void receiveEmail(String email, String pass) {
        Properties props = new Properties();
        props.put("mail.store.protocol", "imaps");
        props.put("mail.imap.host", "imap.gmail.com");
        props.put("mail.imap.port", "993");
        props.put("mail.imap.ssl.enable", "true");
        try {
            Session session = Session.getInstance(props, null);
            Store store = session.getStore();
            store.connect("imap.gmail.com", email, pass);
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);

            int totalMessages = inbox.getMessageCount();
            int limit = 10;
            int start = Math.max(1, totalMessages - limit + 1); // Đảm bảo không lấy quá tổng số email
            Message[] latestMessages = inbox.getMessages(start, totalMessages);

            List<Message> unreadMessages = new ArrayList<>();
            for (Message message : latestMessages) {
                if (!message.isSet(Flags.Flag.SEEN)) {
                    unreadMessages.add(message);
                }
            }
            Message[] messages = unreadMessages.toArray(new Message[0]);

            int numThreads = 4;
            ExecutorService executor = Executors.newFixedThreadPool(numThreads);

            for (Message message : messages) {
                executor.submit(() -> {
                    try {
                        MimeMessage mimeMessage = (MimeMessage) message;
                        if (mimeMessage.getSubject() != null && mimeMessage.getSubject().equals("Library Warning")) {
                            emailList.add("From: " + "Library Manager" + " - Subject: " + message.getSubject());
                            // Đánh dấu email là đã đọc
                            mimeMessage.setFlag(Flags.Flag.SEEN, true);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }

            emailListView.setItems(emailList);

            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.MINUTES);

            inbox.close(false);
            store.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleEmailClick(MouseEvent event) {
        String selectedEmail = emailListView.getSelectionModel().getSelectedItem();
        if (selectedEmail != null) {
            try {
                if (selectedEmail.equals("From: Library Manager - Subject: Library Warning")) {
                    contentTextArea.setText("Hi,\n\nIf you won't return borrowed books, you will get banned.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
