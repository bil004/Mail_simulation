package com.example.mailserver.model;

import java.util.List;

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