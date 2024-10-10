module controller {
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.controls;

    requires com.dlsc.formsfx;
    requires com.jfoenix;
    requires java.sql;
    requires java.desktop;
    //  requires derbyclient;
    requires derby;
    requires com.google.gson;

    opens ui.settings to javafx.fxml;
    opens ui.listmember to javafx.fxml;
    opens ui.addbook to javafx.fxml;
    opens ui.addmember to javafx.fxml;
    opens ui.listbook to javafx.fxml;
    opens ui.main to javafx.fxml;

    exports ui.settings;
    exports ui.main;
    exports ui.listmember;
    exports ui.addbook;
    exports ui.addmember;
    exports ui.listbook;
}