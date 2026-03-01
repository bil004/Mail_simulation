package com.example.mailserver;

import com.example.mailserver.model.Email;
import com.example.mailserver.model.PersistenceManager;
import com.example.mailserver.network.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MailServer {
    private final int port;
    private final ExecutorService threadPool;
    private boolean isRunning = true;
    private final PersistenceManager pm;
    private final List<String> registeredUsers = List.of(
            "giorgio@gmail.com",
            "anna@gmail.com",
            "marco@gmail.com"
    );

    public MailServer(int port) {
        this.port = port;
        this.pm = new PersistenceManager();
        this.threadPool = Executors.newCachedThreadPool();
        initializeAccounts();
    }

    private void initializeAccounts() {
        for (String email : registeredUsers) {
            List<Email> inbox = pm.loadInbox(email);
            pm.saveInbox(email, inbox);
        }
        System.out.println("[SERVER] Account precompiled loaded successfully.");
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("[SERVER] Listening on port " + port + "...");

            while (isRunning) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("[SERVER] Connection accepted from: " + clientSocket.getInetAddress());
                threadPool.execute(new ClientHandler(clientSocket, registeredUsers, pm));
            }
        } catch (IOException e) {
            System.err.println("[SERVER] FATAL ERROR: " + e.getMessage());
        } finally {
            threadPool.shutdown();
        }
    }

    public static void main(String[] args) {
        MailServer server = new MailServer(8080);
        server.start();
    }
}