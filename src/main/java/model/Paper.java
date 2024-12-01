package model;

public class Paper extends Document {
    private String conference;
    private String year;

    public Paper(String title, String author, String id, String gerne, String conference, String year, Boolean isAvail) {
        super(title, author, id, gerne, isAvail);
        this.conference = conference;
        this.year = year;
    }

    public String getConference() {
        return conference;
    }

    public String getYear() {
        return year;
    }

    public static String getColumnName(String fieldName) {
        switch (fieldName) {
            case "title":
                return "title";
            case "author":
                return "author";
            case "id":
                return "id";
            case "genre":
                return "genre";
            case "conference":
                return "conference";
            case "year":
                return "release_year";
            case "availability":
                return "isAvail";
            default:
                throw new IllegalArgumentException("Unknown field: " + fieldName);
        }
    }

    @Override
    public void displayInfo() {
        System.out.println("Paper: " + getTitle() + " by " + getAuthor() + ", Conference: " + conference + ", Year: " + year);
    }
}