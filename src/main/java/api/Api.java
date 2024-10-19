package api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Api {
    private static final String GOOGLE_BOOKS_API_URL = "https://www.googleapis.com/books/v1/volumes?q=";
    private Gson gson = new Gson();

    // Tìm kiếm sách bằng ISBN
    public JsonObject getBookByISBN(String isbn) {
        String urlString = GOOGLE_BOOKS_API_URL + "isbn:" + isbn;
        String jsonResponse = sendGetRequest(urlString);

        // Chuyển đổi JSON thành đối tượng JsonObject
        return gson.fromJson(jsonResponse, JsonObject.class);
    }

    // Tìm kiếm sách bằng tiêu đề
    public JsonObject getBookByTitle(String title) {
        try {
            String encodedTitle = java.net.URLEncoder.encode(title, "UTF-8");
            String urlString = GOOGLE_BOOKS_API_URL + "intitle:" + encodedTitle;
            String jsonResponse = sendGetRequest(urlString);

            // Chuyển đổi JSON thành đối tượng JsonObject
            return gson.fromJson(jsonResponse, JsonObject.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Trả về null nếu có lỗi
        }
    }

    // Gửi yêu cầu GET đến API Google Books
    private String sendGetRequest(String urlString) {
        StringBuilder result = new StringBuilder();
        try {
            java.net.URL url = new java.net.URL(urlString);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode == java.net.HttpURLConnection.HTTP_OK) {
                try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream()))) {
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
        }
        return result.toString();
    }
}