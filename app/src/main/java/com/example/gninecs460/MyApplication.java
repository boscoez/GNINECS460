package com.example.gninecs460;

import android.app.Application;

import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;

/**
 * Custom Application class that initializes necessary Firebase and Firebase App Check services.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Uncomment to initialize Firebase when not already initialized elsewhere in your application
        //FirebaseApp.initializeApp(this);

        // Initialize Firebase App Check to secure your app's Firebase backend
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                // Use Play Integrity in production to ensure that your app's traffic comes from genuine installations
                PlayIntegrityAppCheckProviderFactory.getInstance());

        // Use the Debug provider during development to bypass app integrity checks
        DebugAppCheckProviderFactory.getInstance();
    }
}
