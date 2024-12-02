package com.example.gninecs460;

import static java.util.UUID.randomUUID;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gninecs460.model.TaskModel;
import com.google.firebase.firestore.FirebaseFirestore;
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

        timeInput.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                timeInput.setText(formattedTime);
            }, 12, 0, true);
            timePickerDialog.show();
        });

        saveBtn.setOnClickListener(v -> {
            String title = titleInput.getText().toString().trim();
            String description = descriptionInput.getText().toString().trim();
            String time = timeInput.getText().toString().trim();

            if (title.isEmpty()) {
                titleInput.setError("Title required");
                return;
            }
            if (time.isEmpty()) {
                timeInput.setError("Time required");
                return;
            }

            TaskModel task = new TaskModel(UUID.randomUUID().toString(), title, description, selectedDate, time, false);

            saveBtn.setEnabled(false); // Prevent double submission
            FirebaseFirestore.getInstance().collection("tasks").document(task.getId())
                    .set(task)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Task added", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        saveBtn.setEnabled(true);
                        Log.e("AddTaskActivity", "Error adding task", e);
                        Toast.makeText(this, "Error adding task: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

    }
}
