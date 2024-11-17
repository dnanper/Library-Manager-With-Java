package user;

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

public class BookController {
    private final ApiSearchController search = new ApiSearchController();

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



    public void loadBookDetails(ListBookController.Book book) {
        bookTitleLabel.setText(book.getTitle());
        bookAuthorLabel.setText("Author: " + book.getAuthor());
        bookPublisherLabel.setText("Publisher: " + book.getPublisher());
        bookDescriptionLabel.setText(book.getDescription());

        bookCoverImageView.setImage(new Image(book.getUrlCoverImage()));

        BufferedImage qrCodeImage = search.getQRCode(book.getUrl());
        if (qrCodeImage != null) {
            qrCodeImageView.setImage(search.convertToJavaFXImage(qrCodeImage));
        }
    }

}
