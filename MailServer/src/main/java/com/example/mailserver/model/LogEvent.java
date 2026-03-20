package com.example.mailserver.model;

import javafx.beans.property.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @class LogEvent
 * @brief Represents a log event for display in the server GUI.
 *
 * This class encapsulates the data for a single log entry, including a timestamp,
 * a type (e.g., "INFO", "ERROR"), and a description. It uses JavaFX properties
 * to allow easy binding to UI components like TableView.
 */
public class LogEvent {
    /**
     * @brief The timestamp of the log event, formatted as a string.
     */
    private final StringProperty timestamp;
    /**
     * @brief The type of the log event (e.g., "SYSTEM", "CLIENT").
     */
    private final StringProperty type;
    /**
     * @brief The detailed description of the log event.
     */
    private final StringProperty description;

    /**
     * @brief Constructs a new LogEvent.
     *
     * The timestamp is automatically generated at the time of creation.
     * @param type The type of the log event.
     * @param description The description of the log event.
     */
    public LogEvent(String type, String description) {
        this.timestamp = new SimpleStringProperty(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        this.type = new SimpleStringProperty(type);
        this.description = new SimpleStringProperty(description);
    }

    /**
     * @brief Returns the timestamp property.
     * @return The StringProperty for the timestamp.
     */
    public StringProperty timestampProperty() {
        return timestamp;
    }

    /**
     * @brief Returns the type property.
     * @return The StringProperty for the type.
     */
    public StringProperty typeProperty() {
        return type;
    }

    /**
     * @brief Returns the description property.
     * @return The StringProperty for the description.
     */
    public StringProperty descriptionProperty() {
        return description;
    }
}
