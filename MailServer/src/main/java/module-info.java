module com.example.mailserver {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    opens com.example.mailserver.model to com.google.gson, javafx.base;
    opens com.example.mailserver to javafx.graphics, javafx.fxml;
    opens com.example.mailserver.controller to javafx.fxml;

    exports com.example.mailserver;
}