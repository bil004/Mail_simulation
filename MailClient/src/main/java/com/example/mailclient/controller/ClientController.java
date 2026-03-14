package com.example.mailclient.controller;

import com.example.mailclient.model.Email;
import com.google.gson.Gson;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class ClientController {
    @FXML private ListView<Email> emailListView;
    @FXML private TextArea messageBody;
    @FXML private Label lblFrom;
    @FXML private Label lblSubject;
    @FXML private Label lblDate;
    @FXML private Label statusLabel;
    @FXML private Circle serverStatusLed;
    @FXML private Button btnNewEmail;

    // Lista "osservabile": se aggiungi una mail qui, la ListView si aggiorna da sola
    private final ObservableList<Email> emails = FXCollections.observableArrayList();
    private final Gson gson = new Gson();
    private String userEmail;
    private boolean offline = false;

    public void initUser(String userEmail) {
        this.userEmail = userEmail;
        loadEmailsFromServer();
    }

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

    private void displayEmail(Email email) {
        if (email == null) return;
        lblFrom.setText(email.getSender());
        lblSubject.setText(email.getSubject());
        lblDate.setText(email.getDate().toString());
        messageBody.setText(email.getMessage());
    }

    public void updateStatus(boolean online) {
        if (online) {
            serverStatusLed.setFill(Color.LIMEGREEN);
            statusLabel.setText("Server Online");
        } else {
            serverStatusLed.setFill(Color.RED);
            statusLabel.setText("Server Offline");
        }
    }

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

    @FXML
    protected void onDeleteButtonClick() {
        Email selected = emailListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            emails.remove(selected);
            System.out.println("DEBUG: Email eliminata: " + selected.getSubject());
        }
    }

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
                System.err.println("[CLIENT LOG] Errore caricamento mail: " + e.getMessage());
                javafx.application.Platform.runLater(() -> updateStatus(false));
            }
        }).start();
    }

    // Controlla se il Server è online oppure no
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
                    }
                    else javafx.application.Platform.runLater(() -> updateStatus(true));

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

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}