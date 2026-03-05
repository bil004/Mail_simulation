package com.example.mailclient.controller;

import com.example.mailclient.model.Email;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.net.Socket;
import java.util.Date;
import java.util.List;

public class MainController {
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

    @FXML
    public void initialize() {
        emailListView.setItems(emails);

        // 2. Mock Data: creiamo delle mail finte per il test
        emails.add(new Email(
                1L,
                "giorgio@gmail.com",
                List.of("anna@gmail.com"),
                "Ehi!",
                "Ciao Anna, ci vediamo per il progetto?",
                new Date()
        ));

        emails.add(new Email(
                2L,
                "prof@unito.it",
                List.of("anna@gmail.com"),
                "Esame Prog3",
                "Risultati disponibili sul portale.",
                new Date()
        ));

        emails.add(new Email(
                3L,
                "spam@promo.com",
                List.of("anna@gmail.com"),
                "Sconto 90%",
                "Clicca qui per vincere un iPhone!",
                new Date()
        ));

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

        emailListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) displayEmail(newVal);
        });

        startServerCheck();
    }

    private void displayEmail(Email email) {
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
        System.out.println("DEBUG: Apertura finestra composizione...");
    }

    @FXML
    protected void onDeleteButtonClick() {
        Email selected = emailListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            emails.remove(selected);
            System.out.println("DEBUG: Email eliminata: " + selected.getSubject());
        }
    }

    private void startServerCheck() {
        Thread checkThread = new Thread(() -> {
            while (true) {
                try {

                    try (Socket socket = new Socket("localhost", 8080)) {
                        javafx.application.Platform.runLater(() -> updateStatus(true));
                    }
                } catch (java.io.IOException e) {
                    javafx.application.Platform.runLater(() -> updateStatus(false));
                }

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });

        checkThread.setDaemon(true);
        checkThread.start();
    }
}