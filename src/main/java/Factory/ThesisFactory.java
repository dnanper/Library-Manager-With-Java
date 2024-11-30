package Factory;

import model.Document;
import model.Thesis;

public class ThesisFactory extends DocumentFactory {
    @Override
    public Document createDocument(String type, String title, String author, String id, String gerne, Boolean isAvail, String... additionalInfo) {
        if ("Thesis".equals(type)) {
            return new Thesis(title, author, id, gerne, additionalInfo[0], additionalInfo[1], isAvail);
        }
        return null;
    }
}
