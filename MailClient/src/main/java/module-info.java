module com.example.mailclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    opens com.example.mailclient.model to com.google.gson;

    opens com.example.mailclient to javafx.fxml;
    exports com.example.mailclient;
}