package Factory;
import model.Document;

public abstract class DocumentFactory {
    public abstract Document createDocument(String type, String title, String author, String id, String gerne, Boolean isAvail, String... additionalInfo);
}