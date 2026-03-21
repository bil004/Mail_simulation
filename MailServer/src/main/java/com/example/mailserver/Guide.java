/**
 * @mainpage Client-Server Email System
 *
 * @section intro_sec Introduction
 * Welcome to the documentation for the Programming 3 project.
 * The system is composed of two main modules:
 * - @b MailServer: Manages data persistence (JSON) and multiple connections.
 * - @b MailClient: User interface for managing emails (Send, Receive, Reply, Delete).
 *
 * @section arch_sec Architecture
 * The project uses a Model-View-Controller (MVC) architecture and communication
 * based on TCP sockets with a custom text-based protocol.
 *
 * @section tech_sec Technologies Used
 * - JavaFX for the graphical user interface.
 * - GSON for data serialization.
 * - ThreadPool (ExecutorService) for server-side multithreading.
 *
 * @author Bilal Benslimane
 */