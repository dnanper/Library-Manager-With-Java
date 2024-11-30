package user;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import database.DataBaseHandler;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Book;
import ui.listbook.ListBookController;
import ui.settings.UserPreferences;
import ui.theme.ThemeManager;
import util.LibraryUtil;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import javax.mail.search.FlagTerm;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserController implements Initializable {

    @FXML
    private TableColumn<?, ?> authorCol;

    @FXML
    private TableColumn<?, ?> authorCol1;

    @FXML
    private TableColumn<?, ?> authorCol2;

    @FXML
    private TableColumn<?, ?> availabilityCol;

    @FXML
    private TableColumn<?, ?> availabilityCol1;

    @FXML
    private TableColumn<?, ?> availabilityCol2;

    @FXML
    private Pane borrowPane;

    @FXML
    private TableColumn<?, ?> dayCol;

    @FXML
    private TableColumn<?, ?> genreCol;

    @FXML
    private TableColumn<?, ?> genreCol1;

    @FXML
    private TableColumn<?, ?> genreCol2;

    @FXML
    private TableColumn<?, ?> idCol;

    @FXML
    private TableColumn<?, ?> idCol1;

    @FXML
    private TableColumn<?, ?> idCol2;

    @FXML
    private Pane libraryPane;

    @FXML
    private Pane recommendPane;

    @FXML
    private TableColumn<?, ?> publisherCol;

    @FXML
    private TableColumn<?, ?> publisherCol1;

    @FXML
    private TableColumn<?, ?> publisherCol2;

    @FXML
    private Pane settingPane;

    @FXML
    private JFXButton mailButton;

    @FXML
    private JFXButton settingButton;

    @FXML
    private JFXButton libraryButton;

    @FXML
    private JFXButton borrowButton;

    @FXML
    private JFXButton recommendButton;

    @FXML
    private Pane mailPane;

    @FXML
    private TableView<Book> tableView;

    @FXML
    private TableView<Book> tableView2;

    @FXML
    private TableView<Book> tableView1;

    @FXML
    private TableColumn<?, ?> titleCol;

    @FXML
    private TableColumn<?, ?> titleCol1;

    @FXML
    private TableColumn<?, ?> titleCol2;

    @FXML
    private TextArea contentTextArea;

    @FXML
    private JFXTextField favourGerne;

    @FXML
    private JFXListView<String> emailListView;

    @FXML
    private JFXTextField searchBookText;

    @FXML
    private Pane banPane;

    @FXML
    private AnchorPane normalPane;

    private ObservableList<String> emailList = FXCollections.observableArrayList();

    ObservableList<Book> list = FXCollections.observableArrayList();

    public static String userName;

    private Boolean banned = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        initCol();
        setupTableClickHandlers();
        setupPane();
    }

    private void setupPane() {
        List<UserPreferences.User> uList = UserPreferences.loadUsers();
        for (UserPreferences.User user : uList) {
            if (user.getUsername().equals(userName)) {
                if (user.getBanned()) {
                    banned = true;
                }
                break;
            }
        }
        if (banned != null && banned) {
            normalPane.setDisable(true);
            normalPane.setVisible(false);
            banPane.setDisable(false);
            banPane.setVisible(true);
        } else {
            normalPane.setDisable(false);
            normalPane.setVisible(true);
            banPane.setDisable(true);
            banPane.setVisible(false);
        }
    }


    private void setupTableClickHandlers() {
        setupTableClickHandler(tableView);
        setupTableClickHandler(tableView1);
        setupTableClickHandler(tableView2);
    }

    private void setupTableClickHandler(TableView<Book> tableView) {
        BookController loadBook = new BookController();
        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                Book selectedBook = tableView.getSelectionModel().getSelectedItem();
                if (selectedBook != null) {
                    loadBook.handleBookSelection(selectedBook.getId());
                }
            }
        });
    }
    private void initCol() {
        // library
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        publisherCol.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        genreCol.setCellValueFactory(new PropertyValueFactory<>("genre")); // Kết nối cột genre
        availabilityCol.setCellValueFactory(new PropertyValueFactory<>("availability"));
        // borrow
        titleCol1.setCellValueFactory(new PropertyValueFactory<>("title"));
        idCol1.setCellValueFactory(new PropertyValueFactory<>("id"));
        authorCol1.setCellValueFactory(new PropertyValueFactory<>("author"));
        publisherCol1.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        genreCol1.setCellValueFactory(new PropertyValueFactory<>("genre")); // Kết nối cột genre
        availabilityCol1.setCellValueFactory(new PropertyValueFactory<>("availability"));
        // recommend
        titleCol2.setCellValueFactory(new PropertyValueFactory<>("title"));
        idCol2.setCellValueFactory(new PropertyValueFactory<>("id"));
        authorCol2.setCellValueFactory(new PropertyValueFactory<>("author"));
        publisherCol2.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        genreCol2.setCellValueFactory(new PropertyValueFactory<>("genre")); // Kết nối cột genre
        availabilityCol2.setCellValueFactory(new PropertyValueFactory<>("availability"));
    }

    public static void setUsername(String username) {
        userName = username;
    }

    private void filterBookList(String searchTitle) {
        if (searchTitle == null || searchTitle.isEmpty()) {
            tableView.setItems(list);
            return;
        }
        ObservableList<Book> filterList = FXCollections.observableArrayList();
        for (Book book : list) {
            if (book.getTitle().toLowerCase().contains(searchTitle.toLowerCase())) {
                filterList.add(book);
            }
        }
        tableView.setItems(filterList);
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
        showPane(mailPane);

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
    public void handleEmailClick(MouseEvent event) {
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

    @FXML
    public void settingHandle(ActionEvent event) {
        showPane(settingPane);
    }

    @FXML
    public void viewBorrowBookHandle(ActionEvent event) {
        list.clear();
        showPane(borrowPane);
        DataBaseHandler handler = DataBaseHandler.getInstance();
        list = handler.getBooksIssuedToMember(userName);
        tableView1.setItems(list);
    }

    @FXML
    public void ViewLibraryHandle(ActionEvent event) {
        list.clear();
        showPane(libraryPane);
        DataBaseHandler handler = DataBaseHandler.getInstance();
        String qu = "SELECT * FROM BOOK";
        ResultSet res = handler.execQuery(qu);
        try {
            // get all attributes of each book from database
            while (res.next()) {
                String tit = res.getString("title");
                String aut = res.getString("author");
                String idx = res.getString("id");
                String pub = res.getString("publisher");
                String gen = res.getString("genre"); // Lấy genre từ kết quả truy vấn
                Boolean ava = res.getBoolean("isAvail");

                // add data of book to list
                list.add(new Book(tit, idx, aut, pub, gen, ava,null,null,null));
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
        }
        // push all elements in list to table
        tableView.setItems(list);
        // search Book
        searchBookText.textProperty().addListener((observable, oldValue, newValue) -> {
            filterBookList(newValue);
        });
    }

    @FXML
    public void recommendHandle(ActionEvent event) {
        list.clear();
        showPane(recommendPane);
        String favGene = favourGerne.getText().toLowerCase();
        if (favGene.equals("recent") || favGene.isEmpty()) {
            DataBaseHandler handler = DataBaseHandler.getInstance();
            list = handler.getRecommendedBooksForMember(userName);
        } else {
            DataBaseHandler handler = DataBaseHandler.getInstance();
            list = handler.getRecommendedBooksForMember(userName, favGene);
        }
        tableView2.setItems(list);
    }

    private void showPane(Pane paneToShow) {
        // List of all panes
        List<Pane> allPanes = List.of(libraryPane, borrowPane, mailPane, settingPane, recommendPane);

        // Show the specified pane and hide others
        for (Pane pane : allPanes) {
            pane.setVisible(pane == paneToShow);
        }
    }

}
