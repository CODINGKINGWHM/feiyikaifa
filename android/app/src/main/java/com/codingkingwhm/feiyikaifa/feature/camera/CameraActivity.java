package com.codingkingwhm.feiyikaifa.feature.camera;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Surface;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.codingkingwhm.feiyikaifa.R;
import com.codingkingwhm.feiyikaifa.common.SystemBarInsets;
import com.codingkingwhm.feiyikaifa.data.local.LocalImageStore;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CameraActivity extends AppCompatActivity {
    public static final String EXTRA_IMAGE_URI = "image_uri";
    private PreviewView previewView;
    private ImageCapture imageCapture;
    private ProcessCameraProvider provider;
    private int lensFacing = CameraSelector.LENS_FACING_BACK;
    private final ExecutorService cameraExecutor = Executors.newSingleThreadExecutor();
    private final ActivityResultLauncher<String> permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) startCamera();
                else { Toast.makeText(this, "需要相机权限才能拍摄舞台背景", Toast.LENGTH_LONG).show(); finish(); }
            });

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        SystemBarInsets.apply(getWindow(), findViewById(R.id.root));
        previewView = findViewById(R.id.previewView);
        findViewById(R.id.buttonBack).setOnClickListener(v -> finish());
        findViewById(R.id.buttonCapture).setOnClickListener(v -> capture());
        findViewById(R.id.buttonFlip).setOnClickListener(v -> { lensFacing = lensFacing == CameraSelector.LENS_FACING_BACK ? CameraSelector.LENS_FACING_FRONT : CameraSelector.LENS_FACING_BACK; startCamera(); });
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) startCamera();
        else permissionLauncher.launch(Manifest.permission.CAMERA);
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> future = ProcessCameraProvider.getInstance(this);
        future.addListener(() -> {
            try {
                provider = future.get();
                Preview preview = new Preview.Builder().build();
                imageCapture = new ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).build();
                CameraSelector selector = new CameraSelector.Builder().requireLensFacing(lensFacing).build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());
                provider.unbindAll();
                provider.bindToLifecycle(this, selector, preview, imageCapture);
            } catch (Throwable error) {
                Toast.makeText(this, "相机启动失败：" + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void capture() {
        if (imageCapture == null) return;
        File output;
        try { output = LocalImageStore.createBackgroundFile(this); }
        catch (Throwable error) { Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show(); return; }
        imageCapture.setTargetRotation(previewView.getDisplay() == null ? Surface.ROTATION_0 : previewView.getDisplay().getRotation());
        ImageCapture.OutputFileOptions options = new ImageCapture.OutputFileOptions.Builder(output).build();
        imageCapture.takePicture(options, cameraExecutor, new ImageCapture.OnImageSavedCallback() {
            @Override public void onImageSaved(ImageCapture.OutputFileResults results) {
                runOnUiThread(() -> {
                    Intent data = new Intent();
                    data.putExtra(EXTRA_IMAGE_URI, Uri.fromFile(output).toString());
                    setResult(RESULT_OK, data); finish();
                });
            }
            @Override public void onError(ImageCaptureException error) {
                runOnUiThread(() -> Toast.makeText(CameraActivity.this, "拍摄失败：" + error.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }

    @Override protected void onDestroy() { cameraExecutor.shutdown(); super.onDestroy(); }
}
