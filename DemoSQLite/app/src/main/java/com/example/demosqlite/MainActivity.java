package com.example.demosqlite;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;

import java.util.ArrayList;

import dao.UserAdapter;
import dao.UserDatabaseAdapter;
import model.UserModel;
import utils.Utils;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private ArrayList<UserModel> userList;
    private Button btnAdd, btnEdit, btnDelete;
    private static final int REQUEST_CODE_ADD = 1;
    private static final int REQUEST_CODE_UPDATE = 2;
    private UserDatabaseAdapter userDatabaseAdapter;
    private UserModel currentUser = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        btnAdd = findViewById(R.id.btnAdd);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);

        userDatabaseAdapter = new UserDatabaseAdapter(this);

        // Lấy danh sách user từ database
        loadUsersFromDatabase();

        // Set RecyclerView Adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userAdapter = new UserAdapter(userList);
        recyclerView.setAdapter(userAdapter);

        // Mở màn hình AddUserActivity khi nhấn nút Add
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddUserActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ADD);
        });

        btnEdit.setOnClickListener(v -> {
            if (currentUser != null) {
                // Tạo intent để gửi dữ liệu về MainActivity
                Intent intent = new Intent(MainActivity.this, UpdateUserActivity.class);
                intent.putExtra("USER_ID", currentUser.getId());
                intent.putExtra("USER_NAME", currentUser.getUserName());
                intent.putExtra("USER_PHONE", currentUser.getPhone());
                intent.putExtra("USER_EMAIL", currentUser.getEmail());
                startActivityForResult(intent, REQUEST_CODE_UPDATE);
            }
        });

        btnDelete.setOnClickListener(v -> {
            if (currentUser != null) {
                new AlertDialog.Builder(this)
                        .setTitle("Xác nhận xoá")
                        .setMessage("Bạn có chắc muốn xoá người dùng này không?")
                        .setPositiveButton("Xoá", (dialog, which) -> deleteUser()) // Nếu chọn Xoá
                        .setNegativeButton("Huỷ", (dialog, which) -> dialog.dismiss()) // Nếu chọn Huỷ
                        .show();
            }
        });

        // Bắt sự kiện click vào user
        userAdapter.setOnItemClickListener(user -> {
            currentUser = user;
        });
    }

    private void deleteUser() {
        UserDatabaseAdapter usersDB = new UserDatabaseAdapter(getApplicationContext());
        int rowDeleted =  usersDB.deleteEntry(currentUser.getId());
        if (rowDeleted > 0) {
            Utils.showToast(MainActivity.this, "Xoá thành công " + rowDeleted + " user");
            userList.remove(currentUser);
            refreshUserList();
        } else {
            Utils.showToast(MainActivity.this, "Xoá thất bại!");
        }
    }

    private void loadUsersFromDatabase() {
        try {
            userList = UserDatabaseAdapter.getRows();
        } catch (JSONException e) {
            Log.e("Database Error", "Error loading users", e);
        }
    }

    // Nhận dữ liệu từ AddUserActivity và cập nhật danh sách
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_ADD -> {
                if (resultCode == RESULT_OK) {
                    userList.clear(); // Xoá toàn bộ danh sách cũ
                    loadUsersFromDatabase();
                    refreshUserList();
                }
            }
            case REQUEST_CODE_UPDATE -> {
                if (resultCode == RESULT_OK && data != null) {
                    userList.remove(currentUser);
                    UserModel newUser = new UserModel();
                    newUser.setId(data.getStringExtra("USER_ID"));
                    newUser.setUserName(data.getStringExtra("USER_NAME"));
                    newUser.setPhone(data.getStringExtra("USER_PHONE"));
                    newUser.setEmail(data.getStringExtra("USER_EMAIL"));
                    userList.add(newUser);
                    refreshUserList();
                }
            }
        }
    }

    private void refreshUserList() {
        currentUser = null; // Reset user được chọn
        userAdapter.setSelectedPosition(-1); // Reset trạng thái chọn
        userAdapter.notifyDataSetChanged(); // Cập nhật RecyclerView
    }
}