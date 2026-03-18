package com.example.mailclient;

import com.example.mailclient.controller.ClientController;
import com.example.mailclient.utils.EmailValidator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Optional;

public class MailClient extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        String loggedEmail = "";
        boolean loggedIn = false;

        while (!loggedIn) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.getEditor().setPromptText("yourname@gmail.com");
            dialog.setTitle("Login Mail Client");
            dialog.setHeaderText("Welcome to the Mail system");
            dialog.setContentText("Insert your email address:");

            Optional<String> result = dialog.showAndWait();
            if (result.isEmpty()) {
                System.exit(0);
            }

            String emailInput = result.get().trim();

            if (!EmailValidator.checkEmail(emailInput)) {
                showError("Format Error", "Email not valid!");
                continue;
            }

            try {
                if (verifyUserOnServer(emailInput)) {
                    loggedEmail = emailInput;
                    loggedIn = true;
                } else {

                    showError("Access Denied", "User not found.");
                }
            } catch (IOException e) {
                showError("Server Offline", "Unable to connect. Be sure that the Server is online!");
            }
        }


        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("view/client-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 600);

        ClientController controller = fxmlLoader.getController();
        controller.initUser(loggedEmail);

        stage.setTitle("📧 Client Mail - " + loggedEmail);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Tenta la connessione al server.
     * @throws IOException se il server è irraggiungibile
     */
    private boolean verifyUserOnServer(String email) throws IOException {
        try (Socket socket = new Socket("localhost", 8080);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println("CONNECT|" + email);
            String response = in.readLine();

            return response != null && response.startsWith("OK");
        }
    }

    /**
     * Mostra un popup di errore grafico
     */
    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Error");
        alert.setHeaderText(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }
}