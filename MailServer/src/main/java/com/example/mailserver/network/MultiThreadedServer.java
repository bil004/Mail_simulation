package com.example.mailserver.network;

import com.example.mailserver.controller.ServerController;
import com.example.mailserver.model.PersistenceManager;
import javafx.application.Platform;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @class MultiThreadedServer
 * @brief A multithreaded server for handling client connections.
 *
 * This class sets up a `ServerSocket` to listen for incoming client connections
 * on a specified port. For each connection, it creates a new `ClientHandler`
 * and submits it to a thread pool for concurrent processing.
 */
public class MultiThreadedServer implements Runnable {
    private final int port;
    private volatile boolean isRunning;
    private final PersistenceManager pm;
    private final ServerController controller;
    private final List<String> registeredUsers;
    private ExecutorService threadPool;
    private ServerSocket serverSocket;

    /**
     * @brief Constructs a new MultiThreadedServer.
     * @param port The port to listen on.
     * @param controller The server's GUI controller.
     * @param pm The persistence manager for data storage.
     * @param registeredUsers The list of registered user emails.
     */
    public MultiThreadedServer(int port, ServerController controller, PersistenceManager pm, List<String> registeredUsers) {
        this.port = port;
        this.controller = controller;
        this.pm = pm;
        this.registeredUsers = registeredUsers;
        this.isRunning = true;
    }

    /**
     * @brief Starts the server's main loop in a new thread.
     */
    public void start() {
        new Thread(this).start();
    }

    /**
     * @brief The main execution logic for the server thread.
     *
     * It creates a thread pool and a server socket, then enters a loop to
     * accept client connections. Each new connection is passed to a
     * `ClientHandler` in the thread pool.
     */
    @Override
    public void run() {
        threadPool = Executors.newCachedThreadPool();
        try (ServerSocket ss = new ServerSocket(port)) {
            serverSocket = ss;
            Platform.runLater(() -> controller.addLog("SYSTEM", "Socket Server ready: waiting on port " + port + "..."));

            while (isRunning) {
                Socket client = serverSocket.accept();
                threadPool.execute(new ClientHandler(client, registeredUsers, pm, controller));
            }
        } catch (IOException e) {
            if (isRunning) {
                Platform.runLater(() -> controller.addLog("ERROR", "Server Error: " + e.getMessage()));
            }
        } finally {
            if (threadPool != null && !threadPool.isShutdown()) {
                threadPool.shutdown();
            }
            Platform.runLater(() -> controller.addLog("SYSTEM", "Server stopped."));
        }
    }

    /**
     * @brief Stops the server gracefully.
     *
     * Sets the running flag to false and closes the server socket to interrupt
     * the accept loop. The thread pool is shut down in the `run` method's finally block.
     */
    public void stop() {
        isRunning = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            Platform.runLater(() -> controller.addLog("ERROR", "Error while stopping the server: " + e.getMessage()));
        }
    }
}
