package user;

import alert.AlertMaker;
import database.DataBaseHandler;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import api.ApiSearchController;
import ui.listbook.ListBookController;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BookController {
    private final ApiSearchController search = new ApiSearchController();
    private final DataBaseHandler dataBaseHandler = DataBaseHandler.getInstance();


    @FXML
    private ImageView bookCoverImageView;
    @FXML
    private ImageView qrCodeImageView;
    @FXML
    private Label bookIdLabel;
    @FXML
    private Label bookTitleLabel;
    @FXML
    private Label bookAuthorLabel;
    @FXML
    private Label bookPublisherLabel;
    @FXML
    private Label bookDescriptionLabel;
    @FXML
    private ListView<String> reviewListView;

    @FXML
    private TextField ratingField;
    @FXML
    private TextArea reviewField;
    @FXML
    private Button submitReviewButton;

    @FXML
    public void initialize() {
        submitReviewButton.setOnAction(event -> handleSubmitReview());
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


        bookIdLabel.setText("Book ID: " + bookId);
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

    private void loadReviews(String bookId) {
        if (bookId == null || bookId.isEmpty()) {
            reviewListView.getItems().clear();
            reviewListView.setPlaceholder(new Label("No review for this book yet"));
            return;
        }

        reviewListView.setItems(dataBaseHandler.getBookReview(bookId));
    }

    @FXML
    private void handleSubmitReview() {
        String bookId = bookIdLabel.getText();
        String rating = ratingField.getText();
        String review = reviewField.getText();

        if (bookId == null || bookId.isEmpty() || review == null || review.isEmpty()) {
            AlertMaker.showSimpleAlert("Fail","You haven't entered a review and rating");
            return;
        }

        try {

            double ratingValue = 0;
            if (!rating.isEmpty()) {
                try {
                    ratingValue = Double.parseDouble(rating);
                    if (ratingValue < 0 || ratingValue > 5) {
                        throw new NumberFormatException("Rating must be between 0 and 5");
                    }
                } catch (NumberFormatException e) {
                    AlertMaker.showSimpleAlert("Invalid Rating", "Please enter a valid rating between 0 and 5.");
                    return;
                }
            }

            String query = "INSERT INTO REVIEW (userID, bookID, review, rating) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = dataBaseHandler.getConnection().prepareStatement(query);
            stmt.setString(1, UserController.userName);
            stmt.setString(2, bookId);
            stmt.setString(3, review);
            stmt.setDouble(4, ratingValue);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                loadReviews(bookId);
                ratingField.clear();
                reviewField.clear();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            AlertMaker.showSimpleAlert("Database Error", "Failed to submit your review.");
        }
    }


}
