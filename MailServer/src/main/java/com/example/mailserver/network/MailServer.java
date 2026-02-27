package com.example.mailserver.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MailServer {
    private final int port;
    // Il pool di thread permette di gestire più client contemporaneamente (parallelismo)
    private final ExecutorService threadPool;
    private boolean isRunning = true;

    public MailServer(int port) {
        this.port = port;
        // Usiamo un pool dinamico per scalabilità [cite: 3055]
        this.threadPool = Executors.newCachedThreadPool();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("[SERVER] Listening on port " + port + "...");

            while (isRunning) {
                // Il server si blocca qui finché un client non si connette
                Socket clientSocket = serverSocket.accept();
                System.out.println("[SERVER] Connection accepted from: " + clientSocket.getInetAddress());

                // Passiamo il socket al ClientHandler e lo eseguiamo in un thread separato
                threadPool.execute(new ClientHandler(clientSocket));
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