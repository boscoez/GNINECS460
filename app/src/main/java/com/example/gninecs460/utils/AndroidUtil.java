package com.example.gninecs460.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.gninecs460.model.UserModel;

/**
 * Utility class providing helper methods for commonly used functions across the app.
 * Developed by:  Boscoe and Howey: Implemented utility functionalities.
 */
public class AndroidUtil {

    /**
     * Displays a toast message.
     * @param context Context in which the toast should be shown.
     * @param message The message to display.
     */
    public static void showToast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    /**
     * Passes user model data via an intent to another activity.
     * @param intent The intent to which user data should be added.
     * @param model The user model containing the data to pass.
     */
    public static void passUserModelAsIntent(Intent intent, UserModel model){
        intent.putExtra("username", model.getUsername());
        intent.putExtra("phone", model.getPhone());
        intent.putExtra("userId", model.getUserId());
        intent.putExtra("fcmToken", model.getFcmToken());
    }

    /**
     * Retrieves a UserModel object from an intent.
     * @param intent The intent from which user data is to be retrieved.
     * @return UserModel containing data from the intent.
     */
    public static UserModel getUserModelFromIntent(Intent intent){
        UserModel userModel = new UserModel();
        userModel.setUsername(intent.getStringExtra("username"));
        userModel.setPhone(intent.getStringExtra("phone"));
        userModel.setUserId(intent.getStringExtra("userId"));
        userModel.setFcmToken(intent.getStringExtra("fcmToken"));
        return userModel;
    }

    /**
     * Sets a profile picture into an ImageView using Glide for image loading and circular cropping.
     * @param context The current context.
     * @param imageUri The URI of the image to load.
     * @param imageView The ImageView where the image will be set.
     */
    public static void setProfilePic(Context context, Uri imageUri, ImageView imageView){
        Glide.with(context).load(imageUri).apply(RequestOptions.circleCropTransform()).into(imageView);
    }
}
