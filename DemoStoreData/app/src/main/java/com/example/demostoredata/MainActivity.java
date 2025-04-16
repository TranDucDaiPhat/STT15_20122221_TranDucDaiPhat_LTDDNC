package com.example.demostoredata;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedFile;
import androidx.security.crypto.MasterKeys;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_STORAGE_PERMISSION = 1;
    private Button btnSave;
    private EditText editText;
    private Button btnRead;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSave = findViewById(R.id.btnSave);
        editText = findViewById((R.id.editText));
        btnRead = findViewById(R.id.btnRead);
        textView = findViewById(R.id.textView);

        btnSave.setOnClickListener(v -> {
            String text = editText.getText().toString();
            encryptAndSaveToFile("secure_text.txt", text);
        });

        btnRead.setOnClickListener(v -> {
            String text = readEncryptedFile("secure_text.txt");
            textView.setText(text);
        });

        // yêu cầu quyền truy cập bộ nhớ
        checkAndRequestPermissions();
    }

    private void encryptAndSaveToFile(String filename, String content) {
        try {
            // Tạo khóa mã hóa AES256_GCM
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);

            File file = new File(getFilesDir(), filename);

            if (file.exists()) {
                file.delete();
            }

            EncryptedFile encryptedFile = new EncryptedFile.Builder(
                    file,
                    this,
                    masterKeyAlias,
                    EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build();

            // Ghi nội dung vào file đã mã hóa
            FileOutputStream fos = encryptedFile.openFileOutput();
            fos.write(content.getBytes(StandardCharsets.UTF_8));
            fos.close();

            Toast.makeText(this, "Lưu thành công!", Toast.LENGTH_LONG).show();
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khi lưu file!", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveToFile(String filename, String content) {
        try {
            // .../data/data/<package_name>/files/
            FileOutputStream fos = openFileOutput(filename, MODE_PRIVATE);
            fos.write(content.getBytes());
            fos.close();
            Toast.makeText(this, "Lưu thành công!", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khi lưu file", Toast.LENGTH_SHORT).show();
        }
    }

    private String readEncryptedFile(String filename) {
        StringBuilder content = new StringBuilder();
        try {
            // Tạo khóa giải mã
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            File file = new File(getFilesDir(), filename);
            EncryptedFile encryptedFile = new EncryptedFile.Builder(
                    file,
                    this,
                    masterKeyAlias,
                    EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build();

            // Đọc nội dung file
            InputStream inputStream = encryptedFile.openFileInput();

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            return "Lỗi khi đọc file!";
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
        return content.toString();
    }

    private String readFromAppInternalStorage(String filename) {
        StringBuilder content = new StringBuilder();
        try {
            FileInputStream fis = openFileInput(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            reader.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
            return "Lỗi khi đọc file!";
        }
        return content.toString();
    }


    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Quyền truy cập bộ nhớ đã được cấp!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Quyền truy cập bộ nhớ bị từ chối!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}