package com.example.mailserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TestClient {
    public static void main(String[] args) {
        // Test 1: Utente ESISTENTE (Dovrebbe dare OK!)
        testConnection("giorgio@gmail.com");

        // Test 2: Utente NON ESISTENTE (Dovrebbe dare ERROR!)
        testConnection("test@gmail.com");
    }

    private static void testConnection(String email) {
        // La traccia vieta socket permanenti: apriamo e chiudiamo per ogni operazione
        try (Socket socket = new Socket("localhost", 8080);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.println("Client invia: CONNECT|" + email);
            out.println("CONNECT|" + email);

            String response = in.readLine();
            System.out.println("Server risponde: " + response);

        } catch (IOException e) {
            System.err.println("Errore di connessione: " + e.getMessage()); // Gestione crash server
        }
    }
}
