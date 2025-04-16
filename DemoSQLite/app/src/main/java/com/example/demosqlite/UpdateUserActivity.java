package com.example.demosqlite;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import dao.UserDatabaseAdapter;
import utils.Utils;

public class UpdateUserActivity extends AppCompatActivity {

    private EditText etUserName, etPhone, etEmail;
    private Button btnUpdate;
    private String idUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user);

        etUserName = findViewById(R.id.etUserName);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        btnUpdate = findViewById(R.id.btnSave);

        idUser = getIntent().getStringExtra("USER_ID");
        etPhone.setText(getIntent().getStringExtra("USER_PHONE"));
        etEmail.setText(getIntent().getStringExtra("USER_EMAIL"));
        etUserName.setText(getIntent().getStringExtra("USER_NAME"));


        btnUpdate.setOnClickListener(v -> {
            String userName = etUserName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            if (!userName.isEmpty() && !phone.isEmpty() && !email.isEmpty()) {
                new AlertDialog.Builder(this)
                        .setTitle("Xác nhận cập nhật")
                        .setMessage("Bạn có chắc muốn cập nhật người dùng này không?")
                        .setPositiveButton("Cập nhật", (dialog, which) -> updateUser(userName, phone, email)) // Nếu chọn Xoá
                        .setNegativeButton("Huỷ", (dialog, which) -> dialog.dismiss()) // Nếu chọn Huỷ
                        .show();
            } else {
                Utils.showToast(UpdateUserActivity.this, "Please fill all fields");
            }
        });
    }

    private void updateUser(String name, String phone, String email) {
        UserDatabaseAdapter usersDB = new UserDatabaseAdapter(getApplicationContext());
        boolean rowUpdated =  usersDB.updateEntry(idUser, name, phone, email);
        if (rowUpdated) {
            Intent intent = new Intent();
            intent.putExtra("USER_ID", idUser);
            intent.putExtra("USER_NAME", name);
            intent.putExtra("USER_PHONE", phone);
            intent.putExtra("USER_EMAIL", email);
            setResult(RESULT_OK, intent);
            finish(); // Quay lại MainActivity
        }
    }
}