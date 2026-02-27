package com.example.mailserver.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadedServer {
    private int port;
    private boolean isRunning;

    public MultiThreadedServer(int port) {
        this.port = port;
        this.isRunning = true;
    }

    public void start() {
        ExecutorService pool =  Executors.newCachedThreadPool();

        try (ServerSocket ss = new ServerSocket(port)){
            System.out.println("Server Started on port " + port);

            while(isRunning){
                Socket client = ss.accept();
                System.out.println("New connection: " + client.getInetAddress());
                pool.execute(new ClientHandler(client));
            }
        } catch  (IOException e) {
            System.out.println("Server Error: " + e.getMessage());
        }
        finally {
            pool.shutdown();
        }

    }
}
