package user;

import alert.AlertMaker;
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

    @FXML
    private JFXTextField accountText;

    @FXML
    private JFXTextField passwordText;

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
        initDefaultValues();
        initCol();
        setupTableClickHandlers();
        setupPane();
    }

    private void initDefaultValues() {
        UserPreferences.User currentUser = UserPreferences.findUser(userName);
        if (currentUser!= null) {
            accountText.setText(currentUser.getUsername());
            passwordText.setText(currentUser.getPassword());
            accountText.setEditable(false);
        }
    }

    /**
     * Sets up the UI panes based on the user's banned status. Checks the user's banned status in the `UserPreferences` and if banned, makes the normal pane
     * disabled and invisible while enabling and showing the ban pane. Otherwise, does the opposite.
     */
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

    /**
     * Sets up a click handler for a specific table view. When a row in the table is clicked with the left mouse button and a single click,
     * it retrieves the selected book and calls the `handleBookSelection` method in the `BookController` to handle further actions related to the selected book.
     *
     * @param tableView The table view for which the click handler is being set up.
     */
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
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        publisherCol.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        genreCol.setCellValueFactory(new PropertyValueFactory<>("genre")); // Kết nối cột genre
        availabilityCol.setCellValueFactory(new PropertyValueFactory<>("availability"));

        titleCol1.setCellValueFactory(new PropertyValueFactory<>("title"));
        idCol1.setCellValueFactory(new PropertyValueFactory<>("id"));
        authorCol1.setCellValueFactory(new PropertyValueFactory<>("author"));
        publisherCol1.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        genreCol1.setCellValueFactory(new PropertyValueFactory<>("genre"));
        availabilityCol1.setCellValueFactory(new PropertyValueFactory<>("availability"));

        titleCol2.setCellValueFactory(new PropertyValueFactory<>("title"));
        idCol2.setCellValueFactory(new PropertyValueFactory<>("id"));
        authorCol2.setCellValueFactory(new PropertyValueFactory<>("author"));
        publisherCol2.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        genreCol2.setCellValueFactory(new PropertyValueFactory<>("genre"));
        availabilityCol2.setCellValueFactory(new PropertyValueFactory<>("availability"));

        idColThesis.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColThesis.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColThesis.setCellValueFactory(new PropertyValueFactory<>("author"));
        departmentCol.setCellValueFactory(new PropertyValueFactory<>("department"));
        genreColThesis.setCellValueFactory(new PropertyValueFactory<>("genre"));
        availabilityColThesis.setCellValueFactory(new PropertyValueFactory<>("availability"));
        universityCol.setCellValueFactory(new PropertyValueFactory<>("university"));

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

    /**
     * Handles the action when the user clicks the button to confirm an email.
     * This method loads the FXML layout for the `pscf` view, sets the `UserController` instance for the loaded `pscfController`,
     * creates a new stage, applies a theme to the scene, shows the stage, and sets the stage icon.
     *
     * @param event The action event triggered by the user clicking the confirm email button.
     */
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

    /**
     * Handles the process of receiving emails for the user.
     * It shows the mail pane in the UI, configures the properties for connecting to an IMAP email server, retrieves unread emails,
     * filters for emails with the subject "Library Warning", adds relevant information to the `emailList`, marks the emails as read,
     * updates the email list view, shuts down the executor service used for asynchronous processing, and closes the email connection.
     *
     * @param email The email address used to connect to the email server.
     * @param pass The password for the email account.
     */
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

    /**
     * Handles the click event on an email item in the email list view.
     * If the selected email has the subject "Library Warning", it sets the content text area with a specific warning message.
     *
     * @param event The mouse click event on an item in the email list view.
     */
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

    /**
     * Handles the action when the user clicks the button to view the borrowed books.
     * It clears the existing list of books, shows the borrow pane, retrieves the list of books issued to the current user from the database,
     * and sets the items of the corresponding table view.
     *
     * @param event The action event triggered by the user clicking the view borrowed books button.
     */
    @FXML
    public void viewBorrowBookHandle(ActionEvent event) {
        list.clear();
        showPane(borrowPane);
        DataBaseHandler handler = DataBaseHandler.getInstance();
        list = handler.getBooksIssuedToMember(userName);
        tableView1.setItems(list);
    }

    /**
     * Handles the action when the user clicks the button to view the library (books available in the library).
     * It clears the existing list of books, shows the library pane, queries the database for all books, populates the list with book objects,
     * sets the items of the table view, and adds a listener to the search text field to filter the book list based on user input.
     *
     * @param event The action event triggered by the user clicking the view library button.
     */
    @FXML
    public void ViewLibraryHandle(ActionEvent event) {
        list.clear();
        showPane(libraryPane);
        DataBaseHandler handler = DataBaseHandler.getInstance();
        String qu = "SELECT * FROM BOOK";
        ResultSet res = handler.execQuery(qu);
        try {
            while (res.next()) {
                String tit = res.getString("title");
                String aut = res.getString("author");
                String idx = res.getString("id");
                String pub = res.getString("publisher");
                String gen = res.getString("genre");
                Boolean ava = res.getBoolean("isAvail");

                list.add(new Book(tit, aut, idx, gen, pub, ava,null,null,null));
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
        }
        tableView.setItems(list);
        searchBookText.textProperty().addListener((observable, oldValue, newValue) -> {
            filterBookList(newValue);
        });
    }

    /**
     * Handles the action when the user clicks the button to view recommended books.
     * It clears the existing list of books, shows the recommend pane, determines the favorite genre (or uses a default if not specified),
     * retrieves the recommended books for the current user from the database based on the genre, and sets the items of the corresponding table view.
     *
     * @param event The action event triggered by the user clicking the recommend button.
     */
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

    /**
     * Controls the visibility of different panes in the UI.
     * It iterates over a list of all available panes and makes only the specified `paneToShow` visible while hiding the others.
     *
     * @param paneToShow The pane that should be made visible.
     */
    private void showPane(Pane paneToShow) {
        List<Pane> allPanes = List.of(libraryPane, borrowPane, mailPane, settingPane, recommendPane, thesisPane, paperPane);

        for (Pane pane : allPanes) {
            pane.setVisible(pane == paneToShow);
        }
    }

    @FXML
    void handleSaveButton(ActionEvent event) {
        try {
            String pass = passwordText.getText();
            String acc = accountText.getText();

            if (pass.isEmpty() || acc.isEmpty()) {
                AlertMaker.showErrorMessage("Invalid Input", "Please enter values for both password and account fields.");
                return;
            }

            UserPreferences.User currentUser = UserPreferences.findUser(UserController.userName);
            if (currentUser == null) {
                AlertMaker.showErrorMessage("Error", "User not found in preferences.");
                return;
            }
            currentUser.setPassword(pass);
            List<UserPreferences.User> userList = UserPreferences.loadUsers();
            if (userList == null) {
                userList = new ArrayList<>();
            }
            boolean userUpdated = false;
            for (int i = 0; i < userList.size(); i++) {
                if (userList.get(i).getUsername().equals(UserController.userName)) {
                    userList.set(i, currentUser);
                    userUpdated = true;
                    break;
                }
            }
            if (!userUpdated) {
                userList.add(currentUser);
            }
            try {
                UserPreferences.updateUserList(userList);
                AlertMaker.showErrorMessage("Success", "Account information has been updated successfully.");
            } catch (Exception e) {
                AlertMaker.showErrorMessage("Error", "There was an issue updating the account information. Please try again.");
                e.printStackTrace();
            }
        } catch (NumberFormatException e) {
            AlertMaker.showErrorMessage("Invalid Input", "Please enter valid numbers for password.");
        }
    }
}
