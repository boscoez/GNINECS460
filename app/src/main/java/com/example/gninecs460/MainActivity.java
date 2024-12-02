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

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    ImageButton searchButton;

    ChatFragment chatFragment;
    ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        chatFragment = new ChatFragment();
        profileFragment = new ProfileFragment();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        searchButton = findViewById(R.id.main_search_btn);

        searchButton.setOnClickListener((v) -> {
            startActivity(new Intent(MainActivity.this, SearchUserActivity.class));
        });

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.menu_chat) {
                    // Load Chat Fragment
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_frame_layout, chatFragment)
                            .commit();
                    return true;
                } else if (itemId == R.id.menu_profile) {
                    // Load Profile Fragment
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_frame_layout, profileFragment)
                            .commit();
                    return true;
                } else if (itemId == R.id.menu_calendar) {
                    // Start Calendar Activity
                    startActivity(new Intent(MainActivity.this, CalendarActivity.class));
                    return true;
                }

                // Return false if no items are handled
                return false;
            }
        });


        bottomNavigationView.setSelectedItemId(R.id.menu_chat);

        getFCMToken();

    }

    void getFCMToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    // Get new FCM registration token
                    String token = task.getResult();
                    Log.d("MainActivity", "FCM Token: " + token);

                    // Update token in Firestore
                    FirebaseUtil.currentUserDetails().update("fcmToken", token)
                            .addOnSuccessListener(aVoid -> Log.d("MainActivity", "Token updated successfully"))
                            .addOnFailureListener(e -> Log.e("MainActivity", "Failed to update token", e));
                } else {
                    // Log or handle the failure to retrieve the token
                    Log.e("MainActivity", "Failed to get token", task.getException());
                }
            }
        });
    }
}