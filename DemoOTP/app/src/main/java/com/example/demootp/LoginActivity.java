package com.example.demootp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hbb20.CountryCodePicker;

public class LoginActivity extends AppCompatActivity {

    Button loginBtn;
    Button signupBtn;
    EditText phoneInput;
    EditText passwordInput;
    CountryCodePicker countryCodePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        countryCodePicker = findViewById(R.id.login_countrycode);
        loginBtn = findViewById(R.id.login_btn);
        signupBtn = findViewById(R.id.signup_btn);
        phoneInput = findViewById(R.id.phone);
        passwordInput = findViewById(R.id.password);
        countryCodePicker.registerCarrierNumberEditText(phoneInput);
        phoneInput.setText("0387867308");
        passwordInput.setText("123456");

        signupBtn.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, LoginPhoneNumberActivity.class));
        });

        loginBtn.setOnClickListener(v -> {
            String phone = countryCodePicker.getFullNumberWithPlus();
            String password = passwordInput.getText().toString().trim();

            if (!phone.isEmpty() && !password.isEmpty()) {
                loginUser(phone, password);
            } else {
                Toast.makeText(LoginActivity.this, "Please enter username and password!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginUser(String phone, String password) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .whereEqualTo("phone", phone)  // Tìm user theo tên
                .whereEqualTo("password", password) // So khớp mật khẩu
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            // Đăng nhập thành công
                            Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            // Sai username hoặc password
                            Toast.makeText(LoginActivity.this, "Invalid username or password!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Lỗi truy vấn Firestore
                        Log.e("FirestoreError", "Error: " + task.getException());
                        Toast.makeText(LoginActivity.this, "Login failed, try again!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}