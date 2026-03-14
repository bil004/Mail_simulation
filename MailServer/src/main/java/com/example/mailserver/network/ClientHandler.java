package com.example.mailserver.network;

import com.example.mailserver.controller.ServerController;
import com.example.mailserver.model.Email;
import com.example.mailserver.model.PersistenceManager;
import com.google.gson.Gson;
import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ClientHandler implements Runnable{
    private final Socket clientSocket;
    private final List<String> registeredUsers;
    private final PersistenceManager pm;
    private ServerController controller;
    public final Gson gson = new Gson();


    public ClientHandler(Socket clientSocket, List<String> registeredUsers, PersistenceManager pm, ServerController controller) {
        this.clientSocket = clientSocket;
        this.registeredUsers = registeredUsers;
        this.pm = pm;
        this.controller = controller;
    }

    @Override
    public void run() {
        // Used try-with-resources for code quality and avoid closure problems
        try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));)
        {
            String req = in.readLine();
            if (req == null || req.isEmpty()) return;

            Platform.runLater(() ->
                    controller.addLog("NETWORK", "Request: " + req + " from " + clientSocket.getInetAddress())
            );

            // --- Commands ---
            if (req.startsWith("CONNECT|"))
                handleConnect(req, out);

            else if (req.startsWith("SEND|"))
                handleSend(req.substring(5), out);

            else if (req.startsWith("RECEIVE|"))
                handleReceive(req, out);

            else if (req.startsWith("GET_ALL|"))
                handleGetAll(req, out);

            else if (req.startsWith("DELETE|"))
                handleDelete(req, out);

        } catch (IOException e) {
            System.err.println("[LOG] Communication error (socket): " + e.getMessage());
        }
    }

    private void handleConnect(String req, PrintWriter out) {
        String checkEmail = req.split("\\|")[1];
        System.out.println("[LOG] Connection request from: " + checkEmail);

        if (isUserRegistered(checkEmail)) {
            sendResponse(out, "OK", "User verified");
        } else {
            sendResponse(out, "ERROR", "User not found");
        }
    }

    private void handleGetAll(String req, PrintWriter out) {
        try {
            String userEmail = req.split("\\|")[1];

            List<Email> inbox;

            synchronized (pm) {
                inbox = pm.loadInbox(userEmail);
            }

            String response = gson.toJson(inbox);
            out.println(response);

            controller.addLog("LOG", "Find " + inbox.size() + " messages successfully in " + userEmail + " inbox.");
        } catch (Exception ex) {
            out.println("[]");
            System.err.println("[LOG] Get All error: " + ex.getMessage());
        }
    }

    private void handleSend(String jsonEmail, PrintWriter out) {
        try {
            // Reflection with Gson (for decode the email)
            Email e = gson.fromJson(jsonEmail, Email.class);
            e.setId(System.currentTimeMillis());

            // Handle Invalid User
            List<String> invalidUsers = new ArrayList<>();
            for (String receiver: e.getReceivers()) {
                if (!isUserRegistered(receiver))
                    invalidUsers.add(receiver);
            }

            // Stop the program and report the invalid users
            if (!invalidUsers.isEmpty()) {
                sendResponse(out, "ERROR", "Invalid email address: " + String.join(", ", invalidUsers));
                System.err.println("[LOG] Failed delivery from: " + e.getSender());
                return;
            }

            // Mutual exclusion and Persistence for saving emails on receivers inbox
            for (String receiver: e.getReceivers()) {
                synchronized (pm) {
                    List<Email> inbox = pm.loadInbox(receiver);
                    inbox.add(e);
                    pm.saveInbox(receiver, inbox);
                }
            }

            sendResponse(out, "OK", "Email sent successfully to all receivers!");
            controller.addLog("LOG", "Email from "+ e.getSender() +" sent to " + e.getReceivers());

        } catch(Exception ex) {
            sendResponse(out, "ERROR", "Server Error (JSON): " + ex.getMessage());
        }
    }

    private void handleReceive(String req, PrintWriter out) {
        try {
            String[] parts = req.split("\\|");
            String userEmail = parts[1];
            long lastId = Long.parseLong(parts[2]);

            // loading all emails received from the userEmail
            List<Email> inbox;
            synchronized (pm) {
                inbox = pm.loadInbox(userEmail);
            }

            // Get all emails with ID greater than the client ID
            List<Email> newEmails = new ArrayList<>();
            for (Email e: inbox) {
                if (e.getId() > lastId)
                    newEmails.add(e);
            }

            String jsonResponse = gson.toJson(newEmails);
            out.println(jsonResponse);

            controller.addLog("LOG", "Sent " + newEmails.size() +" messages to " + userEmail);
        } catch (Exception ex) {
            out.println("[]");
            System.err.println("[LOG] Receive Error: " + ex.getMessage());
        }
    }

    private void handleDelete(String req, PrintWriter out) {
        try {
            String[] parts = req.split("\\|");
            String userEmail = parts[1];
            long deleteID = Long.parseLong(parts[2]);

            synchronized (pm) {
                List<Email> inbox = pm.loadInbox(userEmail);
                boolean removed = inbox.removeIf(e -> e.getId() == deleteID);

                if (removed) {
                    pm.saveInbox(userEmail, inbox);
                    sendResponse(out, "OK", "Email deleted successfully!");
                    System.out.println("[LOG] Email "+ deleteID + " successfully deleted from " + userEmail + " inbox!");
                } else
                    sendResponse(out, "ERROR", "Email not found!");
            }
        } catch (Exception ex) {
            sendResponse(out, "ERROR", "Server Delete Message error: " + ex.getMessage());
        }
    }

    private boolean isUserRegistered(String email) {
        return registeredUsers.contains(email);
    }

    private void sendResponse(PrintWriter out, String status, String message) {
        if (message == null || message.isEmpty()) {
            out.println(status);
        } else {
            out.println(status + "|" + message);
        }
    }
}