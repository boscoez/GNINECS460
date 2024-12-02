package com.example.gninecs460;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gninecs460.model.TaskModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

/**
 * Activity to add a new task to the database.
 * Developed by Diego and Daniel, this class handles the creation and saving of tasks to Firestore.
 * It includes setting the task's time using a TimePickerDialog.
 */
public class AddTaskActivity extends AppCompatActivity {

    private EditText titleInput, descriptionInput, timeInput;
    private Button saveBtn;
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        titleInput = findViewById(R.id.task_title_input);
        descriptionInput = findViewById(R.id.task_description_input);
        timeInput = findViewById(R.id.task_time_input);
        saveBtn = findViewById(R.id.save_task_btn);

        selectedDate = getIntent().getStringExtra("selectedDate");

        // Set default time to current time
        String initialTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        timeInput.setText(initialTime);

        // Set up a time picker dialog to choose the time for the task
        timeInput.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                timeInput.setText(formattedTime);
            }, 12, 0, true);
            timePickerDialog.show();
        });

        // Set up the save button to create and save a new task
        saveBtn.setOnClickListener(v -> {
            String title = titleInput.getText().toString().trim();
            String description = descriptionInput.getText().toString().trim();
            String time = timeInput.getText().toString().trim();

            // Validate input
            if (title.isEmpty()) {
                titleInput.setError("Title required");
                return;
            }
            if (time.isEmpty()) {
                timeInput.setError("Time required");
                return;
            }

            // Create a new task model object
            TaskModel task = new TaskModel(UUID.randomUUID().toString(), title, description, selectedDate, time, false);

            saveBtn.setEnabled(false); // Prevent double submission
            // Save the task to Firestore and handle success or failure
            FirebaseFirestore.getInstance().collection("tasks").document(task.getId())
                    .set(task)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Task added", Toast.LENGTH_SHORT).show();
                        finish(); // Close the activity on success
                    })
                    .addOnFailureListener(e -> {
                        saveBtn.setEnabled(true);
                        Log.e("AddTaskActivity", "Error adding task", e);
                        Toast.makeText(this, "Error adding task: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

    }
}
