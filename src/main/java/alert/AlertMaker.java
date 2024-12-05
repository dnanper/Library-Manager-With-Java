package alert;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.events.JFXDialogEvent;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.image.BufferedImage;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Objects;

import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import util.LibraryUtil;

import javax.imageio.ImageIO;

public class AlertMaker {

    /**
     * Displays simple information alert dialog.
     *
     * @param title   The title of the alert dialog, used to describe the topic of the alert.
     * @param content The content text of the alert dialog, showing the specific alert information.
     */
    public static void showSimpleAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        styleAlert(alert);
        alert.showAndWait();
    }

    /**
     * Displays an error message alert dialog with a fixed title "Error" and allows setting the specific header text and content.
     *
     * @param title   The header text of the alert dialog, used to further explain the error-related information.
     * @param content The content text of the alert dialog, showing the specific error alert information.
     */
    public static void showErrorMessage(String title, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(title);
        alert.setContentText(content);
        styleAlert(alert);
        alert.showAndWait();
    }

    /**
     * Displays an error alert dialog that contains the detailed stack trace information of an exception. The title is fixed as "Error occurred" and the header text is "Error Occurred".
     *
     * @param ex The exception object passed in, which is used to extract the localized message of the exception and the stack trace information to be displayed in the alert dialog.
     */
    public static void showErrorMessage(Exception ex) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error occurred");
        alert.setHeaderText("Error Occurred");
        alert.setContentText(ex.getLocalizedMessage());

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        alert.getDialogPane().setExpandableContent(expContent);

        styleAlert(alert);
        alert.showAndWait();
    }

    /**
     * Displays an error alert dialog that contains the detailed stack trace information of an exception. The title is fixed as "Error occurred".
     *
     * @param ex     The exception object passed in, which is used to extract the stack trace information to be displayed in the alert dialog.
     * @param title  The header text of the alert dialog, used to further explain the error-related information.
     * @param content The content text of the alert dialog, showing the specific error alert information.
     */
    public static void showErrorMessage(Exception ex, String title, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error occurred");
        alert.setHeaderText(title);
        alert.setContentText(content);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        alert.getDialogPane().setExpandableContent(expContent);
        alert.showAndWait();
    }

    /**
     * Displays a material design style dialog.
     *
     * @param root            The StackPane which serves as the root container for the dialog.
     * @param nodeToBeBlurred The Node that will have a blurring effect applied when the dialog is shown.
     * @param controls        The list of JFXButton controls to be added to the dialog. If the list is empty, a default "Okay" button will be added.
     * @param header          The header text of the dialog, usually used to describe the main topic of the dialog.
     * @param body            The body text of the dialog, showing the detailed content of the dialog.
     */
    public static void showMaterialDialog(StackPane root, Node nodeToBeBlurred, List<JFXButton> controls, String header, String body) {
        BoxBlur blur = new BoxBlur(3, 3, 3);
        if (controls.isEmpty()) {
            controls.add(new JFXButton("Okay"));
        }
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        JFXDialog dialog = new JFXDialog(root, dialogLayout, JFXDialog.DialogTransition.TOP);

        controls.forEach(controlButton -> {
            controlButton.getStyleClass().add("dialog-button");
            controlButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent mouseEvent) -> {
                dialog.close();
            });
        });

        dialogLayout.setHeading(new Label(header));
        dialogLayout.setBody(new Label(body));
        dialogLayout.setActions(controls);
        dialog.show();
        dialog.setOnDialogClosed((JFXDialogEvent event1) -> {
            nodeToBeBlurred.setEffect(null);
        });
        nodeToBeBlurred.setEffect(blur);
    }

    /**
     * Displays a message in the system tray.
     *
     * @param title   The title of the tray message, used to briefly describe the message content.
     * @param message The actual message content to be displayed in the system tray.
     */
    public static void showTrayMessage(String title, String message) {
        try {
            SystemTray tray = SystemTray.getSystemTray();
            BufferedImage image = ImageIO.read(Objects.requireNonNull(AlertMaker.class.getResource(LibraryUtil.IMAGE_LOC)));
            TrayIcon trayIcon = new TrayIcon(image, "Library Assistant");
            trayIcon.setImageAutoSize(true);
            trayIcon.setToolTip("Library Assistant");
            tray.add(trayIcon);
            trayIcon.displayMessage(title, message, MessageType.INFO);
            tray.remove(trayIcon);
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    /**
     * Styles the given alert dialog with custom stylesheets and sets the stage icon.
     *
     * @param alert The alert dialog to be styled.
     */
    private static void styleAlert(Alert alert) {
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        LibraryUtil.setStageIcon(stage);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(Objects.requireNonNull(AlertMaker.class.getResource("/darkmode.css")).toExternalForm());
        dialogPane.getStyleClass().add("custom-alert");
    }
}
