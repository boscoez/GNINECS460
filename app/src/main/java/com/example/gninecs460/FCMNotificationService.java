package com.example.gninecs460;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Handles Firebase Cloud Messaging notifications for the app.
 * Developed by Boscoe and Howey who focused on authentication, login,
 * signup, profile, and chat features. This service processes incoming
 * notification messages and token updates.
 */
public class FCMNotificationService extends FirebaseMessagingService {
    private static final String TAG = "FCMNotificationService";

    /**
     * Called when a new message is received.
     * @param remoteMessage Contains the data and notification payload.
     */
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Check if the message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            showNotification(title, body);
        }
    }

    /**
     * Displays a notification with the specified title and body.
     * @param title Title of the notification.
     * @param body Body text of the notification.
     */
    private void showNotification(String title, String body) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "chat_notifications";

        NotificationChannel channel = new NotificationChannel(
                channelId, "Chat Notifications", NotificationManager.IMPORTANCE_HIGH);
        notificationManager.createNotificationChannel(channel);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.ic_launcher)
                .setAutoCancel(true);

        notificationManager.notify(1, builder.build());
    }

    /**
     * Called when a new token for the default Firebase project is generated.
     * @param token The new token.
     */
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "Refreshed token: " + token);
        // Add logic to send the token to your app server.
        sendRegistrationToServer(token);
    }

    /**
     * Sends the FCM registration token to the app server.
     * @param token The new token to be sent.
     */
    private void sendRegistrationToServer(String token) {
        // Logic to send the token to your app server.
        Log.d(TAG, "Sending token to server: " + token);
    }
}
