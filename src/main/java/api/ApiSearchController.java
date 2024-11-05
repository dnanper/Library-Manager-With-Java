package api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import alert.AlertMaker;
import database.DataBaseHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

public class ApiSearchController {

    private Api apiHandle = new Api();
    private DataBaseHandler dataBaseHandler = DataBaseHandler.getInstance();

    @FXML
    private TextField nameField;

    @FXML
    private ListView<String> searchResultsList; // To display matching books

    private ObservableList<JsonObject> searchResults = FXCollections.observableArrayList(); // To hold search results

    @FXML
    private void initialize() {
        searchResultsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    @FXML
    private void onSearchByName() {
        nameField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {

                String searchText = nameField.getText().trim();
                if (!searchText.isEmpty()) {
                    JsonObject response = apiHandle.getBookByTitle(searchText);

                    searchResults.clear();
                    searchResultsList.getItems().clear();

                    if (response != null && response.has("items")) {
                        JsonArray items = response.getAsJsonArray("items");

                        for (JsonElement item : items) {
                            JsonObject volumeInfo = item.getAsJsonObject().getAsJsonObject("volumeInfo");

                            // Add the JsonObject to the searchResults list
                            searchResults.add(volumeInfo);

                            // Create a display string and add it to the ListView
                            String title = getBookTitle(volumeInfo);
                            String author = getBookAuthor(volumeInfo);
                            String publisher = getBookPublisher(volumeInfo);
                            String genre = getBookGenre(volumeInfo); // Retrieve genre

                            // Format the string to be displayed in the ListView
                            String displayString = title + " by " + author + " (Publisher: " + publisher + ", Genre: " + genre + ")";
                            searchResultsList.getItems().add(displayString); // Add display string to ListView
                        }
                    } else {
                        AlertMaker.showSimpleAlert("Search Result", "No books found for: " + searchText); // Use AlertMaker
                    }
                } else {
                    AlertMaker.showSimpleAlert("Input Error", "Please enter a search term."); // Use AlertMaker
                }
            }
        });
    }

    @FXML
    private void onAddSelectedBook() {
        ObservableList<Integer> selectedIndices = searchResultsList.getSelectionModel().getSelectedIndices();

        if (!selectedIndices.isEmpty()) {
            for (int selectedIndex : selectedIndices) {
                if (selectedIndex >= 0 && selectedIndex < searchResults.size()) {
                    JsonObject selectedBook = searchResults.get(selectedIndex);

                    // Extract book details and escape single quotes for SQL
                    String title = getBookTitle(selectedBook).replace("'", "''");
                    String author = getBookAuthor(selectedBook).replace("'", "''");
                    String publisher = getBookPublisher(selectedBook).replace("'", "''");
                    String genre = getBookGenre(selectedBook).replace("'", "''"); // Get genre
                    String bookID = getBookISBN(selectedBook);

                    // Insert into the database
                    String insertQuery = "INSERT INTO BOOK (id, title, author, publisher, isAvail, genre) VALUES (" +
                            "'" + bookID + "'," +
                            "'" + title + "'," +
                            "'" + author + "'," +
                            "'" + publisher + "'," +
                            "true," +  // Assuming the book is available
                            "'" + genre + "'" + // Include genre in the insert statement
                            ")";

                    // Execute the database action and log the result
                    if (dataBaseHandler.execAction(insertQuery)) {
                        AlertMaker.showSimpleAlert("Success", "Book added successfully: " + title + " by " + author); // Use AlertMaker
                    } else {
                        AlertMaker.showErrorMessage("Database Error", "Failed to add the book to the database."); // Use AlertMaker
                    }
                }
            }
        } else {
            AlertMaker.showSimpleAlert("Selection Error", "No book selected."); // Use AlertMaker
        }
    }

    // Helper methods to get book details remain unchanged
    private String getBookTitle(JsonObject bookJson) {
        return bookJson.has("title") ? bookJson.get("title").getAsString() : "No Title Found";
    }

    private String getBookISBN(JsonObject volumeInfo) {
        if (volumeInfo.has("industryIdentifiers")) {
            JsonArray identifiers = volumeInfo.getAsJsonArray("industryIdentifiers");
            for (JsonElement idElement : identifiers) {
                if (idElement.isJsonObject()) {
                    JsonObject identifier = idElement.getAsJsonObject();
                    if (identifier.has("type") && "ISBN_13".equals(identifier.get("type").getAsString())) {
                        return identifier.get("identifier").getAsString();
                    }
                }
            }
        }
        return "No ISBN Found";
    }

    private String getBookAuthor(JsonObject bookJson) {
        JsonArray authors = bookJson.has("authors") ? bookJson.getAsJsonArray("authors") : null;
        return (authors != null && authors.size() > 0) ? authors.get(0).getAsString() : "No Author Found";
    }

    private String getBookPublisher(JsonObject bookJson) {
        return bookJson.has("publisher") ? bookJson.get("publisher").getAsString() : "No Publisher Found";
    }

    private String getBookGenre(JsonObject bookJson) {
        JsonArray categories = bookJson.has("categories") ? bookJson.getAsJsonArray("categories") : null;
        return (categories != null && categories.size() > 0) ? categories.get(0).getAsString() : "No Genre Found"; // Get first genre if available
    }
}
