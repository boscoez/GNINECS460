package com.example.gninecs460;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.fragment.app.Fragment;
import androidx.media3.common.util.Log;
import androidx.media3.common.util.UnstableApi;

import com.example.gninecs460.model.UserModel;
import com.example.gninecs460.utils.AndroidUtil;
import com.example.gninecs460.utils.FirebaseUtil;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class ProfileFragment extends Fragment {

    ImageView profilePic;
    EditText usernameInput;
    EditText phoneInput;
    Button updateProfileBtn;
    ProgressBar progressBar;
    TextView logoutBtn;
    UserModel currentUserModel;
    ActivityResultLauncher<Intent> imagePickLauncher;
    Uri selectedImageUri;

    public ProfileFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        if(data!=null && data.getData()!=null){
                            selectedImageUri = data.getData();
                            AndroidUtil.setProfilePic(getContext(),selectedImageUri,profilePic);
                        }
                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);
        profilePic = view.findViewById(R.id.profile_image_view);
        usernameInput = view.findViewById(R.id.profile_username);
        phoneInput = view.findViewById(R.id.profile_phone);
        updateProfileBtn = view.findViewById(R.id.profle_update_btn);
        progressBar = view.findViewById(R.id.profile_progress_bar);
        logoutBtn = view.findViewById(R.id.logout_btn);

        getUserData();

        updateProfileBtn.setOnClickListener((v -> {
            updateBtnClick();
        }));

        logoutBtn.setOnClickListener((v)->{
            FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        FirebaseUtil.logout();
                        Intent intent = new Intent(getContext(),SplashActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }
            });



        });

        profilePic.setOnClickListener((v)->{
            ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512,512)
                    .createIntent(new Function1<Intent, Unit>() {
                        @Override
                        public Unit invoke(Intent intent) {
                            imagePickLauncher.launch(intent);
                            return null;
                        }
                    });
        });

        return view;
    }

    @OptIn(markerClass = UnstableApi.class)
    void updateBtnClick() {
        String newUsername = usernameInput.getText().toString();
        if (newUsername.isEmpty() || newUsername.length() < 3) {
            usernameInput.setError("Username length should be at least 3 chars");
            return;
        }
        currentUserModel.setUsername(newUsername);
        setInProgress(true);

        if (selectedImageUri != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                    .child("profile_pics")
                    .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid() + ".jpg");

            storageRef.putFile(selectedImageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        storageRef.getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    String downloadUrl = uri.toString();
                                    Log.d("ProfileFragment", "Got download URL: " + downloadUrl);
                                    currentUserModel.setProfilePicUrl(downloadUrl);
                                    updateToFirestore();
                                })
                                .addOnFailureListener(e -> {
                                    setInProgress(false);
                                    Log.e("ProfileFragment", "Failed to get download URL", e);
                                    AndroidUtil.showToast(getContext(), "Failed to get download URL: " + e.getMessage());
                                });
                    })
                    .addOnFailureListener(e -> {
                        setInProgress(false);
                        Log.e("ProfileFragment", "Failed to upload profile picture", e);
                        AndroidUtil.showToast(getContext(), "Failed to upload profile picture: " + e.getMessage());
                    });
        } else {
            Log.d("ProfileFragment", "No image selected, updating Firestore only");
            updateToFirestore();
        }
    }


    @OptIn(markerClass = UnstableApi.class)
    void updateToFirestore() {
        DocumentReference docRef = FirebaseFirestore.getInstance()
                .collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());

        docRef.update("profilePicUrl", currentUserModel.getProfilePicUrl(), "username", currentUserModel.getUsername())
                .addOnSuccessListener(aVoid -> {
                    setInProgress(false);
                    Log.d("ProfileFragment", "DocumentSnapshot successfully updated with URL: " + currentUserModel.getProfilePicUrl());
                    AndroidUtil.showToast(getContext(), "Profile updated successfully");
                })
                .addOnFailureListener(e -> {
                    setInProgress(false);
                    Log.e("ProfileFragment", "Error updating document", e);
                    AndroidUtil.showToast(getContext(), "Update failed: " + e.getMessage());
                });
    }
    @OptIn(markerClass = UnstableApi.class)
    private void uploadImage(Bitmap bitmap) {
        if (bitmap == null) {
            Log.e("ProfileFragment", "Bitmap is null");
            return;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.e("ProfileFragment", "User not authenticated");
            return;
        }

        String path = "profile_pics/" + user.getUid() + ".jpg";
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(path);

        Log.d("ProfileFragment", "Starting upload to " + path);
        UploadTask uploadTask = storageRef.putBytes(data);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            Log.d("ProfileFragment", "Image upload successful, fetching download URL");
            getDownloadUrl(storageRef);
        }).addOnFailureListener(e -> {
            setInProgress(false);
            Log.e("ProfileFragment", "Failed to upload image: " + e.getMessage(), e);
            AndroidUtil.showToast(getContext(), "Failed to upload image: " + e.getMessage());
        });
    }

    @OptIn(markerClass = UnstableApi.class)
    private void getDownloadUrl(StorageReference storageRef) {
        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            String downloadUrl = uri.toString();
            Log.d("ProfileFragment", "Got download URL: " + downloadUrl);
            currentUserModel.setProfilePicUrl(downloadUrl);
            updateToFirestore();
        }).addOnFailureListener(e -> {
            setInProgress(false);
            Log.e("ProfileFragment", "Failed to get download URL: " + e.getMessage(), e);
            AndroidUtil.showToast(getContext(), "Failed to get download URL: " + e.getMessage());
        });
    }


    void getUserData() {
        setInProgress(true);

        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            setInProgress(false);
            if (task.isSuccessful()) {
                currentUserModel = task.getResult().toObject(UserModel.class);
                if (currentUserModel != null) {
                    usernameInput.setText(currentUserModel.getUsername());
                    phoneInput.setText(currentUserModel.getPhone());

                    // Load profile picture using the URL from Firestore
                    if (currentUserModel.getProfilePicUrl() != null && !currentUserModel.getProfilePicUrl().isEmpty()) {
                        Uri uri = Uri.parse(currentUserModel.getProfilePicUrl());
                        AndroidUtil.setProfilePic(getContext(), uri, profilePic);
                    } else {
                        // Optionally set a placeholder image if no profile picture URL is available
                        profilePic.setImageResource(R.drawable.person_icon);
                    }
                } else {
                    AndroidUtil.showToast(getContext(), "User data is null");
                }
            } else {
                AndroidUtil.showToast(getContext(), "Failed to fetch user data: " + Objects.requireNonNull(task.getException()).getMessage());
            }
        });
    }


    void setInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            updateProfileBtn.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            updateProfileBtn.setVisibility(View.VISIBLE);
        }
    }
}