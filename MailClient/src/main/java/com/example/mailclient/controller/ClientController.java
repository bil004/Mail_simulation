package com.example.mailclient.controller;

import com.example.mailclient.model.Email;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.List;

/**
 * Controller for the main client view.
 * Handles user interactions and communication with the server.
 */
public class ClientController {
    @FXML private ListView<Email> emailListView;
    @FXML private TextArea messageBody;
    @FXML private Label lblFrom;
    @FXML private Label lblSubject;
    @FXML private Label lblDate;
    @FXML private Label statusLabel;
    @FXML private Circle serverStatusLed;
    @FXML private Button btnNewEmail;

    private final ObservableList<Email> emails = FXCollections.observableArrayList();
    private final Gson gson = new Gson();
    private String userEmail;
    private boolean offline = false;

    /**
     * Initializes the user's email and loads their emails from the server.
     * @param userEmail The email of the logged-in user.
     */
    public void initUser(String userEmail) {
        this.userEmail = userEmail;
        loadEmailsFromServer();
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    public void initialize() {
        emailListView.setItems(emails);

        emailListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Email item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getSender() + " - " + item.getSubject());
                }
            }
        });

        emailListView.getSelectionModel().selectedItemProperty().addListener((o, old, nv) -> displayEmail(nv));
        startServerCheck();
    }

    /**
     * Displays the content of the selected email.
     * @param email The email to display.
     */
    private void displayEmail(Email email) {
        if (email == null) return;
        lblFrom.setText(email.getSender());
        lblSubject.setText(email.getSubject());
        lblDate.setText(email.getDate().toString());
        messageBody.setText(email.getMessage());
    }

    /**
     * Updates the server status indicator.
     * @param online true if the server is online, false otherwise.
     */
    public void updateStatus(boolean online) {
        if (online) {
            serverStatusLed.setFill(Color.LIMEGREEN);
            statusLabel.setText("Server Online");
        } else {
            serverStatusLed.setFill(Color.RED);
            statusLabel.setText("Server Offline");
        }
    }

    /**
     * Handles the "New Email" button click.
     * Opens the compose window.
     */
    @FXML
    protected void onNewEmailButtonClick() {
        if (offline) {
            showError("Server Offline", "Server Offline! Be sure that the server is online and try again.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/mailclient/view/compose-view.fxml"));
            javafx.scene.Parent root = loader.load();

            ComposeController composeController = loader.getController();
            composeController.setSender(this.userEmail);

            Stage stage = new Stage();
            stage.setTitle("New Email - From: " + this.userEmail);
            stage.setScene(new javafx.scene.Scene(root));

            stage.show();

        } catch (IOException ex) {
            showError("Error Compose Window", "Unable to open compose window.");
            ex.printStackTrace();
        }
    }

    /**
     * Handles the "Delete" button click.
     * Deletes the selected email from the server.
     */
    @FXML
    protected void onDeleteButtonClick() {
        Email selected = emailListView.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showError("No selection", "Select an Email to delete.");
            return;
        }

        // Chiediamo conferma (buona pratica di UX)
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Mail");
        confirm.setHeaderText("You're going to delete this mail.");
        confirm.setContentText("Are you sure? (this action is IRREVERSIBLE.)");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            deleteEmailFromServer(selected);
        }
    }

    /**
     * Sends a request to the server to delete an email.
     * @param email The email to delete.
     */
    private void deleteEmailFromServer(Email email) {
        new Thread(() -> {
            try (Socket s = new Socket("localhost", 8080);
                 PrintWriter out = new PrintWriter(s.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()))) {

                out.println("DELETE|" + userEmail + "|" + email.getId());

                String response = in.readLine();

                Platform.runLater(() -> {
                    if (response != null && response.startsWith("OK")) {
                        emails.remove(email);
                        messageBody.clear();
                        lblFrom.setText("");
                        lblSubject.setText("");
                    } else {
                        showError("Delete Error!", "The server can't delete this mail.");
                    }
                });

            } catch (Exception e) {
                Platform.runLater(() -> showError("Connection Error", "Impossible contact the server for delete this mail."));
            }
        }).start();
    }

    /**
     * Handles the "Reply" button click.
     * Opens the compose window to reply to the selected email.
     */
    @FXML
    protected void onReplyButtonClick() {
        Email selected = emailListView.getSelectionModel().getSelectedItem();
        // Usiamo l'helper centralizzato passando la modalità "REPLY"
        openComposeWindow("Reply", selected, "REPLY");
    }

    /**
     * Handles the "Reply All" button click.
     * Opens the compose window to reply to all recipients of the selected email.
     */
    @FXML
    protected void onReplyAllButtonClick() {
        Email selected = emailListView.getSelectionModel().getSelectedItem();
        openComposeWindow("Reply All", selected, "REPLY_ALL");
    }

    /**
     * Handles the "Forward" button click.
     * Opens the compose window to forward the selected email.
     */
    @FXML
    protected void onForwardButtonClick() {
        Email selected = emailListView.getSelectionModel().getSelectedItem();
        openComposeWindow("Forward Message", selected, "FORWARD");
    }

    /**
     * Opens the compose window in a specific mode (Reply, Reply All, Forward).
     * @param title The title for the new window.
     * @param selected The email to act upon.
     * @param mode The mode of composition ("REPLY", "REPLY_ALL", "FORWARD").
     */
    private void openComposeWindow(String title, Email selected, String mode) {
        if (selected == null) {
            showError("No selection", "Please select an email first.");
            return;
        }
        if (offline) {
            showError("Server Offline", "This action is not available while offline.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/mailclient/view/compose-view.fxml"));
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(loader.load()));

            ComposeController controller = loader.getController();

            if (mode.equals("REPLY")) {
                controller.setupReply(selected, this.userEmail, false);
            } else if (mode.equals("REPLY_ALL")) {
                controller.setupReply(selected, this.userEmail, true);
            } else if (mode.equals("FORWARD")) {
                controller.setupForward(selected, this.userEmail);
            }

            stage.show();
        } catch (IOException e) {
            showError("Error", "Unable to open the composition window.");
        }
    }

    /**
     * Loads the user's emails from the server.
     */
    private void loadEmailsFromServer() {
        new Thread(() -> {
            try (Socket socket = new Socket("localhost", 8080);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                out.println("GET_ALL|" + userEmail);
                String jsonResponse = in.readLine();

                // Aggiorna il file JSON e la UI dell'app
                if (jsonResponse != null) {
                    Email[] receivedArray = gson.fromJson(jsonResponse, Email[].class);
                    List<Email> receivedList = List.of(receivedArray);

                    javafx.application.Platform.runLater(() -> {
                        emails.setAll(receivedList);
                        updateStatus(true);
                    });
                }
            } catch (Exception e) {
                System.err.println("[CLIENT LOG] Error to load email: " + e.getMessage());
                javafx.application.Platform.runLater(() -> updateStatus(false));
            }
        }).start();
    }

    /**
     * Starts a background thread to periodically check the server status.
     */
    private void startServerCheck() {
        Thread checkThread = new Thread(() -> {
            while (true) {
                try (Socket socket = new Socket("localhost", 8080)) {
                    if (offline) {
                        javafx.application.Platform.runLater(() -> {
                            updateStatus(true);
                            loadEmailsFromServer();
                        });
                        offline = false;
                    } else {
                        javafx.application.Platform.runLater(() -> updateStatus(true));

                        if (userEmail != null) {
                            checkForNewEmails();
                        }
                    }
                }
                catch (IOException e) {
                    offline = true;
                    javafx.application.Platform.runLater(() -> updateStatus(false));
                }

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) { break; }
            }
        });
        checkThread.setDaemon(true);
        checkThread.start();
    }

    /**
     * Checks for new emails on the server since the last check.
     * It retrieves the ID of the latest email present locally and asks the server for any subsequent emails.
     */
    private void checkForNewEmails() {
        new Thread(() -> {
            long lastId = emails.stream().mapToLong(Email::getId).max().orElse(0);

            try (Socket socket = new Socket("localhost", 8080);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                out.println("RECEIVE|" + userEmail + "|" + lastId);
                String jsonResponse = in.readLine();

                if (jsonResponse != null && !jsonResponse.equals("[]")) {
                    Email[] newArray = gson.fromJson(jsonResponse, Email[].class);

                    javafx.application.Platform.runLater(() -> {
                        for (Email e : newArray) {
                            if (emails.stream().noneMatch(existing -> existing.getId() == e.getId())) {
                                emails.add(0, e);
                            }
                        }
                    });
                }
            } catch (IOException e) {
                System.err.println("[DEBUG] Socket error: " + e.getMessage());
            }
        }).start();
    }

    /**
     * @brief Shows a graphical error popup.
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
}