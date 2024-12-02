package com.example.gninecs460;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gninecs460.adapter.TaskAdapter;
import com.example.gninecs460.model.TaskModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * CalendarActivity manages the calendar interface where users can view and manage their tasks.
 * This activity includes a calendar view that allows selecting a date to display tasks and
 * a button to add new tasks. Developed by Diego and Daniel.
 */
public class CalendarActivity extends AppCompatActivity {

    private RecyclerView taskRecyclerView;
    private TaskAdapter taskAdapter;
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        Log.d("CalendarActivity", "CalendarActivity launched");

        CalendarView calendarView = findViewById(R.id.calendar_view);
        taskRecyclerView = findViewById(R.id.task_recycler_view);
        Button addTaskBtn = findViewById(R.id.add_task_btn);

        // Default selected date is today
        selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        setupRecyclerView();

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
            fetchTasks();
        });

        Button backToMainBtn = findViewById(R.id.back_to_main_btn);
        backToMainBtn.setOnClickListener(v -> {
            finish(); // Closes the current activity and returns to the main activity
        });
        addTaskBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddTaskActivity.class);
            intent.putExtra("selectedDate", selectedDate);
            startActivity(intent);
        });

        fetchTasks();
    }
    /**
     * Sets up the RecyclerView for displaying tasks.
     */
    private void setupRecyclerView() {
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(new ArrayList<>(), this); // Pass `this` as the Context
        taskRecyclerView.setAdapter(taskAdapter);
    }
    /**
     * Fetches tasks from Firebase Firestore based on the selected date.
     */
    private void fetchTasks() {
        FirebaseFirestore.getInstance().collection("tasks")
                .whereEqualTo("date", selectedDate)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<TaskModel> tasks = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            TaskModel taskModel = doc.toObject(TaskModel.class);
                            taskModel.setId(doc.getId()); // Ensure the ID is set
                            tasks.add(taskModel);
                        }
                        taskAdapter.updateTasks(tasks);
                    } else {
                        Toast.makeText(this, "Error fetching tasks", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
