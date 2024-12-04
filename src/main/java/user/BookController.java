package user;

import alert.AlertMaker;
import database.DataBaseHandler;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.util.Map;

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
    private Label bookRatingLabel;

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

    public void loadBookData(String bookId) {
        if (bookId == null || bookId.isEmpty()) {
            System.err.println("No book ID provided.");
            reviewListView.getItems().clear();
            reviewListView.setPlaceholder(new Label("No review for this book yet"));
            return;
        }

        String actualBookId = bookId.startsWith("Book ID: ") ? bookId.replace("Book ID: ", "").trim() : bookId;

        String title = dataBaseHandler.getValueById(actualBookId, "title");
        String author = dataBaseHandler.getValueById(actualBookId, "author");
        String publisher = dataBaseHandler.getValueById(actualBookId, "publisher");
        String description = dataBaseHandler.getValueById(actualBookId, "description");
        String coverImageUrl = dataBaseHandler.getValueById(actualBookId, "urlCoverImage");
        String url = dataBaseHandler.getValueById(actualBookId, "url");

        bookIdLabel.setText("Book ID: " + actualBookId);
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
        Map<String, Double> ratings = dataBaseHandler.getBookRatings(bookId);
        double averageRating = ratings.getOrDefault("averageRating", 0.0);
        int totalRatings = ratings.getOrDefault("totalRatings", 0.0).intValue();

        if (averageRating > 0) {
            bookRatingLabel.setText(String.format("Rating: %.2f (%d ratings)", averageRating, totalRatings));
        } else {
            bookRatingLabel.setText("No ratings available");
        }

        ObservableList<String> reviews = dataBaseHandler.getBookReview(actualBookId);
        reviewListView.setItems(FXCollections.observableArrayList());
        reviewListView.setItems(reviews);
        if (reviews.isEmpty()) {
            reviewListView.setPlaceholder(new Label("No review for this book yet"));
        }
    }

    @FXML
    public void handleBookSelection(String bookId) {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/book.fxml"));
        try {
            Scene scene = new Scene(loader.load());
            BookController controller = loader.getController();
            controller.loadBookData(bookId);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSubmitReview() {
        String bookId = bookIdLabel.getText();
        String actualBookId = bookId.startsWith("Book ID: ") ? bookId.replace("Book ID: ", "").trim() : bookId;

        String rating = ratingField.getText();
        String review = reviewField.getText();

        if (actualBookId.isEmpty() || review == null || review.isEmpty()) {
            AlertMaker.showSimpleAlert("Fail", "Enter reviews and rating again");
            return;
        }

        try {
            double ratingValue = 0;
            if (!rating.isEmpty()) {
                try {
                    ratingValue = Double.parseDouble(rating);
                    if (ratingValue < 0 || ratingValue > 5) {
                        throw new NumberFormatException("Rating must be from 0 to 5");
                    }
                } catch (NumberFormatException e) {
                    AlertMaker.showSimpleAlert("Invalid rating", "Try again");
                    return;
                }
            }

            String query = "INSERT INTO REVIEW (userID, bookID, review, rating) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = dataBaseHandler.getConnection().prepareStatement(query)) {
                stmt.setString(1, UserController.userName);
                stmt.setString(2, actualBookId);
                stmt.setString(3, review);
                stmt.setDouble(4, ratingValue);

                int rowsInserted = stmt.executeUpdate();
                if (rowsInserted > 0) {
                    loadBookData(actualBookId); // Refresh data to include the new review
                    ratingField.clear();
                    reviewField.clear();
                    AlertMaker.showSimpleAlert("Success", "Your reviews have been recorded");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            AlertMaker.showSimpleAlert("Fail", "Cannot send your review");
        }
    }
}
