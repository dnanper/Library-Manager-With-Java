package Factory;

import model.Book;
import model.Document;
import model.Paper;
import model.Thesis;

public class DocumentFactory {

    public Document createDocument(String type, String title, String author, String id, String gerne, Boolean isAvail, String... additionalInfo) {
        switch (type) {
            case "Book":
                if (additionalInfo.length >= 4) {
                    return new Book(title, author, id, gerne, additionalInfo[0], isAvail, additionalInfo[1], additionalInfo[2], additionalInfo[3]);
                }
                break;
            case "Thesis":
                if (additionalInfo.length >= 2) {
                    return new Thesis(title, author, id, gerne, additionalInfo[0], additionalInfo[1], isAvail);
                }
                break;
            case "Paper":
                if (additionalInfo.length >= 2) {
                    return new Paper(title, author, id, gerne, additionalInfo[0], additionalInfo[1], isAvail);
                }
                break;
            default:
                System.err.println("Unknown document type: " + type);
        }
        return null;
    }
}