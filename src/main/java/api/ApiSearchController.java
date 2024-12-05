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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ApiSearchController {

    private final Api apiHandle = new Api();
    private final DataBaseHandler dataBaseHandler = DataBaseHandler.getInstance();

    @FXML
    private TextField nameField;

    @FXML
    private ImageView bookCoverImageView;

    @FXML
    private ImageView qrCodeImageView;

    @FXML
    private ListView<String> searchResultsList;

    private ObservableList<JsonObject> searchResults = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        searchResultsList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        searchResultsList.setOnMouseClicked((MouseEvent event) -> {
            int selectedIndex = searchResultsList.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0 && selectedIndex < searchResults.size()) {
                JsonObject selectedBook = searchResults.get(selectedIndex);

                String bookCoverUrl = getBookCoverImageUrl(selectedBook);
                String bookUrl = getBookUrl(selectedBook);

                if (bookCoverUrl != null && !bookCoverUrl.isEmpty()) {
                    Image coverImage = new Image(bookCoverUrl);
                    bookCoverImageView.setImage(coverImage);
                }

                BufferedImage qrCodeImage = getQRCode(bookUrl);
                if (qrCodeImage != null) {
                    Image qrImage = convertToJavaFXImage(qrCodeImage);
                    qrCodeImageView.setImage(qrImage);
                }
            }
        });
    }


    /**
     * Converts a `BufferedImage` (from the Java AWT library) to a JavaFX `Image` object.
     * This is useful for displaying images in JavaFX applications that were created or manipulated using AWT.
     *
     * @param bufferedImage The `BufferedImage` to be converted.
     * @return A JavaFX `Image` object if the conversion is successful. In case of an error during the conversion process,
     *         it prints the stack trace and returns `null`.
     */
    public Image convertToJavaFXImage(BufferedImage bufferedImage) {
        try {

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
            byte[] imageData = byteArrayOutputStream.toByteArray();

            InputStream inputStream = new java.io.ByteArrayInputStream(imageData);
            return new Image(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves the URL of the book cover image from the provided book JSON object.
     * It first tries to access the `imageLinks` object within the book JSON and then fetches the `thumbnail` URL.
     *
     * @param bookJson The JSON object representing the book's information.
     * @return The URL of the book cover image as a string if found. If any part of the process fails (e.g., missing `imageLinks`
     *         or `thumbnail`), it prints the stack trace and returns `null`.
     */
    private String getBookCoverImageUrl(JsonObject bookJson) {
        try {

            JsonObject imageLinks = bookJson.getAsJsonObject("imageLinks");
            if (imageLinks != null) {
                JsonElement thumbnail = imageLinks.get("thumbnail");
                if (thumbnail != null) {
                    String url = thumbnail.getAsString();
                    System.out.println("Book cover URL: " + url);
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

    /**
     * Generates a QR code image as a `BufferedImage` for the given book URL.
     * It uses the ZXing library to encode the URL into a QR code with specified error correction level and dimensions.
     *
     * @param bookUrl The URL for which the QR code should be generated.
     * @return A `BufferedImage` representing the generated QR code if successful. In case of errors like encoding issues
     *         or other exceptions during the generation process, it prints the appropriate error message and stack trace,
     *         and returns `null`.
     */
    public BufferedImage getQRCode(String bookUrl) {
        if (bookUrl == null || bookUrl.trim().isEmpty()) {
            System.out.println("Error: The URL is null or empty.");
            return null;
        }

        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            BitMatrix bitMatrix = new MultiFormatWriter().encode(bookUrl, BarcodeFormat.QR_CODE, 200, 200, hints);

            BufferedImage image = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < 200; x++) {
                for (int y = 0; y < 200; y++) {
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


                            searchResults.add(volumeInfo);


                            String title = getBookTitle(volumeInfo);
                            String author = getBookAuthor(volumeInfo);
                            String publisher = getBookPublisher(volumeInfo);
                            String genre = getBookGenre(volumeInfo);


                            String displayString = title + " by " + author + " (Publisher: " + publisher + ", Genre: " + genre + ")";
                            searchResultsList.getItems().add(displayString);
                        }
                    } else {
                        AlertMaker.showSimpleAlert("Search Result", "No books found for: " + searchText);
                    }
                } else {
                    AlertMaker.showSimpleAlert("Input Error", "Please enter a search term.");
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

                    String bookID = getBookISBN(selectedBook);

                    if (isBookExists(bookID)) {
                        AlertMaker.showSimpleAlert("Duplicate Book", "This book already exists in the database.");
                        return;
                    }

                    String title = getBookTitle(selectedBook).replace("'", "''");
                    String author = getBookAuthor(selectedBook).replace("'", "''");
                    String publisher = getBookPublisher(selectedBook).replace("'", "''");
                    String genre = getBookGenre(selectedBook).replace("'", "''");
                    String bookUrl = getBookUrl(selectedBook).replace("'", "''");
                    String urlCoverImage = getBookCoverImageUrl(selectedBook).replace("'", "''");
                    String description = getBookDescription(selectedBook).replace("'", "''");

                    String insertQuery = "INSERT INTO BOOK (id, title, author, publisher, isAvail, genre, URL, urlCoverImage, description) VALUES (" +
                            "'" + bookID + "'," +
                            "'" + title + "'," +
                            "'" + author + "'," +
                            "'" + publisher + "'," +
                            "true," +
                            "'" + genre + "'," +
                            "'" + bookUrl + "'," +
                            "'" + urlCoverImage + "'," +
                            "'" + description + "'" +
                            ")";

                    if (dataBaseHandler.execAction(insertQuery)) {
                        AlertMaker.showSimpleAlert("Book added successfully", title + " has been added to library");
                    } else {
                        AlertMaker.showSimpleAlert("Book cannot be added", "fail to add book");
                    }
                }
            }
        } else {
            AlertMaker.showSimpleAlert("Selection Error", "No book selected.");
        }
    }

    /**
     * Checks if a book with the given ID already exists in the database.
     * It constructs and executes a SQL query to count the number of records with the provided book ID.
     *
     * @param bookID The ID of the book to check for existence in the database.
     * @return `true` if the book exists (i.e., the count of records with the given ID is greater than 0),
     *         `false` otherwise. In case of a SQL exception during the query execution, it prints the stack trace
     *         and returns `false`.
     */
    private boolean isBookExists(String bookID) {

        String checkQuery = "SELECT COUNT(*) FROM BOOK WHERE id = ?";
        try (PreparedStatement preparedStatement = dataBaseHandler.getConnection().prepareStatement(checkQuery)) {
            preparedStatement.setString(1, bookID);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String getBookTitle(JsonObject bookJson) {
        return bookJson.has("title") ? bookJson.get("title").getAsString() : "No Title Found";
    }

    /**
     * Retrieves the ISBN of the book from the provided book JSON object (specifically from the `volumeInfo` part).
     * It searches through the `industryIdentifiers` array to find the ISBN-13 identifier if available.
     *
     * @param volumeInfo The JSON object representing the `volumeInfo` part of the book's information.
     * @return The ISBN of the book as a string if found. If no ISBN-13 identifier is present in the `industryIdentifiers`
     *         array, it returns the string "No ISBN Found".
     */
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

    private String getBookUrl(JsonObject bookJson) {
        try {

            String url = (bookJson.has("canonicalVolumeLink")) ?
                    bookJson.get("canonicalVolumeLink").getAsString() : "";
            System.out.println("Book URL: " + url);
            return url;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getBookDescription(JsonObject bookJson) {
        if (bookJson.has("description")) {
            String description = bookJson.get("description").getAsString();


            String[] sentences = description.split("\\.");


            if (sentences.length > 0) {
                return sentences[0].trim() + (sentences[0].endsWith(".") ? "" : ".");
            }
        }
        return "No Description Available";
    }
}
