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
        mail.setSubject("Test Postino");
        mail.setMessage("Ciao ragazzi, questo è un test del sistema di persistenza JSON!");
        mail.setDate(new java.util.Date());

        String jsonMail = gson.toJson(mail);

        try (Socket socket = new Socket("localhost", 8080);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.println("--- INVIO EMAIL IN CORSO ---");
            out.println("SEND|" + jsonMail);

            String response = in.readLine();
            System.out.println("Risultato dal Server: " + response);

        } catch (IOException e) {
            System.err.println("Errore di connessione: " + e.getMessage());
        }
    }
}