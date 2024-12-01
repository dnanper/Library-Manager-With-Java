package api;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

public class Api {
    private static final String GOOGLE_BOOKS_API_URL = "https://www.googleapis.com/books/v1/volumes?q=";
    private final String apiKey = "AIzaSyAp6Bgoq3o06qefC_qQEP8I_yDSPhjy8lk"; // API key
    private Gson gson = new Gson();

    // search for books by ISBN
    public JsonObject getBookByISBN(String isbn) {
        String urlString = GOOGLE_BOOKS_API_URL + "isbn:" + isbn + "&key=" + apiKey;
        String jsonResponse = sendGetRequest(urlString);
        return gson.fromJson(jsonResponse, JsonObject.class);
    }

    // by title
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



    private String sendGetRequest(String urlString) {
        StringBuilder result = new StringBuilder();
        HttpURLConnection conn = null;

        try {
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
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
                conn.disconnect();
            }
        }
        System.out.println("Response: " + result.toString());
        return result.toString();
    }
}
