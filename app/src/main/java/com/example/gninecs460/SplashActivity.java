package com.example.gninecs460;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gninecs460.model.UserModel;
import com.example.gninecs460.utils.AndroidUtil;
import com.example.gninecs460.utils.FirebaseUtil;
/**
 * Entry point of the application that handles the splash screen display and initial navigation based on user authentication status.
 * This class was collaboratively developed, with specific contributions:
 * - Boscoe and Howey implemented the authentication flow, including the transition from the splash screen to the main or login activity.
 * - Diego and Daniel focused on ensuring that the application smoothly handles intents from notifications directing users to specific chats.
 */
@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Check if the activity was started from a notification with a user ID to direct to a specific chat
        if(getIntent().getExtras() != null){
            String userId = getIntent().getExtras().getString("userId");
            assert userId != null;
            // Retrieve user details and navigate to chat if user exists
            FirebaseUtil.allUserCollectionReference().document(userId).get()
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            UserModel model = task.getResult().toObject(UserModel.class);

                            Intent mainIntent = new Intent(this, MainActivity.class);
                            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(mainIntent);

                            Intent intent = new Intent(this, ChatActivity.class);
                            assert model != null;
                            AndroidUtil.passUserModelAsIntent(intent, model);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    });
        } else {
            // Delay to display the splash screen before checking the login status
            new Handler().postDelayed(() -> {
                if(FirebaseUtil.isLoggedIn()){
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                } else {
                    startActivity(new Intent(SplashActivity.this, LoginPhoneNumberActivity.class));
                }
                finish();
            }, 3000);
        }
    }
}