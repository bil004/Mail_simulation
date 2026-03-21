/**
 * @class MailServer
 * @brief Main class for the mail server application.
 *
 * Extends `javafx.application.Application` to create and manage the user interface.
 * It starts a socket server in a separate thread to accept client connections
 * and manages a thread pool to serve multiple clients concurrently.
 */
package com.example.mailserver;

import com.example.mailserver.controller.ServerController;
import com.example.mailserver.model.Email;
import com.example.mailserver.model.PersistenceManager;
import com.example.mailserver.network.MultiThreadedServer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.List;


public class MailServer extends Application {
    /**
     * @brief The port on which the socket server listens for incoming connections.
     */
    private final int port = 8080;
    /**
     * @brief The multithreaded server instance.
     */
    private MultiThreadedServer server;
    /**
     * @brief Persistence manager for loading and saving data.
     */
    private PersistenceManager pm;
    /**
     * @brief Controller for the FXML user interface.
     */
    private ServerController controller;

    /**
     * @brief List of registered users in the system.
     */
    private final List<String> registeredUsers = List.of(
            "giorgio@gmail.com",
            "anna@gmail.com",
            "marco@gmail.com"
    );

    /**
     * @brief The startup method for the JavaFX application.
     *
     * Initializes the user interface, business logic, and starts the socket server.
     * @param stage The primary stage for this application.
     * @throws Exception if an error occurs while loading the FXML.
     */
    @Override
    public void start(Stage stage) throws Exception {
        // --- PART 1: LOADING GUI ---
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/mailserver/view/server-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 650, 450);

        // Getting table controller
        controller = fxmlLoader.getController();

        stage.setTitle("📧 Mail Server Monitor");
        stage.setScene(scene);
        stage.show();

        // --- PART 2: LOGIC INITIALIZATION ---
        pm = new PersistenceManager();
        initializeAccounts();

        // --- PART 3: START THE SOCKET SERVER ---
        server = new MultiThreadedServer(port, controller, pm, registeredUsers);
        server.start();

        stage.setOnCloseRequest(event -> {
            server.stop();
            Platform.exit();
            System.exit(0);
        });
    }

    /**
     * @brief Initializes user accounts by loading their inboxes.
     *
     * Iterates through the list of registered users, loads their inbox,
     * and saves it again to ensure data consistency.
     */
    private void initializeAccounts() {
        for (String email : registeredUsers) {
            List<Email> inbox = pm.loadInbox(email);
            pm.saveInbox(email, inbox);
        }

        if (controller != null)
            controller.addLog("STORAGE", "Account loaded successfully!");
    }

    /**
     * @brief The main method to launch the application.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        launch();
    }
}
