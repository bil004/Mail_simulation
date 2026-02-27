package com.example.mailserver.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable{
    private final Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
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

    // TEST
    private boolean isUserRegistered(String email) {
        // In futuro, questo controllerà una lista o dei file JSON sul server
        return email.equals("giorgio@mia.mail.com") ||
                email.equals("anna@mia.mail.com") ||
                email.equals("marco@mia.mail.com");
    }
}