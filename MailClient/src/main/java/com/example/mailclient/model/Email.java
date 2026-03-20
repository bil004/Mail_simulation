package com.example.mailclient.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Represents an email message.
 * This class is used to store the details of an email, such as sender, receivers, subject, and message body.
 */
public class Email implements Serializable {
    private long id;
    private String sender;
    private List<String> receivers;
    private String subject;
    private String message;
    private Date date;

    /**
     * Default constructor.
     */
    public Email() {}

    /**
     * Constructor with all fields.
     * @param id The unique ID of the email.
     * @param sender The sender's email address.
     * @param receivers A list of receiver email addresses.
     * @param subject The subject of the email.
     * @param message The body of the email.
     * @param date The date the email was sent.
     */
    public Email(long id, String sender, List<String> receivers, String subject, String message, Date date) {
        this.id = id;
        this.sender = sender;
        this.receivers = receivers;
        this.subject = subject;
        this.message = message;
        this.date = date;
    }

    /**
     * Gets the unique ID of the email.
     * @return The email ID.
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the unique ID of the email.
     * @param id The email ID.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Gets the sender's email address.
     * @return The sender's email address.
     */
    public String getSender() {
        return sender;
    }

    /**
     * Sets the sender's email address.
     * @param sender The sender's email address.
     */
    public void setSender(String sender) {
        this.sender = sender;
    }

    /**
     * Gets the list of receiver email addresses.
     * @return The list of receivers.
     */
    public List<String> getReceivers() {
        return receivers;
    }

    /**
     * Sets the list of receiver email addresses.
     * @param receivers The list of receivers.
     */
    public void setReceivers(List<String> receivers) {
        this.receivers = receivers;
    }

    /**
     * Gets the subject of the email.
     * @return The email subject.
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Sets the subject of the email.
     * @param subject The email subject.
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * Gets the body of the email.
     * @return The email message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the body of the email.
     * @param message The email message.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the date the email was sent.
     * @return The send date.
     */
    public Date getDate() {
        return date;
    }

    /**
     * Sets the date the email was sent.
     * @param date The send date.
     */
    public void setDate(Date date) {
        this.date = date;
    }
}