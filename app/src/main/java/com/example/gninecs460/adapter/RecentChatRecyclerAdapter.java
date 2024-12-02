package com.example.gninecs460.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gninecs460.ChatActivity;
import com.example.gninecs460.R;
import com.example.gninecs460.model.ChatroomModel;
import com.example.gninecs460.model.UserModel;
import com.example.gninecs460.utils.AndroidUtil;
import com.example.gninecs460.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
/**
 * Adapter for displaying recent chats in a RecyclerView.
 * Manages user interactions and chatroom data binding for smooth navigation to chat activities.
 * Developed by Boscoe and Howey for the chat functionalities of the Academic Alliance Chat Application.
 */
public class RecentChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatroomModel, RecentChatRecyclerAdapter.ChatroomModelViewHolder> {
    private final Context context;
    /**
     * Constructs the RecentChatRecyclerAdapter with Firestore options and a context.
     * @param options FirestoreRecyclerOptions for handling chatroom models.
     * @param context Application context for resource access and navigation.
     */
    public RecentChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatroomModel> options, Context context) {
        super(options);
        this.context = context;
    }
    @Override
    protected void onBindViewHolder(@NonNull ChatroomModelViewHolder holder, int position, @NonNull ChatroomModel model) {
        // Fetches the user associated with the chatroom and binds it to the view holder
        FirebaseUtil.getOtherUserFromChatroom(model.getUserIds())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        UserModel otherUserModel = task.getResult().toObject(UserModel.class);
                        if (otherUserModel != null) {
                            holder.bind(otherUserModel, model);
                        } else {
                            Log.e("RecentChatAdapter", "UserModel is null");
                        }
                    } else {
                        Log.e("RecentChatAdapter", "Error fetching user", task.getException());
                    }
                });
    }
    @NonNull
    @Override
    public ChatroomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recent_chat_recycler_row, parent, false);
        return new ChatroomModelViewHolder(view);
    }
    /**
     * ViewHolder class for managing the layout and data of each recent chat item.
     */
    static class ChatroomModelViewHolder extends RecyclerView.ViewHolder {
        private final TextView usernameText;
        private final TextView lastMessageText;
        private final TextView lastMessageTime;
        private final ImageView profilePic;

        public ChatroomModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.user_name_text);
            lastMessageText = itemView.findViewById(R.id.last_message_text);
            lastMessageTime = itemView.findViewById(R.id.last_message_time_text);
            profilePic = itemView.findViewById(R.id.profile_pic_image_view);
        }
        /**
         * Binds a user and chatroom data to the ViewHolder UI components.
         * @param user The user associated with the chatroom.
         * @param chat The chatroom model containing recent chat details.
         */
        public void bind(UserModel user, ChatroomModel chat) {
            // Load and set the user's profile picture
            FirebaseUtil.getOtherProfilePicStorageRef(user.getUserId()).getDownloadUrl()
                    .addOnCompleteListener(t -> {
                        if (t.isSuccessful() && t.getResult() != null) {
                            Uri uri = t.getResult();
                            AndroidUtil.setProfilePic(itemView.getContext(), uri, profilePic);
                        } else {
                            profilePic.setImageResource(R.drawable.person_icon); // Fallback image
                        }
                    });

            // Bind username, last message, and timestamp
            usernameText.setText(user.getUsername());
            lastMessageText.setText(chat.getLastMessageSenderId().equals(FirebaseUtil.currentUserId()) ? "You: " + chat.getLastMessage() : chat.getLastMessage());
            lastMessageTime.setText(FirebaseUtil.timestampToString(chat.getLastMessageTimestamp()));

            // Navigate to chat activity on click
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(itemView.getContext(), ChatActivity.class);
                AndroidUtil.passUserModelAsIntent(intent, user);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                itemView.getContext().startActivity(intent);
            });
        }
    }
}
