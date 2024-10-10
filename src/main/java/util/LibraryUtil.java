package util;

import javafx.scene.image.Image;
import javafx.stage.Stage;

// set icon for app
public class LibraryUtil {
    private static final String IMAGE_LOC = "/library.png";

    public static void setStageIcon(Stage stage) {
        var img = new Image(LibraryUtil.class.getResource(IMAGE_LOC).toString());
        stage.getIcons().add(img);
    }
}
