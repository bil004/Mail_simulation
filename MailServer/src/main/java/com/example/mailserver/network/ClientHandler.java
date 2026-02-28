package com.example.mailserver.network;

import com.example.mailserver.model.PersistenceManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable{
    private final Socket clientSocket;
    private final List<String> registeredUsers;
    private final PersistenceManager pm;


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

            if (req != null && req.startsWith("CONNECT|")) {
                String checkEmail = req.split("\\|")[1];

                System.out.println("[LOG] Connection request from: " + checkEmail);

                if (isUserRegistered(checkEmail)) {
                    out.println("OK!");
                    System.out.println("[LOG] User " + checkEmail + " verified successfully!");
                } else {
                    out.println("ERROR!");
                    System.out.println("[LOG] User " + checkEmail + " not found!");
                }
            }
        } catch (IOException e) {
            System.err.println("[LOG] Communication error (socket): " + e.getMessage());
        }
    }

    private boolean isUserRegistered(String email) {
        return registeredUsers.contains(email);
    }
}