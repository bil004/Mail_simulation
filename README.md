# 📬 JavaFX Mail System - Prog3 Project

This repository contains the final laboratory project for the **Programmazione III** course. The system consists of two distinct Java applications: a **Mail Server** and a **Mail Client**. These applications run in separate Java Virtual Machines (JVMs) and communicate exclusively via text-based data over Java Sockets.

---

## 🏗️ Architecture & Technologies

* **Language & Framework:** Java 17/21 and JavaFX.
* **Modular Design:** Both applications are organized into packages to ensure modularity.
* **Design Pattern:** Strict adherence to the **Model-View-Controller (MVC)** pattern.
    * No direct communication exists between Views and Models.
    * Communication is mediated by Controllers or supported by the **Observer-Observable** pattern.
    * Uses modern JavaFX properties and `ObservableLists` (strictly avoiding deprecated `Observer.java` classes).
* **Networking:** * Communication is handled via **Non-permanent Sockets** (Request-Response model similar to HTTP).
    * Data is transmitted exclusively as **textual data** (JSON via Gson).
* **Concurrency:** * Multi-threaded architecture to parallelize non-sequential tasks.
    * Strict management of mutual exclusion for shared resources.



---

## 📧 Mail Client Features

The Mail Client allows users to manage their personal inbox (InBox):
* **Authentication:** Users log in using their email address as a unique identifier.
    * Syntax is validated locally via **Regular Expressions (Regex)**.
    * Account existence is verified by the Server.
* **InBox Operations:** * View message lists and specific message details.
    * Delete messages from the inbox.
* **Messaging:** * Compose new emails to one or more recipients.
    * Reply (to sender) and Reply-All (to all recipients).
    * Forward messages to new recipients.
* **Dynamic UI:** * The GUI is partially responsive: it automatically refreshes the message list without manual action.
    * System notifications alert the user upon receiving new messages.
* **Resilience:** * The client displays server connection status (Connected/Disconnected).
    * Handles server downtime gracefully and automatically attempts reconnection when the server is active again.

---

## 🖥️ Mail Server Features

The Mail Server acts as the central hub for email routing and storage:
* **Account Management:** Supports a fixed number of pre-configured accounts (3 for demo purposes).
* **Data Persistence:** Messages are stored permanently using **local files** (JSON format) without the use of databases. 
    * Each email instance includes: ID, sender, receiver(s), subject, text, and timestamp.
* **Network Efficiency:** To ensure scalability, the server only transmits messages that have not been previously distributed to the client.
* **Event Logging:** A dedicated GUI displays real-time logs of network interactions, such as connection status, message delivery, and routing errors.



---

## 🚀 Getting Started

### Prerequisites
* **JDK 17/21** or higher.
* **IntelliJ IDEA Ultimate** (Recommended).
* **Maven** for dependency management (Gson, JavaFX).

### How to Run
1. Clone the repository.
2. Open **MailServer** and **MailClient** as two separate IntelliJ IDEA projects.
3. Launch the **Mail Server** first.
4. Launch one or more instances of the **Mail Client** and log in with a pre-configured email address (e.g., `giorgio@gmail.com`).
