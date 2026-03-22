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
    private String email;
    private List<Email> inbox;

    public Account(String email, List<Email> inbox) {
        this.email = email;
        this.inbox = inbox;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Email> getInbox() {
        return inbox;
    }

    public void setInbox(List<Email> inbox) {
        this.inbox = inbox;
    }
}