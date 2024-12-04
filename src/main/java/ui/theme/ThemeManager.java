package ui.theme;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;

public class ThemeManager {
    private static boolean isDarkMode = false;
    private static final String LIGHT_THEME = "/lightmode.css";
    private static final String DARK_THEME = "/darkmode.css";

    public static void toggleTheme() {
        isDarkMode = !isDarkMode;
    }

    public static void setTheme(Scene scene) {
        scene.getStylesheets().clear();
        String themePath = isDarkMode ? DARK_THEME : LIGHT_THEME;
        System.out.println(isDarkMode + themePath);
        URL themeUrl = ThemeManager.class.getResource(themePath);
        if (themeUrl != null) {
            scene.getStylesheets().add(themePath);
        } else {
            System.err.println("Không tìm thấy file CSS ở đường dẫn: " + themePath);
        }
    }

}
