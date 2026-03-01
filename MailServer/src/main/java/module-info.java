module com.example.mailserver {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    opens com.example.mailserver.model to com.google.gson;

    opens com.example.mailserver to javafx.fxml;
    exports com.example.mailserver;
}