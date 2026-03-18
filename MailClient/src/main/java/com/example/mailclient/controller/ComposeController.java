package com.example.mailclient.controller;

import com.example.mailclient.model.Email;
import com.example.mailclient.utils.EmailValidator;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ComposeController {
    @FXML
    private TextField txtTo;
    @FXML
    private TextField txtSubject;
    @FXML
    private TextArea txtMessage;

    private String senderEmail;
    private Gson gson = new Gson();

    public void setSender(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    @FXML
    public void onSendClick() {
        if (!isServerReachable()) {
            showError("Connection Lost", "Server offline! Be sure that the server is reachable!");
            return;
        }

        String to = txtTo.getText().trim();
        String subject = txtSubject.getText().trim();
        String message = txtMessage.getText().trim();

        if (to.isEmpty() || subject.isEmpty() || message.isEmpty()) {
            showError("Fields Empty", "Please fill all the fields before sending.");
            return;
        }

        // CHEDI
        List<String> receivers = Arrays.stream(to.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        for (String email : receivers) {
            if(!EmailValidator.checkEmail(email)) {
                showError("Invalid Email",email + " not found. " + "Please enter a valid email address.");
                return;
            }
        }

        Email emailToSend = new Email(
                System.currentTimeMillis(),
                senderEmail,
                receivers,
                subject,
                message,
                new Date()
        );

        sendEmailToReceiver(emailToSend);
    }

    public void sendEmailToReceiver(Email email) {
        new Thread(() -> {
            try (Socket s = new Socket("localhost", 8080);
                 PrintWriter out = new PrintWriter(s.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream())))
            {
                String jsonEmail = gson.toJson(email);
                out.println("SEND|" + jsonEmail);

                String response = in.readLine();

                Platform.runLater(() -> {
                    if (response != null && response.startsWith("OK")) {
                        showInfo("Success", "Email sent successfully to the receiver.");
                        closeWindow();
                    } else showError("Errore Server", response != null ? response.split("\\|")[1] : "Errore ignoto");
                });

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void setupReply(Email originalEmail, String myEmail) {
        this.senderEmail = myEmail;
        txtTo.setText(originalEmail.getSender());

        if (!originalEmail.getSubject().toLowerCase().startsWith("re:")) {
            txtSubject.setText("Re: " + originalEmail.getSubject());
        } else {
            txtSubject.setText(originalEmail.getSubject());
        }

        txtMessage.setText("\n\n" + "--- Response to " + originalEmail.getSender() + " ---" + "\n" + originalEmail.getMessage());
        txtMessage.requestFocus();
        txtMessage.selectRange(0, 0);
    }

    @FXML
    public void onCancelButton() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) txtTo.getScene().getWindow();
        stage.close();
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private boolean isServerReachable() {
        try (Socket s = new Socket()) {
            s.connect(new InetSocketAddress("localhost", 8080), 500); // Timeout 500ms
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}