package com.example.gninecs460.model;

import com.google.firebase.firestore.FirebaseFirestore;

public class TaskModel {
    private String id;
    private String title;
    private String description;
    private String date;
    private String time; // Add this field
    private boolean completed;

    public TaskModel() {
        // Required for Firestore
    }

    public TaskModel(String id, String title, String description, String date, String time, boolean completed) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
        this.completed = completed;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; } // Getter for time
    public void setTime(String time) { this.time = time; } // Setter for time

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    FirebaseFirestore db = FirebaseFirestore.getInstance();

}

