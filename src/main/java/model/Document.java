package model;

public abstract class Document {
    private String title;
    private String author;
    private String id;
    private String gerne;
    private Boolean isAvail;

//    public Document() {
//        this.title = "";
//        this.author = "";
//        this.id = "";
//        this.gerne = "";
//        this.isAvail = true;
//    }

    public Document(String title, String author, String id, String gerne, Boolean isAvail) {
        this.title = title;
        this.author = author;
        this.id = id;
        this.gerne = gerne;
        this.isAvail = isAvail;
    }



    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setGerne(String gerne) {
        this.gerne = gerne;
    }

    public void setAvail(Boolean avail) {
        isAvail = avail;
    }

    public Boolean getAvailability() {
        return isAvail;
    }

    public String getGenre() {
        return gerne;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getId() {
        return id;
    }

    public abstract void displayInfo();
}