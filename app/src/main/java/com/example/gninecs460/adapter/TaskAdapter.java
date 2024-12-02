package com.example.gninecs460.adapter;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gninecs460.R;
import com.example.gninecs460.model.TaskModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Locale;
/**
 * Adapter class for handling the display and interaction of tasks within the RecyclerView in the
 * Academic Alliance Chat Application. This class is responsible for managing task items including
 * their creation, update, and deletion functionalities.
 * Developed by Diego and Daniel for the Task Management feature.
 */
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<TaskModel> taskList;
    private Context context;
    public TaskAdapter(List<TaskModel> taskList, Context context) {
        this.taskList = taskList;
        this.context = context;
    }
    public void updateTasks(List<TaskModel> updatedTaskList) {
        this.taskList = updatedTaskList;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        TaskModel task = taskList.get(position);

        holder.title.setText(task.getTitle());
        holder.description.setText(task.getDescription());
        holder.date.setText(task.getDate() != null ? task.getDate() : "No Date");
        holder.time.setText(task.getTime() != null ? task.getTime() : "No Time");
        holder.completed.setChecked(task.isCompleted());

        // Checkbox to mark task as complete
        holder.completed.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setCompleted(isChecked);
            FirebaseFirestore.getInstance().collection("tasks")
                    .document(task.getId())
                    .update("completed", isChecked);
        });
        // Button to delete a task
        holder.deleteButton.setOnClickListener(v -> {
            FirebaseFirestore.getInstance().collection("tasks")
                    .document(task.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        taskList.remove(position);
                        notifyItemRemoved(position);
                        Toast.makeText(context, "Task deleted", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Error deleting task: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
        // Button to edit a task
        holder.editButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_task, null);
            builder.setView(dialogView);

            EditText editTitle = dialogView.findViewById(R.id.edit_task_title_input);
            EditText editDescription = dialogView.findViewById(R.id.edit_task_description_input);
            EditText editTime = dialogView.findViewById(R.id.edit_task_time_input);

            // Pre-fill the fields with current task data
            editTitle.setText(task.getTitle());
            editDescription.setText(task.getDescription());
            editTime.setText(task.getTime());

            editTime.setOnClickListener(tv -> {
                TimePickerDialog timePickerDialog = new TimePickerDialog(context, (view, hourOfDay, minute) -> {
                    String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                    editTime.setText(formattedTime);
                }, 12, 0, true);
                timePickerDialog.show();
            });

            builder.setPositiveButton("Update", (dialog, which) -> {
                String updatedTitle = editTitle.getText().toString().trim();
                String updatedDescription = editDescription.getText().toString().trim();
                String updatedTime = editTime.getText().toString().trim();

                if (updatedTitle.isEmpty()) {
                    Toast.makeText(context, "Title is required", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Update task in Firestore
                FirebaseFirestore.getInstance().collection("tasks")
                        .document(task.getId())
                        .update("title", updatedTitle, "description", updatedDescription, "time", updatedTime)
                        .addOnSuccessListener(aVoid -> {
                            task.setTitle(updatedTitle);
                            task.setDescription(updatedDescription);
                            task.setTime(updatedTime);
                            notifyItemChanged(position);
                            Toast.makeText(context, "Task updated", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(context, "Error updating task: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
            builder.create().show();
        });
    }
    @Override
    public int getItemCount() {
        return taskList.size();
    }
    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, date, time;
        CheckBox completed;
        ImageButton deleteButton, editButton;
        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.task_title);
            description = itemView.findViewById(R.id.task_description);
            date = itemView.findViewById(R.id.task_date);
            time = itemView.findViewById(R.id.task_time);
            completed = itemView.findViewById(R.id.task_completed);
            deleteButton = itemView.findViewById(R.id.delete_button);
            editButton = itemView.findViewById(R.id.edit_button);
        }
    }
}
