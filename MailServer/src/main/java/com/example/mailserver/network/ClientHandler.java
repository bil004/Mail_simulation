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

/**
 * @class ClientHandler
 * @brief Handles communication with a single client in a separate thread.
 *
 * This class is responsible for processing requests from a connected client.
 * It reads commands from the client's input stream, performs the requested
 * actions (like sending, receiving, or deleting emails), and sends back a response.
 * Each `ClientHandler` instance runs on its own thread, allowing the server
 * to handle multiple clients concurrently.
 */
public class ClientHandler implements Runnable{
    private final Socket clientSocket;
    private final List<String> registeredUsers;
    private final PersistenceManager pm;
    private final ServerController controller;
    public final Gson gson = new Gson();


    /**
     * @brief Constructs a new ClientHandler.
     * @param clientSocket The socket connected to the client.
     * @param registeredUsers A list of all registered user emails.
     * @param pm The persistence manager for data storage.
     * @param controller The server's GUI controller for logging.
     */
    public ClientHandler(Socket clientSocket, List<String> registeredUsers, PersistenceManager pm, ServerController controller) {
        this.clientSocket = clientSocket;
        this.registeredUsers = registeredUsers;
        this.pm = pm;
        this.controller = controller;
    }

    /**
     * @brief The main execution method for the client handler thread.
     *
     * It listens for incoming requests from the client, parses them, and delegates
     * to the appropriate handler method. It uses a try-with-resources statement
     * to ensure that the socket and its streams are closed properly.
     */
    @Override
    public void run() {
        // Used try-with-resources for code quality and avoid closure problems
        try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));)
        {
            String req = in.readLine();
            if (req == null || req.isEmpty()) return;

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

    /**
     * @brief Handles a "CONNECT" request from a client.
     *
     * It checks if the provided email address is in the list of registered users.
     * @param req The full request string (e.g., "CONNECT|user@example.com").
     * @param out The PrintWriter to send the response to the client.
     */
    private void handleConnect(String req, PrintWriter out) {
        String checkEmail = req.split("\\|")[1];

        if (isUserRegistered(checkEmail)) {
            Platform.runLater(() ->
                    controller.addLog("CONNECTION", "User " + checkEmail + " connected successfully.")
            );
            sendResponse(out, "OK", "User verified");
        } else {
            Platform.runLater(() ->
                    controller.addLog("ERROR", "Connection failed: " + checkEmail + " not registered.")
            );
            sendResponse(out, "ERROR", "User not found");
        }
    }

    /**
     * @brief Handles a "GET_ALL" request from a client.
     *
     * It loads the entire inbox for the specified user and sends it back as a JSON string.
     * @param req The full request string (e.g., "GET_ALL|user@example.com").
     * @param out The PrintWriter to send the response to the client.
     */
    private void handleGetAll(String req, PrintWriter out) {
        try {
            String userEmail = req.split("\\|")[1];

            List<Email> inbox;

            synchronized (pm) {
                inbox = pm.loadInbox(userEmail);
            }

            String response = gson.toJson(inbox);
            out.println(response);

            controller.addLog("NETWORK", "Find " + inbox.size() + " messages successfully in " + userEmail + " inbox.");
        } catch (Exception ex) {
            out.println("[]");
            System.err.println("[LOG] Get All error: " + ex.getMessage());
        }
    }

    /**
     * @brief Handles a "SEND" request from a client.
     *
     * It deserializes the email from JSON, validates the recipients, and saves
     * the email to each recipient's inbox.
     * @param jsonEmail The email object serialized as a JSON string.
     * @param out The PrintWriter to send the response to the client.
     */
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
            controller.addLog("INCOMING", "Email from "+ e.getSender() +" sent to " + e.getReceivers());

        } catch(Exception ex) {
            sendResponse(out, "ERROR", "Server Error (JSON): " + ex.getMessage());
        }
    }

    /**
     * @brief Handles a "RECEIVE" request from a client.
     *
     * It retrieves all emails for a user that are newer than the last ID
     * provided by the client.
     * @param req The full request string (e.g., "RECEIVE|user@example.com|12345").
     * @param out The PrintWriter to send the response to the client.
     */
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

            if (!newEmails.isEmpty())
                controller.addLog("OUTGOING", "Sent " + newEmails.size() +" messages to " + userEmail);

        } catch (Exception ex) {
            out.println("[]");
            System.err.println("[LOG] Receive Error: " + ex.getMessage());
        }
    }

    /**
     * @brief Handles a "DELETE" request from a client.
     *
     * It removes an email with a specific ID from the user's inbox.
     * @param req The full request string (e.g., "DELETE|user@example.com|12345").
     * @param out The PrintWriter to send the response to the client.
     */
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
                    controller.addLog("STORAGE", "Email " + deleteID + " deleted for " + userEmail);
                } else
                    sendResponse(out, "ERROR", "Email not found!");
            }
        } catch (Exception ex) {
            sendResponse(out, "ERROR", "Server Delete Message error: " + ex.getMessage());
        }
    }

    /**
     * @brief Checks if an email address belongs to a registered user.
     * @param email The email address to check.
     * @return `true` if the user is registered, `false` otherwise.
     */
    private boolean isUserRegistered(String email) {
        return registeredUsers.contains(email);
    }

    /**
     * @brief Sends a standardized response to the client.
     * @param out The PrintWriter to send the response.
     * @param status The status of the response (e.g., "OK", "ERROR").
     * @param message An optional message providing more details.
     */
    private void sendResponse(PrintWriter out, String status, String message) {
        if (message == null || message.isEmpty()) {
            out.println(status);
        } else {
            out.println(status + "|" + message);
        }
    }
}