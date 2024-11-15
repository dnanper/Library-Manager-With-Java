package api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import alert.AlertMaker;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import database.DataBaseHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import javafx.scene.input.MouseEvent;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ApiSearchController {

    private Api apiHandle = new Api();
    private DataBaseHandler dataBaseHandler = DataBaseHandler.getInstance();

    @FXML
    private TextField nameField;

    @FXML
    private ImageView bookCoverImageView; // For displaying the book cover

    @FXML
    private ImageView qrCodeImageView;

    @FXML
    private ListView<String> searchResultsList; // To display matching books

    private ObservableList<JsonObject> searchResults = FXCollections.observableArrayList(); // To hold search results

    @FXML
    private void initialize() {
        searchResultsList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE); // Ensure single selection

        searchResultsList.setOnMouseClicked((MouseEvent event) -> {
            int selectedIndex = searchResultsList.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0 && selectedIndex < searchResults.size()) {
                JsonObject selectedBook = searchResults.get(selectedIndex);

                // Get the book cover URL and QR code URL
                String bookCoverUrl = getBookCoverImageUrl(selectedBook);
                String bookUrl = getBookUrl(selectedBook);  // Assume this method returns a book URL

                // Set book cover image
                if (bookCoverUrl != null && !bookCoverUrl.isEmpty()) {
                    Image coverImage = new Image(bookCoverUrl);
                    bookCoverImageView.setImage(coverImage);
                }

                // Set QR code image
                BufferedImage qrCodeImage = getQRCode(bookUrl);  // Generate QR code image
                if (qrCodeImage != null) {
                    Image qrImage = convertToJavaFXImage(qrCodeImage);  // Convert BufferedImage to JavaFX Image
                    qrCodeImageView.setImage(qrImage);
                }
            }
        });
    }


    /**
     * Converts a BufferedImage to a JavaFX Image.
     */
    private Image convertToJavaFXImage(BufferedImage bufferedImage) {
        try {
            // Convert BufferedImage to ByteArrayOutputStream
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
            byte[] imageData = byteArrayOutputStream.toByteArray();

            // Convert byte array to InputStream
            InputStream inputStream = new java.io.ByteArrayInputStream(imageData);

            // Create a JavaFX Image from InputStream
            return new Image(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getBookCoverImageUrl(JsonObject bookJson) {
        try {
            // Directly access imageLinks since it's at the root level
            JsonObject imageLinks = bookJson.getAsJsonObject("imageLinks");
            if (imageLinks != null) {
                JsonElement thumbnail = imageLinks.get("thumbnail");
                if (thumbnail != null) {
                    String url = thumbnail.getAsString();
                    System.out.println("Book cover URL: " + url); // Debugging line
                    return url;
                } else {
                    System.out.println("Thumbnail not found in imageLinks.");
                }
            } else {
                System.out.println("imageLinks object is missing.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getBookUrl(JsonObject bookJson) {
        try {
            // Directly access canonicalVolumeLink since it's at the root level
            String url = (bookJson.has("canonicalVolumeLink")) ?
                    bookJson.get("canonicalVolumeLink").getAsString() : "";
            System.out.println("Book URL: " + url); // Debugging line
            return url;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public BufferedImage getQRCode(String bookUrl) {
        if (bookUrl == null || bookUrl.trim().isEmpty()) {
            // Handle invalid input (empty or null URL)
            System.out.println("Error: The URL is null or empty.");
            return null;
        }

        try {
            // Set up QR code encoding with error correction level and size
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L); // Low error correction

            // Generate QR code matrix
            BitMatrix bitMatrix = new MultiFormatWriter().encode(bookUrl, BarcodeFormat.QR_CODE, 200, 200, hints);

            // Convert BitMatrix to BufferedImage
            BufferedImage image = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < 200; x++) {
                for (int y = 0; y < 200; y++) {
                    // Set pixel to black or white based on the BitMatrix value
                    image.setRGB(x, y, bitMatrix.get(x, y) ? Color.BLACK.getRGB() : Color.WHITE.getRGB());
                }
            }

            return image;
        } catch (WriterException e) {
            System.out.println("Error generating QR code: " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
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
