module org.msolutions {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml;
    requires javafx.media;
    requires org.json;
    requires jintellitype;

    opens org.msolutions to javafx.fxml;
    exports org.msolutions;
    exports org.msolutions.controllers;
    opens org.msolutions.controllers to javafx.fxml;
}