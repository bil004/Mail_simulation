package com.example.mailserver.controller;

import com.example.mailserver.model.LogEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * @class ServerController
 * @brief Controls the server's user interface.
 *
 * This class manages the JavaFX components of the server's GUI, primarily
 * handling the display of log events in a TableView.
 */
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

    /**
     * @brief Adds a new log entry to the log table.
     *
     * This method is thread-safe and can be called from any thread. It uses
     * `Platform.runLater()` to ensure that the GUI is updated on the
     * JavaFX Application Thread.
     *
     * @param type The type of the log event (e.g., "SYSTEM", "ERROR").
     * @param message The log message to be displayed.
     */
    public void addLog(String type, String message) {
        javafx.application.Platform.runLater(() -> logData.add(new LogEvent(type, message)));
    }
}
