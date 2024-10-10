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
    requires org.apache.commons.codec;

    opens ui.login to javafx.fxml;
    opens ui.settings to javafx.fxml, com.google.gson;
    opens ui.listmember to javafx.fxml;
    opens ui.addbook to javafx.fxml;
    opens ui.addmember to javafx.fxml;
    opens ui.listbook to javafx.fxml;
    opens ui.main to javafx.fxml;

    exports ui.main;
    exports ui.listmember;
    exports ui.addbook;
    exports ui.addmember;
    exports ui.listbook;
}