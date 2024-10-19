package api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import com.google.gson.JsonObject;

public class ApiController {
    private Api apiHandle = new Api();

    @FXML
    private TextField isbnField;

    @FXML
    private TextField titleField;

    @FXML
    private Label statusLabel;

    @FXML
    private Button addISBNButton;

    @FXML
    private Button addTitleButton;

    // Method to handle "Add by ISBN" button click
    @FXML
    private void onAddByISBN() {
        String isbn = isbnField.getText();
        if (isbn != null && !isbn.isEmpty()) {
            JsonObject bookData = apiHandle.getBookByISBN(isbn);

            if (bookData != null && !bookData.isJsonNull()) {
                String title = getBookTitle(bookData);
                String author = getBookAuthor(bookData);
                statusLabel.setText("Book added by ISBN: " + title + " by " + author);
            } else {
                statusLabel.setText("No book found for ISBN: " + isbn);
            }
        } else {
            statusLabel.setText("Please enter an ISBN.");
        }
    }

    // Method to handle "Add by Title" button click
    @FXML
    private void onAddByTitle() {
        String title = titleField.getText();
        if (title != null && !title.isEmpty()) {
            JsonObject bookData = apiHandle.getBookByTitle(title);

            if (bookData != null && !bookData.isJsonNull()) {
                String bookTitle = getBookTitle(bookData);
                String author = getBookAuthor(bookData);
                statusLabel.setText("Book added by Title: " + bookTitle + " by " + author);
            } else {
                statusLabel.setText("No book found for Title: " + title);
            }
        } else {
            statusLabel.setText("Please enter a Title.");
        }
    }

    // Helper methods to extract book details
    public String getBookTitle(JsonObject bookJson) {
        if (bookJson == null) {
            System.out.println("Book JSON is null");
            return "No title found";
        }

        JsonObject volumeInfo = bookJson.getAsJsonObject("volumeInfo");
        if (volumeInfo == null) {
            System.out.println("Volume info is missing");
            return "No title found";
        }

        JsonElement titleElement = volumeInfo.get("title");
        if (titleElement == null) {
            System.out.println("Title is missing");
            return "No title found";
        }

        return titleElement.getAsString();
    }

    private String getBookAuthor(JsonObject bookData) {
        // Check if "volumeInfo" exists and is not null
        JsonObject volumeInfo = bookData.getAsJsonObject("volumeInfo");
        if (volumeInfo != null) {
            // Check if "authors" exists and is not null
            JsonArray authors = volumeInfo.getAsJsonArray("authors");
            if (authors != null && authors.size() > 0) {
                // Return the first author as a string
                return authors.get(0).getAsString();
            } else {
                // Handle case where "authors" is missing or empty
                return "Unknown Author";
            }
        } else {
            // Handle case where "volumeInfo" is missing
            return "No Volume Info Available";
        }
    }
}

