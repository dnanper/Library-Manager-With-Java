module controller {
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.controls;
    requires com.dlsc.formsfx;
    requires com.jfoenix;
    requires java.sql;
    requires java.desktop;
    //  requires derbyclient;
    requires org.apache.httpcomponents.httpclient;
    requires org.apache.httpcomponents.httpcore;
    requires commons.logging;
    requires derby;
    //requires com.google.gson;

    opens util to javafx.fxml;
    requires com.google.gson;
    requires org.apache.commons.codec;
    requires java.net.http;

    opens ui.login to javafx.fxml;
    opens ui.settings to javafx.fxml, com.google.gson;
    opens ui.listmember to javafx.fxml;
    opens ui.addbook to javafx.fxml, com.google.gson;
    opens ui.addmember to javafx.fxml;
    opens ui.listbook to javafx.fxml, com.google.gson;
    opens ui.main to javafx.fxml;
    opens ui.main.toolbar to javafx.fxml;
    opens api to org.apache.httpcomponents.httpclient, org.apache.httpcomponents.httpcore, commons.logging, javafx.fxml;


    exports util;
    //exports ui.settings;
    exports ui.main.toolbar;
    exports ui.main;
    exports ui.listmember;
    exports ui.addbook;
    exports ui.addmember;
    exports ui.listbook;
    exports api;
}