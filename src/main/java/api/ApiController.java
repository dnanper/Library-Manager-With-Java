package api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import database.DataBaseHandler;




public class ApiController {
    private Api apiHandle = new Api();
    private DataBaseHandler dataBaseHandler;

    @FXML
    private TextField isbnField;

    @FXML
    private Label statusLabel;

    @FXML
    private Button addISBNButton;

    @FXML
    public void initialize() {
        dataBaseHandler = DataBaseHandler.getInstance(); // Initialize the DataBaseHandler
    }

    // Method to handle "Add by ISBN" button click
    @FXML
    private void onAddByISBN() {
        String isbn = isbnField.getText();
        if (isbn == null || isbn.isEmpty()) {
            statusLabel.setText("Please enter an ISBN.");
            return;
        }

        JsonObject bookData = apiHandle.getBookByISBN(isbn);

        // Debug: Print the entire bookData response
        System.out.println(bookData);

        if (bookData == null || bookData.isJsonNull() || !bookData.has("items")) {
            statusLabel.setText("No book found for ISBN: " + isbn);
            return;
        }

        JsonArray items = bookData.getAsJsonArray("items");
        if (items.size() == 0) {
            statusLabel.setText("No book found for ISBN: " + isbn);
            return;
        }

        JsonObject volumeInfo = items.get(0).getAsJsonObject().getAsJsonObject("volumeInfo");

        String title = getBookTitle(volumeInfo);
        String author = getBookAuthor(volumeInfo);
        String publisher = getBookPublisher(volumeInfo); // Fetch publisher

        // Now insert into the database
        String bookID = isbn; // Use ISBN as the book ID
        String insertQuery = "INSERT INTO BOOK (id, title, author, publisher, isAvail) VALUES (" +
                "'" + bookID + "'," +
                "'" + title + "'," +
                "'" + author + "'," +
                "'" + publisher + "'," +
                true + ")";

        // Check if the database insertion is successful
        if (dataBaseHandler.execAction(insertQuery)) {
            statusLabel.setText("Book added successfully: " + title + " by " + author);
        } else {
            statusLabel.setText("Failed to add the book to the database.");
        }
    }

    // Helper method to extract book title
    public String getBookTitle(JsonObject volumeInfo) {
        if (volumeInfo == null) {
            return "No title found (missing volumeInfo)";
        }

        JsonElement titleElement = volumeInfo.get("title");
        return (titleElement != null && !titleElement.isJsonNull()) ? titleElement.getAsString() : "No title found";
    }

    // Helper method to extract book author
    private String getBookAuthor(JsonObject volumeInfo) {
        if (volumeInfo == null) {
            return "No author found (missing volumeInfo)";
        }

        JsonArray authors = volumeInfo.getAsJsonArray("authors");
        if (authors == null || authors.size() == 0) {
            return "Unknown Author";
        }

        return authors.get(0).getAsString();
    }

    // Helper method to get volumeInfo safely
    private JsonObject getVolumeInfo(JsonObject bookJson) {
        if (bookJson == null || bookJson.isJsonNull()) {
            return null;
        }
        return bookJson.getAsJsonObject("volumeInfo");
    }

    private String getBookPublisher(JsonObject volumeInfo) {
        if (volumeInfo == null) {
            return "No publisher found";
        }

        JsonElement publisherElement = volumeInfo.get("publisher");
        return (publisherElement != null && !publisherElement.isJsonNull()) ? publisherElement.getAsString() : "Unknown Publisher";
    }
}
