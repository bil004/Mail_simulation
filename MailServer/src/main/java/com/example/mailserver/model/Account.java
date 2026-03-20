package com.example.mailserver.model;

import java.util.List;

/**
 * @class Account
 * @brief Represents a user account in the mail server.
 *
 * This class encapsulates the data for a user account, including the user's
 * email address and their inbox, which contains a list of emails.
 */
public class Account {
    /**
     * @brief The email address associated with the account.
     */
    private String email;
    /**
     * @brief The list of emails in the user's inbox.
     */
    private List<Email> inbox;

    /**
     * @brief Constructs a new Account.
     * @param email The user's email address.
     * @param inbox The user's inbox.
     */
    public Account(String email, List<Email> inbox) {
        this.email = email;
        this.inbox = inbox;
    }

    /**
     * @brief Gets the email address of the account holder.
     * @return The email address.
     */
    public String getEmail() {
        return email;
    }

    /**
     * @brief Sets the email address of the account holder.
     * @param email The new email address.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @brief Gets the list of emails in the inbox.
     * @return A list of Email objects.
     */
    public List<Email> getInbox() {
        return inbox;
    }

    /**
     * @brief Sets the list of emails in the inbox.
     * @param inbox The new list of emails.
     */
    public void setInbox(List<Email> inbox) {
        this.inbox = inbox;
    }
}