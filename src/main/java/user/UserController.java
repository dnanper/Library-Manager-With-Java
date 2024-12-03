package user;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import database.GenericSearch;
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
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import database.DataBaseHandler;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Book;
import model.Paper;
import model.Thesis;
import ui.listbook.ListBookController;
import ui.listpaper.ListPaperController;
import ui.listthesis.ListThesisController;
import ui.settings.UserPreferences;
import ui.theme.ThemeManager;
import util.LibraryUtil;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import javax.mail.search.FlagTerm;
import java.net.URL;
import java.sql.Connection;
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

    // Thesis
    @FXML
    private TableView<Thesis> tableViewThesis;

    @FXML
    private TableColumn<?, ?> authorColThesis;

    @FXML
    private TableColumn<?, ?> availabilityColThesis;

    @FXML
    private TableColumn<?, ?> departmentCol;

    @FXML
    private TableColumn<?, ?> genreColThesis;

    @FXML
    private TableColumn<?, ?> idColThesis;

    @FXML
    private JFXTextField searchThesisText;

    @FXML
    private JFXButton thesisButton;

    @FXML
    private Pane thesisPane;

    @FXML
    private TableColumn<?, ?> titleColThesis;

    @FXML
    private TableColumn<?, ?> universityCol;

    // Paper
    @FXML
    private TableView<Paper> tableViewPaper;

    @FXML
    private TableColumn<?, ?> authorPaper;

    @FXML
    private TableColumn<?, ?> availabilityPaper;

    @FXML
    private TableColumn<?, ?> conferenceCol;

    @FXML
    private TableColumn<?, ?> genreColPaper;

    @FXML
    private TableColumn<?, ?> idColPaper;

    @FXML
    private JFXButton paperButton;

    @FXML
    private Pane paperPane;

    @FXML
    private JFXTextField searchPaperText;

    @FXML
    private TableColumn<?, ?> titleColPaper;

    @FXML
    private TableColumn<?, ?> yearCol;

    ObservableList<Thesis> listThesis = FXCollections.observableArrayList();
    ObservableList<Paper> listPaper = FXCollections.observableArrayList();

    @FXML
    void ViewPaperHandle(ActionEvent event) {
        listPaper.clear();
        showPane(paperPane);
        DataBaseHandler handler = DataBaseHandler.getInstance();
        String qu = "SELECT * FROM PAPER";
        ResultSet res = handler.execQuery(qu);
        try {
            while (res.next()) {
                String tit = res.getString("title");
                String aut = res.getString("author");
                String idx = res.getString("id");
                String conf = res.getString("conference");
                String yr = res.getString("release_year");
                String gen = res.getString("genre");
                Boolean ava = res.getBoolean("isAvail");

                listPaper.add(new Paper(tit, aut, idx, gen, conf, yr, ava));
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
        }
        tableViewPaper.setItems(listPaper);
        searchPaperText.textProperty().addListener((observable, oldValue, newValue) -> filterPaperList(newValue));
    }

    @FXML
    void ViewThesisHandle(ActionEvent event) {
        listThesis.clear();
        showPane(thesisPane);
        DataBaseHandler handler = DataBaseHandler.getInstance();
        String qu = "SELECT * FROM THESIS";
        ResultSet res = handler.execQuery(qu);
        try {
            while (res.next()) {
                String tit = res.getString("title");
                String aut = res.getString("author");
                String idx = res.getString("id");
                String uni = res.getString("university");
                String dep = res.getString("department");
                String gen = res.getString("genre");
                Boolean ava = res.getBoolean("isAvail");

                listThesis.add(new Thesis(tit, aut, idx, gen, uni, dep, ava));
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
        }
        tableViewThesis.setItems(listThesis);
        searchThesisText.textProperty().addListener((observable, oldValue, newValue) -> filterThesisList(newValue));
    }


    private ObservableList<String> emailList = FXCollections.observableArrayList();

    ObservableList<Book> list = FXCollections.observableArrayList();

    public static String userName;

    private Boolean banned = false;


    DataBaseHandler handler = DataBaseHandler.getInstance();
    Connection connection = handler.getConnection();
    GenericSearch<Book> bookSearch = new GenericSearch<>(connection);
    GenericSearch<Thesis> thesisSearch = new GenericSearch<>(connection);
    GenericSearch<Paper> paperSearch = new GenericSearch<>(connection);

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
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
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
        // thesis
        idColThesis.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColThesis.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColThesis.setCellValueFactory(new PropertyValueFactory<>("author"));
        departmentCol.setCellValueFactory(new PropertyValueFactory<>("department"));
        genreColThesis.setCellValueFactory(new PropertyValueFactory<>("genre"));
        availabilityColThesis.setCellValueFactory(new PropertyValueFactory<>("availability"));
        universityCol.setCellValueFactory(new PropertyValueFactory<>("university"));
        // paper
        idColPaper.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColPaper.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorPaper.setCellValueFactory(new PropertyValueFactory<>("author"));
        conferenceCol.setCellValueFactory(new PropertyValueFactory<>("conference"));
        genreColPaper.setCellValueFactory(new PropertyValueFactory<>("genre"));
        availabilityPaper.setCellValueFactory(new PropertyValueFactory<>("availability"));
        yearCol.setCellValueFactory(new PropertyValueFactory<>("year"));
    }

    public static void setUsername(String username) {
        userName = username;
    }

    private void filterBookList(String searchTitle) {
        if (searchTitle == null || searchTitle.isEmpty()) {
            tableView.setItems(list);
            return;
        }

        String condition = "LOWER(title) LIKE ?";
        Object[] parameters = new Object[]{"%" + searchTitle.toLowerCase() + "%"};

        List<Book> filterList = bookSearch.search("BOOK", condition, parameters, Book.class);

        ObservableList<Book> observableFilterList = FXCollections.observableArrayList(filterList);
        tableView.setItems(observableFilterList);
    }

    private void filterPaperList(String searchTitle) {
        if (searchTitle == null || searchTitle.isEmpty()) {
            tableViewPaper.setItems(listPaper);
            return;
        }

        String condition = "LOWER(title) LIKE ?";
        Object[] parameters = new Object[]{"%" + searchTitle.toLowerCase() + "%"};

        List<Paper> filterList = paperSearch.search("PAPER", condition, parameters, Paper.class);

        ObservableList<Paper> observableFilterList = FXCollections.observableArrayList(filterList);
        tableViewPaper.setItems(observableFilterList);
    }

    private void filterThesisList(String searchTitle) {
        if (searchTitle == null || searchTitle.isEmpty()) {
            tableViewThesis.setItems(listThesis);
            return;
        }

        String condition = "LOWER(title) LIKE ?";
        Object[] parameters = new Object[]{"%" + searchTitle.toLowerCase() + "%"};

        List<Thesis> filterList = thesisSearch.search("THESIS", condition, parameters, Thesis.class);

        ObservableList<Thesis> observableFilterList = FXCollections.observableArrayList(filterList);
        tableViewThesis.setItems(observableFilterList);
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
                list.add(new Book(tit, aut, idx, gen, pub, ava,null,null,null));
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
        List<Pane> allPanes = List.of(libraryPane, borrowPane, mailPane, settingPane, recommendPane, thesisPane, paperPane);

        // Show the specified pane and hide others
        for (Pane pane : allPanes) {
            pane.setVisible(pane == paneToShow);
        }
    }

}
