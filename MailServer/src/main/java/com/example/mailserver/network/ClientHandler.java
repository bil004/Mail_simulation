package com.example.mailserver.network;

import com.example.mailserver.model.Email;
import com.example.mailserver.model.PersistenceManager;
import com.google.gson.Gson;

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
    public final Gson gson = new Gson();


    public ClientHandler(Socket clientSocket, List<String> registeredUsers, PersistenceManager pm) {
        this.clientSocket = clientSocket;
        this.registeredUsers = registeredUsers;
        this.pm = pm;
    }

    @Override
    public void run() {
        try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));)
        {
            String req = in.readLine();
            if (req == null) return;

            // --- SMISTAMENTO COMANDI ---
            if (req.startsWith("CONNECT|")) {
                handleConnect(req, out);
            }
            else if (req.startsWith("SEND|")) {
                handleSend(req.substring(5), out); // Togliamo "SEND|" e passiamo il resto (il JSON)
            }
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
            System.out.println("[LOG] Email from "+ e.getSender() +" sent to " + e.getReceivers());

        } catch(Exception ex) {
            sendResponse(out, "ERROR", "Server Error (JSON): " + ex.getMessage());
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