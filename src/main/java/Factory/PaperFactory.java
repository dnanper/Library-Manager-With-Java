package Factory;

import model.Document;
import model.Paper;

public class PaperFactory extends DocumentFactory {
    @Override
    public Document createDocument(String type, String title, String author, String id, String gerne, Boolean isAvail, String... additionalInfo) {
        if ("Paper".equals(type)) {
            return new Paper(title, author, id, gerne, additionalInfo[0],additionalInfo[1], isAvail);
        }
        return null;
    }
}
