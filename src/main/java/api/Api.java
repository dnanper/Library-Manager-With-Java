package api;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.BitSet;

public class Api {
    // Base URL for Google Books API
    private static final String GOOGLE_BOOKS_API_URL = "https://www.googleapis.com/books/v1/volumes?q=";
    private final String apiKey = "AIzaSyAp6Bgoq3o06qefC_qQEP8I_yDSPhjy8lk"; // Replace with your actual API key
    private Gson gson = new Gson();

    // Search for books by ISBN
    public JsonObject getBookByISBN(String isbn) {
        String urlString = GOOGLE_BOOKS_API_URL + "isbn:" + isbn + "&key=" + apiKey;
        String jsonResponse = sendGetRequest(urlString);
        return gson.fromJson(jsonResponse, JsonObject.class);
    }

    public JsonObject getBookByTitle(String title) {
        try {
            String encodedTitle = URLEncoder.encode(title, "UTF-8");
            String urlString = GOOGLE_BOOKS_API_URL + "intitle:" + encodedTitle + "&key=" + apiKey;
            String jsonResponse = sendGetRequest(urlString);
            return gson.fromJson(jsonResponse, JsonObject.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getBookCoverImageUrl(JsonObject bookData) {
        try {
            JsonObject volumeInfo = bookData.getAsJsonObject("volumeInfo");
            if (volumeInfo != null) {
                JsonObject imageLinks = volumeInfo.getAsJsonObject("imageLinks");
                if (imageLinks != null) {
                    JsonElement thumbnail = imageLinks.get("thumbnail");
                    if (thumbnail != null) {
                        return thumbnail.getAsString();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public BufferedImage getQRCode(String bookUrl) {
        int size = 200;
        int qrCodeSize = 21;
        int scale = size / qrCodeSize;


        BitSet qrMatrix = encodeToQRMatrix(bookUrl, qrCodeSize);


        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();


        g.setColor(Color.WHITE);
        g.fillRect(0, 0, size, size);
        g.setColor(Color.BLACK);

        for (int y = 0; y < qrCodeSize; y++) {
            for (int x = 0; x < qrCodeSize; x++) {
                if (qrMatrix.get(y * qrCodeSize + x)) {
                    g.fillRect(x * scale, y * scale, scale, scale);
                }
            }
        }

        g.dispose();
        return image;
    }


    private BitSet encodeToQRMatrix(String data, int size) {
        BitSet matrix = new BitSet(size * size);

        // Fake encoding (just alternating for simplicity)
        // You would need to implement a full QR encoding algorithm here
        for (int i = 0; i < size * size; i++) {
            matrix.set(i, (i % 2 == 0));
        }

        return matrix;
    }

    private String sendGetRequest(String urlString) {
        StringBuilder result = new StringBuilder();
        HttpURLConnection conn = null;

        try {
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read the response
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                }
            } else {
                System.out.println("Error: HTTP response code " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect(); // Clean up connection
            }
        }
        System.out.println("Response: " + result.toString()); // Print the response
        return result.toString();
    }
}
