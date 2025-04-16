package com.example.democamerax;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.security.crypto.EncryptedFile;
import androidx.security.crypto.MasterKeys;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class PhotoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        Button showButton = findViewById(R.id.showButton);

        showButton.setOnClickListener(v -> {
            showDecryptedImage();
        });

    }

    private void showDecryptedImage() {
        File encryptedFile = new File(getFilesDir(), "secure_photo.enc");
        File decryptedFile = decryptFile(encryptedFile);

        if (decryptedFile != null) {
            ImageView imageView = findViewById(R.id.imageView);
            imageView.setImageURI(Uri.fromFile(decryptedFile));

            if (decryptedFile.exists()) {
                decryptedFile.delete();
            }
        }
    }

    private File decryptFile(File encryptedFile) {
        try {
            if (!encryptedFile.exists()) {
                Toast.makeText(this, "Tệp mã hóa không tồn tại!", Toast.LENGTH_SHORT).show();
                return null;
            }

            // tạo file giải mã
            File decryptedFile = new File(getFilesDir(), "decrypted_photo.jpg"); // Tạo file mới

            // Nếu file giải mã đã tồn tại, xóa nó trước
            if (decryptedFile.exists()) {
                decryptedFile.delete();
            }

            // tạo đối tượng EncryptedFile để giải mã
            EncryptedFile encFile = new EncryptedFile.Builder(
                    encryptedFile,
                    this,
                    MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
                    EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build();

            // openFileInput để đọc file giải mã và lưu vào file mới
            FileInputStream fis = encFile.openFileInput();
            FileOutputStream fos = new FileOutputStream(decryptedFile);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }

            fis.close();
            fos.close();

            Toast.makeText(this, "Giải mã ảnh thành công!", Toast.LENGTH_SHORT).show();
            return decryptedFile; // Trả về file đã giải mã

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khi giải mã ảnh!", Toast.LENGTH_SHORT).show();
            return null;
        }
    }


}
