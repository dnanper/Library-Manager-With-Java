package api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import database.DataBaseHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class ApiSearchController {

    private Api apiHandle = new Api();
    private DataBaseHandler dataBaseHandler = DataBaseHandler.getInstance();

    @FXML
    private TextField nameField; // For searching by book name

    @FXML
    private ListView<String> searchResultsList; // To display matching books

    private ObservableList<JsonObject> searchResults = FXCollections.observableArrayList(); // To hold search results

    @FXML
    private void initialize() {
        // Bind the ListView to the searchResults observable list
        searchResultsList.setItems(FXCollections.observableArrayList()); // Clear initial items
    }

    @FXML
    private void onSearchByName() {
        String searchText = nameField.getText().trim();
        if (!searchText.isEmpty()) {
            JsonObject response = apiHandle.getBookByTitle(searchText);

            // Clear previous search results from the observable list and ListView
            searchResults.clear();
            searchResultsList.getItems().clear(); // Clear items in the ListView

            if (response != null && response.has("items")) {
                JsonArray items = response.getAsJsonArray("items");

                // Process the items array and populate the observable list
                for (JsonElement item : items) {
                    JsonObject volumeInfo = item.getAsJsonObject().getAsJsonObject("volumeInfo");

                    // Add the JsonObject to the searchResults list
                    searchResults.add(volumeInfo); // Add the whole JsonObject

                    // Create a display string and add it to the ListView
                    String title = getBookTitle(volumeInfo);
                    String author = getBookAuthor(volumeInfo);
                    String publisher = getBookPublisher(volumeInfo);

                    // Format the string to be displayed in the ListView
                    String displayString = title + " by " + author + " (Publisher: " + publisher + ")";
                    searchResultsList.getItems().add(displayString); // Add display string to ListView
                }
            } else {
                System.out.println("No books found for: " + searchText);
            }
        } else {
            System.out.println("Please enter a search term.");
        }
    }

    // Method to add the selected book to the database
    @FXML
    private void onAddSelectedBook() {
        int selectedIndex = searchResultsList.getSelectionModel().getSelectedIndex();

        // Debug: Print the selected index
        System.out.println("Selected Index: " + selectedIndex);

        // Ensure the selected index is valid
        if (selectedIndex >= 0 && selectedIndex < searchResults.size()) {
            JsonObject selectedBook = searchResults.get(selectedIndex); // Get the selected JsonObject

            // Extract book details
            String title = getBookTitle(selectedBook);
            String author = getBookAuthor(selectedBook);
            String publisher = getBookPublisher(selectedBook);
            String bookID = getBookISBN(selectedBook); // Use ISBN as the book ID

            // Debug: Print extracted details
            System.out.println("Selected Book - Title: " + title + ", Author: " + author + ", Publisher: " + publisher + ", ISBN: " + bookID);

            // Insert into the database
            String insertQuery = "INSERT INTO BOOK (id, title, author, publisher, isAvail) VALUES (" +
                    "'" + bookID + "'," +
                    "'" + title + "'," +
                    "'" + author + "'," +
                    "'" + publisher + "'," +
                    "true)";

            // Debug: Print the insert query
            System.out.println("Insert Query: " + insertQuery);

            // Execute the database action and log the result
            if (dataBaseHandler.execAction(insertQuery)) {
                System.out.println("Book added successfully: " + title + " by " + author);
            } else {
                System.out.println("Failed to add the book to the database.");
            }
        } else {
            System.out.println("No book selected.");
        }
    }

    // Helper methods to get book details remain unchanged
    private String getBookTitle(JsonObject bookJson) {
        return bookJson.has("title") ? bookJson.get("title").getAsString() : "No Title Found";
    }

    private String getBookISBN(JsonObject bookJson) {
        // Access the volumeInfo object
        JsonObject volumeInfo = bookJson.getAsJsonObject("volumeInfo");
        if (volumeInfo != null && volumeInfo.has("industryIdentifiers")) {
            JsonArray identifiers = volumeInfo.getAsJsonArray("industryIdentifiers");
            for (JsonElement idElement : identifiers) {
                // Check if the current element is a JsonObject
                if (idElement.isJsonObject()) {
                    JsonObject identifier = idElement.getAsJsonObject();
                    // Check for ISBN_13 type
                    if (identifier.has("type") && identifier.get("type").getAsString().equals("ISBN_13")) {
                        return identifier.get("identifier").getAsString();
                    }
                }
            }
        }
        return "No ISBN Found"; // Fallback if no ISBN is found
    }

    private String getBookAuthor(JsonObject bookJson) {
        JsonArray authors = bookJson.has("authors") ? bookJson.getAsJsonArray("authors") : null;
        return (authors != null && authors.size() > 0) ? authors.get(0).getAsString() : "No Author Found";
    }

    private String getBookPublisher(JsonObject bookJson) {
        return bookJson.has("publisher") ? bookJson.get("publisher").getAsString() : "No Publisher Found";
    }
}
