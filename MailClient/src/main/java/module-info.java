/**
 * @brief Module definition for the MailClient application.
 */
module com.example.mailclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires java.desktop;

    opens com.example.mailclient.model to com.google.gson;
    opens com.example.mailclient to javafx.graphics, javafx.fxml;
    opens com.example.mailclient.controller to javafx.fxml;

    exports com.example.mailclient;
}