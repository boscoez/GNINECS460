package com.example.gninecs460.model;

import com.google.firebase.Timestamp;

/**
 * Represents an individual chat message in the Academic Alliance Chat Application.
 * This model is used to store and retrieve messages exchanged between users in chatrooms.
 * Developed by Group 9, focusing on real-time communication features.
 */
public class ChatMessageModel {
    private String message; // The content of the chat message
    private String senderId; // User ID of the sender
    private Timestamp timestamp; // Timestamp of when the message was sent

    /**
     * Default constructor required for Firebase's automatic data mapping.
     */
    public ChatMessageModel() {
    }

    /**
     * Constructs a new ChatMessageModel with specified message details.
     * @param message The content of the chat message.
     * @param senderId The user ID of the sender.
     * @param timestamp The timestamp when the message was sent.
     */
    public ChatMessageModel(String message, String senderId, Timestamp timestamp) {
        this.message = message;
        this.senderId = senderId;
        this.timestamp = timestamp;
    }

    /**
     * Gets the message content.
     * @return The content of the message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message content.
     * @param message The content of the message to set.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the ID of the sender of the message.
     * @return The sender's user ID.
     */
    public String getSenderId() {
        return senderId;
    }

    /**
     * Sets the sender's user ID.
     * @param senderId The user ID of the sender to set.
     */
    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    /**
     * Gets the timestamp of when the message was sent.
     * @return The timestamp of the message.
     */
    public Timestamp getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp of when the message was sent.
     * @param timestamp The timestamp to set.
     */
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
