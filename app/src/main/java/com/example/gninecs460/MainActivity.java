package com.example.gninecs460;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageButton;

import com.example.gninecs460.ChatFragment;
import com.example.gninecs460.ProfileFragment;
import com.example.gninecs460.R;
import com.example.gninecs460.SearchUserActivity;
import com.example.gninecs460.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Main activity class that initializes the navigation and manages user interactions with the bottom navigation bar.
 * Developed by Boscoe and Howey, this class integrates the chat, profile, and other main functionalities of the app.
 */
public class MainActivity extends AppCompatActivity {

    // Bottom navigation bar to navigate between different sections of the app
    BottomNavigationView bottomNavigationView;
    // Search button for transitioning to the search user activity
    ImageButton searchButton;

    // Fragments for chat and profile sections of the app
    ChatFragment chatFragment;
    ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this); // Initialize Firebase context

        chatFragment = new ChatFragment(); // Chat functionality developed by Boscoe and Howey
        profileFragment = new ProfileFragment(); // Profile management developed by Boscoe and Howey

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        searchButton = findViewById(R.id.main_search_btn);

        // Handler for the search button that starts the SearchUserActivity
        searchButton.setOnClickListener((v) -> {
            startActivity(new Intent(MainActivity.this, SearchUserActivity.class));
        });

        // Setup listener for bottom navigation items selection
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.menu_chat) {
                    // Load the chat fragment when chat menu item is selected
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_frame_layout, chatFragment)
                            .commit();
                    return true;
                } else if (itemId == R.id.menu_profile) {
                    // Load the profile fragment when profile menu item is selected
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_frame_layout, profileFragment)
                            .commit();
                    return true;
                } else if (itemId == R.id.menu_calendar) {
                    // Start Calendar Activity developed by Diego and Daniel
                    startActivity(new Intent(MainActivity.this, CalendarActivity.class));
                    return true;
                }

                return false; // Return false if no navigation item is handled
            }
        });

        // Set default selected item in bottom navigation
        bottomNavigationView.setSelectedItemId(R.id.menu_chat);

        // Fetch and manage Firebase Cloud Messaging token
        getFCMToken();
    }

    /**
     * Fetches the current user's Firebase Cloud Messaging (FCM) token and updates it in Firestore.
     */
    void getFCMToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    String token = task.getResult(); // Retrieve new FCM token
                    Log.d("MainActivity", "FCM Token: " + token);

                    // Update the token in Firestore for the current user
                    FirebaseUtil.currentUserDetails().update("fcmToken", token)
                            .addOnSuccessListener(aVoid -> Log.d("MainActivity", "Token updated successfully"))
                            .addOnFailureListener(e -> Log.e("MainActivity", "Failed to update token", e));
                } else {
                    Log.e("MainActivity", "Failed to get token", task.getException());
                }
            }
        });
    }
}
