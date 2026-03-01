package com.example.mailclient;

import com.example.mailclient.model.Email;
import com.google.gson.Gson;
import java.io.*;
import java.net.Socket;
import java.util.List;

public class TestClient {
    public static void main(String[] args) {
        Gson gson = new Gson();

        Email mail = new Email();
        mail.setSender("marco@mia.mail.com");
        mail.setReceivers(List.of("giorgio@gmail.com", "anna@gmail.com"));
        mail.setSubject("PC Security");
        mail.setMessage("Hello guys,\n\nI'm Marco from Microsoft Corporate and we've notice some issues on your Windows device.\nLet's have a call to fix it, but remember that we need you Social Security number to proceed.");
        mail.setDate(new java.util.Date());

        String jsonMail = gson.toJson(mail);

        try (Socket socket = new Socket("localhost", 8080);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.println("--- SENDING EMAIL ---");
            out.println("SEND|" + jsonMail);

            String response = in.readLine();
            System.out.println("SERVER RESULT: " + response);

        } catch (IOException e) {
            System.err.println("CONNECTION ERROR: " + e.getMessage());
        }

        // Test 2
        long lastSeenId = -1;
        String user = "giorgio@gmail.com";

        try (Socket socket = new Socket("localhost", 8080);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.println("Client chiede mail nuove dopo l'ID: " + lastSeenId);
            out.println("RECEIVE|" + user + "|" + lastSeenId);

            String response = in.readLine();
            System.out.println("Server ha risposto con questo JSON: " + response);

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Test 3
        long idToDelete = 1772378488810L;

        try (Socket socket = new Socket("localhost", 8080);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.println("Client richiede eliminazione mail ID: " + idToDelete);
            out.println("DELETE|" + user + "|" + idToDelete);

            String response = in.readLine();
            System.out.println("Risposta del Server: " + response);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}