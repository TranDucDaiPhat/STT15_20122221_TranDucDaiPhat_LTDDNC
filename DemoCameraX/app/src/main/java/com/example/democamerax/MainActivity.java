package com.example.democamerax;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.security.crypto.EncryptedFile;
import androidx.security.crypto.MasterKeys;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {
    // 100 là một số ngẫu nhiên được đặt ra để tránh trùng lặp
    // với các yêu cầu khác nếu ứng dụng có nhiều yêu cầu
    private static final int CAMERA_REQUEST_CODE = 100;
    private PreviewView viewFinder;
    private Button captureButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewFinder = findViewById(R.id.viewFinder);
        captureButton = findViewById(R.id.captureButton);

        captureButton.setOnClickListener(v -> {
            requestPermissions();
        });
    }

    private void startCamera() {

        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                // ProcessCameraProvider là thành phần quản lý vòng đời của camera.
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                // tạo một Preview (chế độ xem trước hình ảnh camera).
                Preview preview = new Preview.Builder().build();
                // gán preview cho viewFinder để hiện lên giao diện
                preview.setSurfaceProvider(viewFinder.getSurfaceProvider());
                // tạo một đối tượng ImageCapture dùng để chụp ảnh.
                ImageCapture imageCapture = new ImageCapture.Builder().build();

                // tạo camera và chọn camera sau
                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();

                // Gán sự kiện chụp ảnh cho nút captureButton
                // Khi người dùng bấm nút captureButton, hàm takePhoto(imageCapture) sẽ được gọi
                captureButton.setOnClickListener(v -> takePhoto(imageCapture));

                // giải phóng bất kỳ camera nào đang được sử dụng trước đó
                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);


            } catch (Exception e) {
                e.printStackTrace();
            }
            // ContextCompat.getMainExecutor(this) đảm bảo đoạn code chạy trên luồng chính (UI thread).
        }, ContextCompat.getMainExecutor(this));
    }

    private void takePhoto(ImageCapture imageCapture) {
        // getFilesDir() tạo ra đường dẫn và lưu ở thư mục data/data
        File file = new File(getFilesDir(), "secure_photo.jpg");

        ImageCapture.OutputFileOptions outputFileOptions =
                new ImageCapture.OutputFileOptions.Builder(file).build();

        imageCapture.takePicture(outputFileOptions,
                ContextCompat.getMainExecutor(this), new ImageCapture.OnImageSavedCallback() {
                    // Xử lý khi ảnh được lưu thành công
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        encryptFile(file);
                        Toast.makeText(MainActivity.this, "Ảnh đã lưu bảo mật", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(MainActivity.this, PhotoActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Toast.makeText(MainActivity.this, "Lỗi khi chụp ảnh", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Hàm mã hoá ảnh bằng thuật toán AES-256
    private void encryptFile(File originalFile) {
        try {
            File encryptedFile = new File(getFilesDir(), "secure_photo.enc"); // Tạo file mã hóa

            // Nếu tệp mã hóa đã tồn tại, nó sẽ bị xóa trước để tránh lỗi ghi đè
            if (encryptedFile.exists()) {
                encryptedFile.delete();
            }

            // Khởi tạo EncryptedFile để mã hóa dữ liệu
            EncryptedFile encFile = new EncryptedFile.Builder(
                    encryptedFile,
                    this,
                    MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
                    EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build();

            // Đọc ảnh gốc và ghi vào file mã hóa
            FileInputStream fis = new FileInputStream(originalFile);
            FileOutputStream fos = encFile.openFileOutput();

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }

            fis.close();
            fos.close();

            // Xóa file gốc sau khi mã hóa
            if (originalFile.exists()) {
                originalFile.delete();
            }

            Toast.makeText(this, "Ảnh đã mã hóa thành công!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khi mã hóa ảnh!", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestPermissions() {
        // import android.Manifest;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        } else {
            startCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "Quyền Camera bị từ chối!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}