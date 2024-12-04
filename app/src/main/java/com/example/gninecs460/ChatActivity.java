package com.example.gninecs460;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gninecs460.adapter.ChatRecyclerAdapter;
import com.example.gninecs460.model.ChatMessageModel;
import com.example.gninecs460.model.ChatroomModel;
import com.example.gninecs460.model.UserModel;
import com.example.gninecs460.utils.AndroidUtil;
import com.example.gninecs460.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Query;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
/**
 * ChatActivity manages the chat interface where users can send and receive messages.
 * This class includes functionalities for setting up the chat room, sending messages,
 * and integrating with Firebase for real-time data exchange.
 * Developed by: Boscoe and Howey: Implemented chat functionalities including message sending and receiving.
 */
public class ChatActivity extends AppCompatActivity {

    UserModel otherUser;
    String chatroomId;
    ChatroomModel chatroomModel;
    ChatRecyclerAdapter adapter;
    EditText messageInput;
    ImageButton sendMessageBtn;
    ImageButton backBtn;
    TextView otherUsername;
    RecyclerView recyclerView;
    ImageView imageView;
    /**
     * Initializes the chat activity with required views and setups for messaging.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //get UserModel
        otherUser = AndroidUtil.getUserModelFromIntent(getIntent());
        chatroomId = FirebaseUtil.getChatroomId(FirebaseUtil.currentUserId(), otherUser.getUserId());

        messageInput = findViewById(R.id.chat_message_input);
        sendMessageBtn = findViewById(R.id.message_send_btn);
        backBtn = findViewById(R.id.back_btn);
        otherUsername = findViewById(R.id.other_username);
        recyclerView = findViewById(R.id.chat_recycler_view);
        imageView = findViewById(R.id.profile_pic_image_view);

        FirebaseUtil.getOtherProfilePicStorageRef(otherUser.getUserId()).getDownloadUrl()
                .addOnCompleteListener(t -> {
                    if(t.isSuccessful()){
                        Uri uri  = t.getResult();
                        AndroidUtil.setProfilePic(this,uri,imageView);
                    }
                });

        backBtn.setOnClickListener((v)->{
            onBackPressed();
        });
        otherUsername.setText(otherUser.getUsername());

        sendMessageBtn.setOnClickListener((v -> {
            String message = messageInput.getText().toString().trim();
            if(message.isEmpty())
                return;
            sendMessageToUser(message);
        }));

        getOrCreateChatroomModel();
        setupChatRecyclerView();
    }
    /**
     * Sets up the RecyclerView for displaying chat messages using a FirestoreRecyclerAdapter.
     */
    void setupChatRecyclerView(){
        Query query = FirebaseUtil.getChatroomMessageReference(chatroomId)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query,ChatMessageModel.class).build();

        adapter = new ChatRecyclerAdapter(options,getApplicationContext());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.smoothScrollToPosition(0);
            }
        });
    }
    /**
     * Sends a chat message to the Firebase database and updates the chat room's last message details.
     * @param message The message text to be sent.
     */
    void sendMessageToUser(String message) {
        if (!chatroomModel.isParticipant(FirebaseUtil.currentUserId())) {
            Toast.makeText(this, "You are not a participant in this chatroom.", Toast.LENGTH_SHORT).show();
            return;
        }
        ChatMessageModel chatMessageModel = new ChatMessageModel(message, FirebaseUtil.currentUserId(), Timestamp.now());
        FirebaseUtil.getChatroomMessageReference(chatroomId).add(chatMessageModel)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        chatroomModel.setLastMessageTimestamp(Timestamp.now());
                        chatroomModel.setLastMessageSenderId(FirebaseUtil.currentUserId());
                        chatroomModel.setLastMessage(message);
                        FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);
                        messageInput.setText("");
                        sendNotification(message);
                    } else {
                        AndroidUtil.showToast(getApplicationContext(), "Failed to send message");
                    }
                });
    }
    /**
     * Retrieves or creates a new chatroom model for the chat session.
     */
    void getOrCreateChatroomModel(){
        FirebaseUtil.getChatroomReference(chatroomId).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                chatroomModel = task.getResult().toObject(ChatroomModel.class);
                if(chatroomModel==null){
                    //first time chat
                    chatroomModel = new ChatroomModel(
                            chatroomId,
                            Arrays.asList(FirebaseUtil.currentUserId(),otherUser.getUserId()),
                            Timestamp.now(),
                            "",
                            ""
                    );
                    FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);
                }
            }
        });
    }
    void sendNotification(String message) {
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                UserModel currentUser = task.getResult().toObject(UserModel.class);
                try {
                    assert currentUser != null;
                    JSONObject jsonObject = buildNotificationPayload(currentUser, message, otherUser.getFcmToken());
                    callApi(jsonObject);
                } catch (Exception e) {
                    Log.e("NotificationError", "Error building notification JSON", e);
                    AndroidUtil.showToast(getApplicationContext(), "Failed to send notification");
                }
            } else {
                AndroidUtil.showToast(getApplicationContext(), "Failed to fetch user details");
            }
        });
    }
    /**
     * Constructs a JSON payload for sending a push notification via Firebase Cloud Messaging (FCM).
     * @param user The current user model.
     * @param message The message to include in the notification.
     * @param recipientToken The FCM token of the recipient.
     * @return JSONObject representing the notification payload.
     * @throws JSONException if there is an error constructing the JSON object.
     */
    private JSONObject buildNotificationPayload(UserModel user, String message, String recipientToken) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        JSONObject notificationObj = new JSONObject();
        notificationObj.put("title", user.getUsername());
        notificationObj.put("body", message);

        JSONObject dataObj = new JSONObject();
        dataObj.put("userId", user.getUserId());

        jsonObject.put("notification", notificationObj);
        jsonObject.put("data", dataObj);
        jsonObject.put("to", recipientToken);
        return jsonObject;
    }
    /**
     * Makes an HTTP POST request to send the notification through FCM.
     * @param jsonObject The JSON payload containing the notification data.
     */
    void callApi(JSONObject jsonObject){
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String url = "https://fcm.googleapis.com/fcm/send";
        RequestBody body = RequestBody.create(jsonObject.toString(),JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization","Bearer YOUR_API_KEY")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("FCM_API_CALL", "Failed to send FCM message: " + e.getMessage());
                runOnUiThread(() -> Toast.makeText(ChatActivity.this, "Notification failed to send.", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.i("FCM_API_CALL", "FCM message sent successfully!");
                } else {
                    Log.e("FCM_API_CALL", "Error response from FCM: " + response.body().string());
                    // Handle other HTTP errors here
                }
                response.close();// Ensure to close the response body to free up system resources
            }
        });

    }
}