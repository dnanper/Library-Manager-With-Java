package util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ui.main.MainController;
import ui.settings.Preferences;
import ui.theme.ThemeManager;

import java.io.IOException;
import java.net.URL;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

// set icon for app
public class LibraryUtil {
    public static final String IMAGE_LOC = "/library.png";
    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");

    public static void setStageIcon(Stage stage) {
        var img = new Image(LibraryUtil.class.getResource(IMAGE_LOC).toString());
        stage.getIcons().add(img);
    }

    // function to load window of each method
    public static void loadWindow(URL loc, String title, Stage parStage) {
        try {
            // for each of FXML file, parent will be different
            Parent parent = FXMLLoader.load(loc);
            Stage stage = null;
            if (parStage != null) {
                stage = parStage;
            } else {
                stage = new Stage(StageStyle.DECORATED);
            }
            stage.setTitle(title);
            // Create scene from FXML file that stored in parent
            stage.setScene(new Scene(parent));
            ThemeManager.setTheme(stage.getScene());
            stage.show();
            setStageIcon(stage);

        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Float getFineAmount(int totalDays) {
        Preferences pref = Preferences.getPreferences();
        Integer fineDays = totalDays - pref.getnDaysWithoutFine();
        Float fine = 0f;
        if (fineDays > 0) {
            fine = fineDays * pref.getFinePerDay();
        }
        return fine;
    }

    public static String formatDateTimeString(Date date) {
        return DATE_TIME_FORMAT.format(date);
    }

    public static String formatDateTimeString(Long time) {
        return DATE_TIME_FORMAT.format(new Date(time));
    }
}
