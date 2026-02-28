package com.example.mailserver.model;

import com.example.mailserver.model.Email;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PersistenceManager {
    private static final String BASE_PATH = "server_data/";

    public PersistenceManager() {
        File dir = new File(BASE_PATH);
        if (!dir.exists()) dir.mkdir();
    }

    public synchronized List<Email> loadInbox(String userEmail) {
        File f = new File(BASE_PATH + userEmail + ".json");
        if (!f.exists()) return new ArrayList<>();

        try (Reader r = new FileReader(f)) {
            Type listType = new TypeToken<ArrayList<Email>>(){}.getType();
            List<Email> list = new Gson().fromJson(r, listType);
            return (list != null) ? list : new ArrayList<>();
        }
        catch (IOException e) {
            System.err.println("[SERVER] FATAL ERROR caricamento " + userEmail + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public synchronized void saveInbox(String userEmail, List<Email> inbox) {
        try (Writer writer = new FileWriter(BASE_PATH + userEmail + ".json")) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(inbox, writer);
        }
        catch (IOException e) {
            System.err.println("[SERVER] FATAL ERROR: " + e.getMessage());
        }
    }
}
