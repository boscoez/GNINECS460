package com.example.gninecs460.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gninecs460.R;
import com.example.gninecs460.model.ChatMessageModel;
import com.example.gninecs460.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

/**
 * Adapter for displaying chat messages in a RecyclerView.
 * This adapter dynamically handles messages from the sender and receiver,
 * showing them on the appropriate sides of the chat interface.
 * Developed by Boscoe and Howey for the Academic Alliance Chat Application.
 */
public class ChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatMessageModel, ChatRecyclerAdapter.ChatModelViewHolder> {
    private final Context context;
    /**
     * Constructs the ChatRecyclerAdapter with Firestore options and a context.
     * @param options FirestoreRecyclerOptions for managing chat messages.
     * @param context Application context for resource access.
     */
    public ChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatMessageModel> options, Context context) {
        super(options);
        this.context = context;
    }
    @Override
    protected void onBindViewHolder(@NonNull ChatModelViewHolder holder, int position, @NonNull ChatMessageModel model) {
        Log.i("FCM", "Binding message");

        // Get the formatted timestamp
        String formattedTimestamp = model.getFormattedTimestamp();

        if (model.getSenderId().equals(FirebaseUtil.currentUserId())) {
            // Sender (Right Chat Bubble)
            holder.leftChatLayout.setVisibility(View.GONE);
            holder.rightChatLayout.setVisibility(View.VISIBLE);
            holder.rightChatTextview.setText(model.getMessage());
            holder.rightChatTimeTextview.setText(formattedTimestamp);
        } else {
            // Receiver (Left Chat Bubble)
            holder.rightChatLayout.setVisibility(View.GONE);
            holder.leftChatLayout.setVisibility(View.VISIBLE);
            holder.leftChatTextview.setText(model.getMessage());
            holder.leftChatTimeTextview.setText(formattedTimestamp);
        }
    }

    @NonNull
    @Override
    public ChatModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_message_recycler_row, parent, false);
        return new ChatModelViewHolder(view);
    }
    /**
     * ViewHolder for managing chat message items in the RecyclerView.
     * Handles both sender and receiver message layouts.
     */
    public static class ChatModelViewHolder extends RecyclerView.ViewHolder {
        LinearLayout leftChatLayout;
        LinearLayout rightChatLayout;
        TextView leftChatTextview;
        TextView leftChatTimeTextview; // Add this
        TextView rightChatTextview;
        TextView rightChatTimeTextview; // Add this
        /**
         * Initializes the view components for the chat message.
         * @param itemView The view of the chat message row.
         */
        public ChatModelViewHolder(@NonNull View itemView) {
            super(itemView);
            leftChatLayout = itemView.findViewById(R.id.left_chat_layout);
            rightChatLayout = itemView.findViewById(R.id.right_chat_layout);
            leftChatTextview = itemView.findViewById(R.id.left_chat_textview);
            leftChatTimeTextview = itemView.findViewById(R.id.left_chat_time_textview); // Initialize
            rightChatTextview = itemView.findViewById(R.id.right_chat_textview);
            rightChatTimeTextview = itemView.findViewById(R.id.right_chat_time_textview); // Initialize
        }
    }
}
