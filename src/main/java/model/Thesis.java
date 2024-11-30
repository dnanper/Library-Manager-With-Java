package model;

public class Thesis extends Document {
    private String university;
    private String department;

    public Thesis(String title, String author, String id, String gerne, String university, String department, Boolean isAvail) {
        super(title, author, id, gerne, isAvail);
        this.university = university;
        this.department = department;
    }

    public String getUniversity() {
        return university;
    }

    public String getDepartment() {
        return department;
    }

    @Override
    public void displayInfo() {
        System.out.println("Thesis: " + getTitle() + " by " + getAuthor() + ", University: " + university + ", Department: " + department);
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
            case "university":
                return "university";
            case "department":
                return "department";
            case "availability":
                return "isAvail";
            default:
                throw new IllegalArgumentException("Unknown field: " + fieldName);
        }
    }
}