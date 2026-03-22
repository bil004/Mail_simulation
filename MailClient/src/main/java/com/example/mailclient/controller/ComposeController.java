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

/**
 * @brief Controller for the email composition window.
 * @details Handles sending new emails and replying to existing ones.
 */
public class ComposeController {
    @FXML
    private TextField txtTo;
    @FXML
    private TextField txtSubject;
    @FXML
    private TextArea txtMessage;

    private String senderEmail;
    private Gson gson = new Gson();

    /**
     * Sets the sender's email address.
     * @param senderEmail The email address of the sender.
     */
    public void setSender(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    /**
     * Handles the "Send" button click.
     * Validates the input fields and sends the email.
     */
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

    /**
     * Sends the email to the server.
     * @param email The email to send.
     */
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
                    } else showError("Server Error", response != null ? response.split("\\|")[1] : "Undefined Error on sending email.");
                });

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    /**
     * Sets up the compose window for a reply.
     * @param originalEmail The original email to reply to.
     * @param myEmail The email of the user replying.
     * @param all true for the Reply-All option, false for a single user Reply.
     */
    public void setupReply(Email originalEmail, String myEmail, boolean all) {
        this.senderEmail = myEmail;

        if (all) {
            List<String> recipients = new java.util.ArrayList<>();
            recipients.add(originalEmail.getSender());
            recipients.addAll(originalEmail.getReceivers());
            recipients.remove(myEmail);
            txtTo.setText(String.join(", ", recipients));
        } else {
            txtTo.setText(originalEmail.getSender());
        }

        String sub = originalEmail.getSubject();
        txtSubject.setText(sub.toLowerCase().startsWith("re:") ? sub : "Re: " + sub);
        txtMessage.setText("\n\n--- Replying to " + originalEmail.getSender() + " ---\n" + originalEmail.getMessage());
    }

    /**
     * Sets up the compose window for forwarding an email.
     * @param originalEmail The original email to forward.
     * @param myEmail The email of the user forwarding the message.
     */
    public void setupForward(Email originalEmail, String myEmail) {
        this.senderEmail = myEmail;
        txtTo.setText("");

        String sub = originalEmail.getSubject();
        txtSubject.setText(sub.toLowerCase().startsWith("fwd:") ? sub : "Fwd: " + sub);
        txtMessage.setText("\n\n--- Forwarded message ---\nFrom: " + originalEmail.getSender() + "\n" + originalEmail.getMessage());
    }

    /**
     * @brief Handles the "Cancel" button click.
     * Closes the compose window.
     */
    @FXML
    public void onCancelButton() {
        closeWindow();
    }

    /**
     * @brief Closes the current window.
     */
    private void closeWindow() {
        Stage stage = (Stage) txtTo.getScene().getWindow();
        stage.close();
    }

    /**
     * Shows a graphical error popup.
     * @param title The title of the error dialog.
     * @param content The content message of the error dialog.
     */
    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Shows a graphical information popup.
     * @param title The title of the info dialog.
     * @param content The content message of the info dialog.
     */
    private void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * @brief Checks if the server is reachable.
     * @return true if the server is reachable, false otherwise.
     */
    private boolean isServerReachable() {
        try (Socket s = new Socket()) {
            s.connect(new InetSocketAddress("localhost", 8080), 500);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}