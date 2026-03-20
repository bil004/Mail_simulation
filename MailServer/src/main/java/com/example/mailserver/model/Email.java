package com.example.mailserver.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @class Email
 * @brief Represents an email message.
 *
 * This class is a simple data structure (POJO) that holds all the information
 * related to an email, such as sender, receivers, subject, and content.
 * It implements `Serializable` to allow its instances to be converted into a byte stream.
 */
public class Email implements Serializable {
    /**
     * @brief The unique identifier for the email.
     */
    private long id;
    /**
     * @brief The sender's email address.
     */
    private String sender;
    /**
     * @brief A list of recipient email addresses.
     */
    private List<String> receivers;
    /**
     * @brief The subject of the email.
     */
    private String subject;
    /**
     * @brief The main content/body of the email.
     */
    private String message;
    /**
     * @brief The date and time when the email was sent.
     */
    private Date date;

    /**
     * @brief Default constructor.
     */
    public Email() {}

    /**
     * @brief Constructs a new Email with all its properties.
     * @param id The unique ID.
     * @param sender The sender's address.
     * @param receivers The list of recipients.
     * @param subject The email subject.
     * @param message The email body.
     * @param date The sending date.
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
     * @brief Gets the unique ID of the email.
     * @return The email ID.
     */
    public long getId() {
        return id;
    }

    /**
     * @brief Sets the unique ID of the email.
     * @param id The new email ID.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @brief Gets the sender's email address.
     * @return The sender's email.
     */
    public String getSender() {
        return sender;
    }

    /**
     * @brief Sets the sender's email address.
     * @param sender The new sender's email.
     */
    public void setSender(String sender) {
        this.sender = sender;
    }

    /**
     * @brief Gets the list of recipient email addresses.
     * @return A list of recipient emails.
     */
    public List<String> getReceivers() {
        return receivers;
    }

    /**
     * @brief Sets the list of recipient email addresses.
     * @param receivers The new list of recipients.
     */
    public void setReceivers(List<String> receivers) {
        this.receivers = receivers;
    }

    /**
     * @brief Gets the subject of the email.
     * @return The email subject.
     */
    public String getSubject() {
        return subject;
    }

    /**
     * @brief Sets the subject of the email.
     * @param subject The new email subject.
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * @brief Gets the body/content of the email.
     * @return The email message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * @brief Sets the body/content of the email.
     * @param message The new email message.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @brief Gets the date the email was sent.
     * @return The sending date.
     */
    public Date getDate() {
        return date;
    }

    /**
     * @brief Sets the date the email was sent.
     * @param date The new sending date.
     */
    public void setDate(Date date) {
        this.date = date;
    }
}