package com.example.mailserver;

import com.example.mailserver.controller.ServerController;
import com.example.mailserver.model.Email;
import com.example.mailserver.model.PersistenceManager;
import com.example.mailserver.network.ClientHandler;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MailServer extends Application {
    private final int port = 8080;
    private ExecutorService threadPool;
    private boolean isRunning = true;
    private PersistenceManager pm;
    private ServerController controller;

    private final List<String> registeredUsers = List.of(
            "giorgio@gmail.com",
            "anna@gmail.com",
            "marco@gmail.com"
    );

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
        threadPool = Executors.newCachedThreadPool();
        initializeAccounts();

        // --- PART 3: START THE SOCKET SERVER IN BACKGROUND ---
        new Thread(this::runSocketServer).start();

        controller.addLog("SYSTEM", "GUI Server ready: waiting on port " + port + "...");

        stage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });
    }

    private void initializeAccounts() {
        for (String email : registeredUsers) {
            List<Email> inbox = pm.loadInbox(email);
            pm.saveInbox(email, inbox);
        }

        if (controller != null)
            controller.addLog("STORAGE", "Account loaded successfully!");
    }

    private void runSocketServer() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (isRunning) {
                Socket clientSocket = serverSocket.accept();
                threadPool.execute(new ClientHandler(clientSocket, registeredUsers, pm, controller));
            }
        } catch (IOException e) {
            javafx.application.Platform.runLater(() ->
                    controller.addLog("ERROR", "FATAL ERROR: " + e.getMessage())
            );
        } finally {
            threadPool.shutdown();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}