package com.example.gninecs460;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gninecs460.model.UserModel;
import com.example.gninecs460.utils.AndroidUtil;
import com.example.gninecs460.utils.FirebaseUtil;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if(getIntent().getExtras()!=null){
            //from notification
            String userId = getIntent().getExtras().getString("userId");
            assert userId != null;
            FirebaseUtil.allUserCollectionReference().document(userId).get()
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            UserModel model = task.getResult().toObject(UserModel.class);

                            Intent mainIntent = new Intent(this,MainActivity.class);
                            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(mainIntent);

                            Intent intent = new Intent(this, ChatActivity.class);
                            assert model != null;
                            AndroidUtil.passUserModelAsIntent(intent,model);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    });


            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(FirebaseUtil.isLoggedIn()){
                            startActivity(new Intent(SplashActivity.this,MainActivity.class));
                        }else{
                            startActivity(new Intent(SplashActivity.this,LoginPhoneNumberActivity.class));
                        }
                        finish();
                    }
                },3000);
            }
        }
    }