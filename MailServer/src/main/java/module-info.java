module com.example.mailserver {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    // Permette a JavaFX di avviare l'applicazione
    opens com.example.mailserver to javafx.graphics, javafx.fxml;

    // FONDAMENTALE: Permette a FXML di leggere e istanziare i tuoi Controller
    opens com.example.mailserver.controller to javafx.fxml;

    // Permette a Gson e alla TableView di leggere i dati dei Log
    opens com.example.mailserver.model to com.google.gson, javafx.base;

    exports com.example.mailserver;
}