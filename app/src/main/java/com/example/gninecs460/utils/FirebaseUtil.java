package com.example.gninecs460.utils;

import android.annotation.SuppressLint;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Utility class to manage Firebase operations such as authentication, Firestore data retrieval,
 * and storage references. This class centralizes all Firebase interactions.
 * Developed by Boscoe, Daniel, Diego, and Howey as part of the backend setup for managing user data and chat functionalities.
 */
public class FirebaseUtil {

    /**
     * Gets the current user's ID from FirebaseAuth.
     * @return the user ID of the currently authenticated user.
     */
    public static String currentUserId(){
        return FirebaseAuth.getInstance().getUid();
    }

    /**
     * Checks if a user is logged in.
     * @return true if a user is currently logged in, false otherwise.
     */
    public static boolean isLoggedIn(){
        return currentUserId() != null;
    }

    /**
     * Retrieves a reference to the current user's details in the Firestore database.
     * @return a DocumentReference pointing to the current user's document.
     */
    public static DocumentReference currentUserDetails(){
        return FirebaseFirestore.getInstance().collection("users").document(currentUserId());
    }

    /**
     * Retrieves a reference to the collection of all users in Firestore.
     * @return a CollectionReference for the 'users' collection in Firestore.
     */
    public static CollectionReference allUserCollectionReference(){
        return FirebaseFirestore.getInstance().collection("users");
    }

    /**
     * Retrieves a reference to a specific chatroom in Firestore.
     * @param chatroomId the ID of the chatroom to retrieve.
     * @return a DocumentReference to the specified chatroom.
     */
    public static DocumentReference getChatroomReference(String chatroomId){
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId);
    }

    /**
     * Retrieves a reference to the messages collection of a specific chatroom.
     * @param chatroomId the ID of the chatroom whose messages are to be accessed.
     * @return a CollectionReference to the 'chats' subcollection in the specified chatroom.
     */
    public static CollectionReference getChatroomMessageReference(String chatroomId){
        return getChatroomReference(chatroomId).collection("chats");
    }

    /**
     * Generates a chatroom ID based on the IDs of two users, ensuring consistent ID generation
     * regardless of the order of user IDs.
     * @param userId1 the ID of the first user.
     * @param userId2 the ID of the second user.
     * @return a string representing the chatroom ID.
     */
    public static String getChatroomId(String userId1, String userId2) {
        return (userId1.hashCode() < userId2.hashCode()) ? userId1 + "_" + userId2 : userId2 + "_" + userId1;
    }

    /**
     * Retrieves a reference to the collection of all chatrooms in Firestore.
     * @return a CollectionReference for the 'chatrooms' collection.
     */
    public static CollectionReference allChatroomCollectionReference(){
        return FirebaseFirestore.getInstance().collection("chatrooms");
    }

    /**
     * Retrieves a reference to the document of another user from a chatroom based on the list of user IDs.
     * @param userIds a list containing user IDs where the current user's ID is expected to be included.
     * @return a DocumentReference to the other user's document.
     */
    public static DocumentReference getOtherUserFromChatroom(List<String> userIds){
        if(userIds.get(0).equals(FirebaseUtil.currentUserId())){
            return allUserCollectionReference().document(userIds.get(1));
        }else{
            return allUserCollectionReference().document(userIds.get(0));
        }
    }

    /**
     * Converts a Timestamp to a formatted time string.
     * @param timestamp the Timestamp to format.
     * @return a string representing the formatted time.
     */
    @SuppressLint("SimpleDateFormat")
    public static String timestampToString(Timestamp timestamp){
        return new SimpleDateFormat("HH:mm").format(timestamp.toDate());
    }

    /**
     * Logs out the current user from Firebase Authentication.
     */
    public static void logout(){
        FirebaseAuth.getInstance().signOut();
    }

    /**
     * Retrieves a reference to the storage location of the current user's profile picture.
     * @return a StorageReference pointing to the current user's profile picture in Firebase Storage.
     */
    public static StorageReference getCurrentProfilePicStorageRef(){
        return FirebaseStorage.getInstance().getReference().child("profile_pic")
                .child(FirebaseUtil.currentUserId());
    }

    /**
     * Retrieves a reference to the storage location of another user's profile picture.
     * @param otherUserId the ID of the other user whose profile picture reference is needed.
     * @return a StorageReference pointing to the specified user's profile picture.
     */
    public static StorageReference getOtherProfilePicStorageRef(String otherUserId){
        return FirebaseStorage.getInstance().getReference().child("profile_pic")
                .child(otherUserId);
    }

}
