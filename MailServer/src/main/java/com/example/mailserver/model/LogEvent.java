package com.example.mailserver.model;

import javafx.beans.property.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogEvent {
    private final StringProperty timestamp;
    private final StringProperty type;
    private final StringProperty description;

    public LogEvent(String type, String description) {
        this.timestamp = new SimpleStringProperty(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        this.type = new SimpleStringProperty(type);
        this.description = new SimpleStringProperty(description);
    }

    public StringProperty timestampProperty() {
        return timestamp;
    }

    public StringProperty typeProperty() {
        return type;
    }

    public StringProperty descriptionProperty() {
        return description;
    }
}
