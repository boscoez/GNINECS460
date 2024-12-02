package com.example.gninecs460.model;

import com.google.firebase.Timestamp;
import java.util.List;
import java.util.Objects;

/**
 * Represents a chatroom within the Academic Alliance Chat Application. This model stores
 * information about each chatroom, including IDs of participating users, the last message
 * details, and timestamps to manage and display conversations effectively.
 * Developed by Group 9 as part of the chat functionalities. Ensures that all data fields are
 * properly initialized and validates input to maintain data integrity.
 */
public class ChatroomModel {
    private String chatroomId;
    private List<String> userIds;
    private Timestamp lastMessageTimestamp;
    private String lastMessageSenderId;
    private String lastMessage;

    /**
     * Default constructor required for Firebase's automatic data mapping.
     */
    public ChatroomModel() {
        // Required for calls to DataSnapshot.getValue(ChatroomModel.class)
    }

    /**
     * Constructs a new ChatroomModel with specified details ensuring all fields are not null.
     * @param chatroomId Unique identifier for the chatroom.
     * @param userIds List of user IDs who are participants of the chatroom.
     * @param lastMessageTimestamp Timestamp of the last message sent in the chatroom.
     * @param lastMessageSenderId User ID of the sender of the last message.
     * @param lastMessage Content of the last message.
     */
    public ChatroomModel(String chatroomId, List<String> userIds, Timestamp lastMessageTimestamp, String lastMessageSenderId, String lastMessage) {
        setChatroomId(chatroomId);
        setUserIds(userIds);
        setLastMessageTimestamp(lastMessageTimestamp);
        setLastMessageSenderId(lastMessageSenderId);
        setLastMessage(lastMessage);
    }

    // Getters and setters with null checks and data integrity validations

    public String getChatroomId() {
        return chatroomId;
    }

    public void setChatroomId(String chatroomId) {
        this.chatroomId = Objects.requireNonNull(chatroomId, "Chatroom ID cannot be null");
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            throw new IllegalArgumentException("User IDs cannot be null or empty");
        }
        this.userIds = userIds;
    }

    public Timestamp getLastMessageTimestamp() {
        return lastMessageTimestamp;
    }

    public void setLastMessageTimestamp(Timestamp lastMessageTimestamp) {
        this.lastMessageTimestamp = Objects.requireNonNull(lastMessageTimestamp, "Timestamp cannot be null");
    }

    public String getLastMessageSenderId() {
        return lastMessageSenderId;
    }

    public void setLastMessageSenderId(String lastMessageSenderId) {
        this.lastMessageSenderId = Objects.requireNonNull(lastMessageSenderId, "Sender ID cannot be null");
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = Objects.requireNonNull(lastMessage, "Last message cannot be null");
    }

    /**
     * Checks if a user is a participant of this chatroom.
     * @param userId The user ID to check.
     * @return true if the user is a participant, false otherwise.
     */
    public boolean isParticipant(String userId) {
        return userIds.contains(userId);
    }
}
