package com.example.gninecs460.model;

import com.google.firebase.Timestamp;

/**
 * Represents a user in the Academic Alliance Chat Application.
 * This class is used to manage user details and interactions within the application.
 */
public class UserModel {
    private String phone;
    private String username;
    private Timestamp createdTimestamp;
    private String userId;
    private String fcmToken;
    private String profilePicUrl;
    /**
     * Default constructor required for calls to DataSnapshot.getValue(UserModel.class)
     */
    public UserModel() {
    }

    /**
     * Constructs a new UserModel with specified details.
     * @param phone             the user's phone number
     * @param username          the user's username
     * @param createdTimestamp  the timestamp when the user was created
     * @param userId            the unique identifier for the user
     */
    public UserModel(String phone, String username, Timestamp createdTimestamp, String userId) {
        this.phone = phone;
        this.username = username;
        this.createdTimestamp = createdTimestamp;
        this.userId = userId;
    }

    /**
     * Returns the phone number of the user.
     * @return the phone number
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the phone number of the user.
     * @param phone the phone number to set
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Returns the username of the user.
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user.
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the timestamp when the user was created.
     * @return the created timestamp
     */
    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    /**
     * Sets the timestamp when the user was created.
     * @param createdTimestamp the created timestamp to set
     */
    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    /**
     * Returns the unique identifier for the user.
     * @return the user ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the unique identifier for the user.
     * @param userId the user ID to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Returns the Firebase Cloud Messaging (FCM) token for the user.
     * @return the FCM token
     */
    public String getFcmToken() {
        return fcmToken;
    }

    /**
     * Sets the Firebase Cloud Messaging (FCM) token for the user.
     * @param fcmToken the FCM token to set
     */
    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    /**
     * Returns the URL of the user's profile picture.
     * @return the profile picture URL
     */
    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    /**
     * Sets the URL of the user's profile picture.
     * @param profilePicUrl the profile picture URL to set
     */
    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }
}
