# JavaFX Mail Server & Client

This repository contains the final project for the "Programmazione III" course [1]. The project consists of two distinct Java applications: a **Mail Server** and a **Mail Client**, running on separate Java Virtual Machines (JVMs) [1, 4]. 

The system implements a custom text-based communication protocol over TCP sockets and features a Graphical User Interface (GUI) built with JavaFX [1, 4].
---

## 🏗️ Architecture & Technologies

* **Language & Framework:** Java 20+ and JavaFX [1, 5].
* **Design Pattern:** Strict adherence to the **Model-View-Controller (MVC)** pattern for both applications [1].
* **Concurrency:** Multithreaded architecture designed to handle parallel tasks and mutual exclusion for shared resources [6].
* **Communication:** Communication between Client and Server is handled via standard Java Sockets, transmitting **exclusively text data** (no serialized Java objects) [4]. Sockets are non-permanent: the connection is opened per-request and closed immediately after, similar to HTTP [7].
* **Data Binding:** The GUI automatically reflects changes in the Model using JavaFX Properties and `ObservableList` [4]. The deprecated `java.util.Observer` and `Observable` classes are strictly avoided [4].

## 📧 Mail Client Features

The Mail Client application allows users to manage their inbox effectively [2]:
* **Authentication:** Login using an email address, validated locally via Regex [8]. The server verifies if the account actually exists [8].
* **Inbox Management:** View incoming messages, read full details, and delete emails [9].
* **Sending Mails:** Create new emails, Reply to sender, Reply-All, and Forward messages to one or multiple recipients [9, 10].
* **Responsive GUI:** The inbox updates automatically when new messages arrive (without requiring a manual refresh) and notifies the user [11].
* **Resilience:** If the server goes offline, the client does not crash; it displays an error message and attempts to auto-reconnect when the server comes back online [7]. Connection status is visibly displayed [11].

## 🖥️ Mail Server Features

The Mail Server acts as the backend handling requests from multiple clients simultaneously [1, 6]:
* **Multi-user Support:** Manages a pre-defined set of email accounts (e.g., 3 accounts for demo purposes) [6, 12].
* **Data Persistence:** Emails are permanently stored using local files (TXT, Binary, or JSON) since database usage is not permitted [3].
* **Scalability:** To optimize network usage, the server only transmits *new* messages to clients upon request, rather than sending the entire mailbox every time [13].
* **Server GUI / Event Logging:** Features a visual interface that logs real-time network events, such as client connections, message deliveries, and routing errors [14]. It validates recipient existence and throws errors for non-existent accounts [12].

## 🚀 Getting Started

### Prerequisites
* **Java Development Kit (JDK):** Version 20 or higher [5].
* **IDE:** IntelliJ IDEA Ultimate is recommended [5].
---

### How to Run
1. Clone this repository to your local machine.
2. Open the project in your IDE. Note that the Client and the Server must be treated as two separate projects [1].
3. Run the **Mail Server** application first.
4. Run one or multiple instances of the **Mail Client** application.
5. Login with one of the pre-configured email addresses.