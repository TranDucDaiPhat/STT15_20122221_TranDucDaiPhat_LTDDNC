package com.example.demosqlite;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

import dao.UserDatabaseAdapter;
import utils.Utils;

public class AddUserActivity extends AppCompatActivity {

    private EditText etUserName, etPhone, etEmail;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        etUserName = findViewById(R.id.etUserName);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        btnSave = findViewById(R.id.btnSave);

        btnSave.setOnClickListener(v -> {
            String userName = etUserName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String email = etEmail.getText().toString().trim();

            if (!userName.isEmpty() && !phone.isEmpty() && !email.isEmpty()) {

                UserDatabaseAdapter usersDB = new UserDatabaseAdapter(getApplicationContext());
                boolean isSucces = usersDB.insertEntry(userName, phone, email);
                if (isSucces) {
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish(); // Quay lại MainActivity
                } else {
                    Utils.showToast(AddUserActivity.this, "Thêm user thất bại");
                }

            } else {
                Utils.showToast(AddUserActivity.this, "Please fill all fields");
            }
        });
    }
}
