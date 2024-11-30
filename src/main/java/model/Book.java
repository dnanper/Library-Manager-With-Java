package model;

import javafx.beans.property.SimpleStringProperty;

public class Book extends Document {
    private String publisher;
    private String url;
    private String urlCoverImage;
    private String description;

//    public Book() {
//        super();
//        this.publisher = "";
//        this.url = "";
//        this.urlCoverImage = "";
//        this.description = "";
//    }

    public Book(String title, String author, String id, String gerne, String publisher, Boolean isAvail, String url,  String urlCoverImage, String description) {
        super(title, author, id, gerne, isAvail);
        this.publisher = publisher;
        this.url = url;
        this.urlCoverImage = urlCoverImage;
        this.description = description;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getUrlCoverImage() {
        return urlCoverImage;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public static String getColumnName(String fieldName) {
        switch (fieldName) {
            case "title":
                return "title";
            case "author":
                return "author";
            case "id":
                return "id";
            case "publisher":
                return "publisher";
            case "genre":
                return "genre";
            case "availability":
                return "isAvail";
            case "url":
                return "url";
            case "urlCoverImage":
                return "urlCoverImage";
            case "description":
                return "description";
            default:
                throw new IllegalArgumentException("Unknown field: " + fieldName);
        }
    }

    @Override
    public void displayInfo() {
        System.out.println("Book: " + getTitle() + " by " + getAuthor() + ", Publisher: " + publisher  + ", URL: " + url);
    }
}