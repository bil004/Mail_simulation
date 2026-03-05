package com.example.mailserver.controller;

import com.example.mailserver.model.LogEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class ServerController {
    @FXML
    private TableView<LogEvent> logTable;
    @FXML
    private TableColumn<LogEvent, String> colTime;
    @FXML
    private TableColumn<LogEvent, String> colType;
    @FXML
    private TableColumn<LogEvent, String> colDesc;

    private final ObservableList<LogEvent> logData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colTime.setCellValueFactory(cellData -> cellData.getValue().timestampProperty());
        colType.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
        colDesc.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        logTable.setItems(logData);
    }

    public void addLog(String type, String message) {
        javafx.application.Platform.runLater(() -> logData.add(new LogEvent(type, message)));
    }
}

