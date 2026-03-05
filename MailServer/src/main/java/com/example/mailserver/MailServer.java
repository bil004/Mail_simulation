package com.example.mailserver;

import com.example.mailserver.controller.ServerController;
import com.example.mailserver.model.Email;
import com.example.mailserver.model.PersistenceManager;
import com.example.mailserver.network.ClientHandler;
import javafx.application.Application;
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
        // --- PARTE 1: AVVIO GUI ---
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/mailserver/view/server-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 650, 450);

        // Recuperiamo il controller della tabella
        this.controller = fxmlLoader.getController();

        stage.setTitle("📧 Mail Server Monitor");
        stage.setScene(scene);
        stage.show();

        // --- PARTE 2: INIZIALIZZAZIONE LOGICA ---
        this.pm = new PersistenceManager();
        this.threadPool = Executors.newCachedThreadPool();
        initializeAccounts();

        // --- PARTE 3: AVVIO SERVER SOCKET IN BACKGROUND ---
        // Usiamo un thread separato per non "congelare" la finestra
        new Thread(this::runSocketServer).start();

        controller.addLog("SYSTEM", "GUI Server ready: waiting on port " + port + "...");
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

                javafx.application.Platform.runLater(() ->
                        controller.addLog("NETWORK", "Connection accepted from: " + clientSocket.getInetAddress())
                );

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