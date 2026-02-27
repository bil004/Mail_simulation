package com.example.mailserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TestClient {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 8080);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println("CONNECT|giorgio@mia.mail.com");
            System.out.println("Server dice: " + in.readLine());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
