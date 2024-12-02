package com.example.gninecs460.model;

import com.google.firebase.Timestamp;
import java.util.List;
import java.util.Objects;

public class ChatroomModel {
    private String chatroomId;
    private List<String> userIds;
    private Timestamp lastMessageTimestamp;
    private String lastMessageSenderId;
    private String lastMessage;

    public ChatroomModel() {
        // Default constructor required for calls to DataSnapshot.getValue(ChatroomModel.class)
    }

    public ChatroomModel(String chatroomId, List<String> userIds, Timestamp lastMessageTimestamp, String lastMessageSenderId, String lastMessage) {
        setChatroomId(chatroomId);
        setUserIds(userIds);
        setLastMessageTimestamp(lastMessageTimestamp);
        setLastMessageSenderId(lastMessageSenderId);
        setLastMessage(lastMessage);
    }

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
