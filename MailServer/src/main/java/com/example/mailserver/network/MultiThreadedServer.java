package com.example.mailserver.network;

import com.example.mailserver.controller.ServerController;
import com.example.mailserver.model.PersistenceManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadedServer {
    private int port;
    private boolean isRunning;
    private final PersistenceManager pm;
    private ServerController controller;
    private final List<String> registeredUsers = List.of(
            "giorgio@gmail.com",
            "anna@gmail.com",
            "marco@gmail.com"
    );

    public MultiThreadedServer(int port, ServerController controller) {
        this.port = port;
        this.isRunning = true;
        this.pm = new PersistenceManager();
        this.controller = controller;
    }

    public void start() {
        ExecutorService pool =  Executors.newCachedThreadPool();

        try (ServerSocket ss = new ServerSocket(port)){
            System.out.println("Server Started on port " + port);

            while(isRunning){
                Socket client = ss.accept();
                System.out.println("New connection: " + client.getInetAddress());
                pool.execute(new ClientHandler(client, registeredUsers, pm, controller));
            }
        } catch  (IOException e) {
            System.out.println("Server Error: " + e.getMessage());
        }
        finally {
            pool.shutdown();
        }

    }
}
