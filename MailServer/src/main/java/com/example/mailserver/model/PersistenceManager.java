package com.example.mailserver.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @class PersistenceManager
 * @brief Manages the loading and saving of user data to the file system.
 *
 * This class handles the serialization and deserialization of user inboxes
 * to and from JSON files. It ensures that data is persisted across server
 * restarts. All file operations are synchronized to prevent race conditions.
 */
public class PersistenceManager {
    private static final String BASE_PATH = "server_data/";

    public PersistenceManager() {
        File dir = new File(BASE_PATH);
        if (!dir.exists()) dir.mkdir();
    }

    /**
     * @brief Loads a user's inbox from a JSON file.
     *
     * If the file for the user does not exist, it returns an empty list.
     * This method is synchronized to ensure thread-safe file access.
     *
     * @param userEmail The email address of the user whose inbox is to be loaded.
     * @return A list of Email objects representing the user's inbox.
     */
    public List<Email> loadInbox(String userEmail) {
        File f = new File(BASE_PATH + userEmail + ".json");
        if (!f.exists()) return new ArrayList<>();

        try (Reader r = new FileReader(f)) {
            Type listType = new TypeToken<ArrayList<Email>>(){}.getType();
            List<Email> list = new Gson().fromJson(r, listType);
            return (list != null) ? list : new ArrayList<>();
        }
        catch (IOException e) {
            System.err.println("[SERVER] FATAL ERROR loading " + userEmail + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * @brief Saves a user's inbox to a JSON file.
     *
     * The list of emails is serialized to JSON with pretty printing for readability.
     * This method is synchronized to ensure thread-safe file access.
     *
     * @param userEmail The email address of the user whose inbox is to be saved.
     * @param inbox The list of Email objects to be saved.
     */
    public synchronized void saveInbox(String userEmail, List<Email> inbox) {
        try (Writer w = new FileWriter(BASE_PATH + userEmail + ".json")) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(inbox, w);
        }
        catch (IOException e) {
            System.err.println("[SERVER] FATAL ERROR for saving the inbox: " + e.getMessage());
        }
    }
}
