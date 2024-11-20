package user;

import database.DataBaseHandler;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import api.ApiSearchController;
import ui.listbook.ListBookController;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class BookController {
    private final ApiSearchController search = new ApiSearchController();
    private final DataBaseHandler dataBaseHandler = DataBaseHandler.getInstance();


    @FXML
    private ImageView bookCoverImageView;
    @FXML
    private ImageView qrCodeImageView;
    @FXML
    private Label bookTitleLabel;
    @FXML
    private Label bookAuthorLabel;
    @FXML
    private Label bookPublisherLabel;
    @FXML
    private Label bookDescriptionLabel;
    @FXML
    private TextField ratingField;
    @FXML
    private TextArea reviewField;
    @FXML
    private Button submitReviewButton;

    @FXML
    public void initialize() {

    }

    public void loadBookDetails(String bookId) {
        if (bookId == null || bookId.isEmpty()) {
            System.err.println("No book ID provided.");
            return;
        }

        String title = dataBaseHandler.getValueById(bookId, "title");
        String author = dataBaseHandler.getValueById(bookId, "author");
        String publisher = dataBaseHandler.getValueById(bookId, "publisher");
        String description = dataBaseHandler.getValueById(bookId, "description");
        String coverImageUrl = dataBaseHandler.getValueById(bookId, "urlCoverImage");
        String url = dataBaseHandler.getValueById(bookId, "url");

        bookTitleLabel.setText(title != null ? title : "N/A");
        bookAuthorLabel.setText("Author: " + (author != null ? author : "N/A"));
        bookPublisherLabel.setText("Publisher: " + (publisher != null ? publisher : "N/A"));
        bookDescriptionLabel.setText(description != null ? description : "N/A");

        if (coverImageUrl != null && !coverImageUrl.isEmpty()) {
            bookCoverImageView.setImage(new Image(coverImageUrl));
        } else {
            bookCoverImageView.setImage(null);
        }

        if (url != null && !url.isEmpty()) {
            BufferedImage qrCodeImage = search.getQRCode(url);
            if (qrCodeImage != null) {
                qrCodeImageView.setImage(search.convertToJavaFXImage(qrCodeImage));
            } else {
                qrCodeImageView.setImage(null);
            }
        }
    }


    @FXML
    public void handleBookSelection(String bookId) {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/book.fxml"));
        try {
            Scene scene = new Scene(loader.load());
            BookController controller = loader.getController();
            controller.loadBookDetails(bookId);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
