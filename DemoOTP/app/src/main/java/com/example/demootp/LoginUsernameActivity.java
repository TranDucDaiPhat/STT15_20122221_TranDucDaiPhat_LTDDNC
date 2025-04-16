package com.example.demootp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.demootp.model.UserModel;
import com.example.demootp.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

public class LoginUsernameActivity extends AppCompatActivity {

    EditText userNameInput;
    Button letMeInBtn;
    ProgressBar progressBar;
    String phoneNumber;
    EditText passwordInput;
    UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_info);

        userNameInput = findViewById(R.id.login_username);
        letMeInBtn = findViewById(R.id.login_let_me_in_btn);
        progressBar = findViewById(R.id.login_progress_bar);
        passwordInput = findViewById(R.id.password);

        userNameInput.setText("phat");
        passwordInput.setText("123456");

        phoneNumber = getIntent().getExtras().getString("phone");

        getUsername();

        // create account
        letMeInBtn.setOnClickListener(v -> {
            setUserName();
        });
    }

    void setUserName() {
        String username = userNameInput.getText().toString();
        String password = passwordInput.getText().toString();
        if (username.isEmpty() || username.length() < 3) {
            userNameInput.setError("Username length should be at least 3 chars");
            return;
        }
        if (password.isEmpty() || password.length() < 6) {
            passwordInput.setError("password length should be at least 6 chars");
            return;
        }
        setInProgress(true);
        if (userModel != null) {
            userModel.setUsername(username);
        } else {
            userModel = new UserModel(phoneNumber,username, password, Timestamp.now());
        }

        FirebaseUtil.currentUserDetails().set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    setInProgress(false);
                    Intent intent = new Intent(LoginUsernameActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    Log.e("FirestoreError", "Failed to save user: " + task.getException());
                }
            }
        }).addOnFailureListener(e -> Log.e("FirestoreError", "Error: " + e.getMessage()));;
    }

    void getUsername() {
        setInProgress(true);
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                setInProgress(false);;
                if (task.isSuccessful()) {
                    userModel = task.getResult().toObject(UserModel.class);
                    if (userModel != null) {
                        userNameInput.setText(userModel.getUsername());
                    }
                }

            }
        });
    }

    void setInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            letMeInBtn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            letMeInBtn.setVisibility(View.VISIBLE);
        }
    }
}