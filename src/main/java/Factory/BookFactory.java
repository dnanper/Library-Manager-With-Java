package Factory;

import model.Book;
import model.Document;

public class BookFactory extends DocumentFactory {
    @Override
    public Document createDocument(String type, String title, String author, String id, String gerne, Boolean isAvail, String... additionalInfo) {
        if ("Book".equals(type)) {
            return new Book(title, author, id, gerne, additionalInfo[0], isAvail, additionalInfo[1], additionalInfo[2], additionalInfo[3]);
        }
        return null;
    }
}