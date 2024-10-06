module controller {
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.controls;

    requires com.dlsc.formsfx;
    requires com.jfoenix;
    requires java.sql;

    opens controller to javafx.fxml;
    opens ui.addbook to javafx.fxml;
    exports ui.addbook;
    exports controller;
}