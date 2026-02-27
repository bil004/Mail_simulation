module com.example.mailserver {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.mailserver to javafx.fxml;
    exports com.example.mailserver;
}