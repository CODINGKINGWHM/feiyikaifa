package com.codingkingwhm.feiyikaifa.feature.poster;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.codingkingwhm.feiyikaifa.R;
import com.codingkingwhm.feiyikaifa.common.SystemBarInsets;
import com.codingkingwhm.feiyikaifa.data.local.ImageExporter;
import com.codingkingwhm.feiyikaifa.data.local.LocalImageStore;
import com.codingkingwhm.feiyikaifa.data.local.ResultCallback;
import com.codingkingwhm.feiyikaifa.feature.camera.CameraActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PosterActivity extends AppCompatActivity {
    public static final String EXTRA_PUPPET_VARIANT = "puppet_variant";
    private final ExecutorService imageExecutor = Executors.newSingleThreadExecutor();
    private PosterCanvasView canvas;
    private String lastExportUri;
    private final ActivityResultLauncher<String> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(), uri -> { if (uri != null) importGallery(uri); });
    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String uri = result.getData().getStringExtra(CameraActivity.EXTRA_IMAGE_URI);
                    if (uri != null) loadBackground(uri);
                }
            });

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poster);
        SystemBarInsets.apply(getWindow(), findViewById(R.id.root));
        canvas = findViewById(R.id.posterCanvas);
        canvas.setPuppetVariant(getIntent().getIntExtra(EXTRA_PUPPET_VARIANT, 0));
        lastExportUri = getSharedPreferences("works", MODE_PRIVATE).getString("last_poster_uri", null);
        findViewById(R.id.buttonBack).setOnClickListener(v -> finish());
        findViewById(R.id.buttonBackground).setOnClickListener(v -> chooseBackground());
        findViewById(R.id.buttonPuppet).setOnClickListener(v -> canvas.setPuppetVariant(canvas.getPuppetVariant() + 1));
        findViewById(R.id.buttonFrame).setOnClickListener(v -> canvas.setFrameVariant(canvas.getFrameVariant() + 1));
        findViewById(R.id.buttonText).setOnClickListener(v -> editCaption());
        findViewById(R.id.buttonExport).setOnClickListener(v -> export());
    }

    private void chooseBackground() {
        new AlertDialog.Builder(this)
                .setTitle("选择舞台背景")
                .setItems(new String[]{"使用相机拍摄", "从系统相册选择"}, (dialog, which) -> {
                    if (which == 0) cameraLauncher.launch(new Intent(this, CameraActivity.class));
                    else galleryLauncher.launch("image/*");
                })
                .show();
    }

    private void importGallery(Uri uri) {
        Toast.makeText(this, "正在处理照片…", Toast.LENGTH_SHORT).show();
        imageExecutor.execute(() -> {
            try { String stored = LocalImageStore.copyIntoApp(this, uri); runOnUiThread(() -> loadBackground(stored)); }
            catch (Throwable error) { runOnUiThread(() -> Toast.makeText(this, "照片导入失败：" + error.getMessage(), Toast.LENGTH_LONG).show()); }
        });
    }

    private void loadBackground(String uri) {
        imageExecutor.execute(() -> {
            try { Bitmap bitmap = LocalImageStore.decode(this, uri, 2048); runOnUiThread(() -> { canvas.setBackgroundBitmap(bitmap); Toast.makeText(this, "背景已放入舞台", Toast.LENGTH_SHORT).show(); }); }
            catch (Throwable error) { runOnUiThread(() -> Toast.makeText(this, "照片读取失败：" + error.getMessage(), Toast.LENGTH_LONG).show()); }
        });
    }

    private void editCaption() {
        EditText input = new EditText(this); input.setInputType(InputType.TYPE_CLASS_TEXT); input.setText(canvas.getCaption()); input.setSelectAllOnFocus(true);
        int pad = Math.round(22 * getResources().getDisplayMetrics().density); input.setPadding(pad,0,pad,0);
        new AlertDialog.Builder(this).setTitle("为作品题字").setView(input).setNegativeButton("取消",null)
                .setPositiveButton("完成",(d,w)->canvas.setCaption(input.getText().toString())).show();
    }

    private void export() {
        Bitmap bitmap = canvas.renderBitmap();
        Toast.makeText(this, "正在生成高清海报…", Toast.LENGTH_SHORT).show();
        ImageExporter.saveToGallery(this, bitmap, "影间_" + canvas.getCaption(), new ResultCallback<String>() {
            @Override public void onSuccess(String result) {
                lastExportUri = result;
                getSharedPreferences("works", MODE_PRIVATE).edit().putString("last_poster_uri", result).putString("last_poster_title", canvas.getCaption()).apply();
                Toast.makeText(PosterActivity.this, "作品已保存到系统相册", Toast.LENGTH_LONG).show();
            }
            @Override public void onError(Throwable error) { Toast.makeText(PosterActivity.this, "导出失败：" + error.getMessage(), Toast.LENGTH_LONG).show(); }
        });
    }

    private void share() {
        if (lastExportUri == null) { Toast.makeText(this, "请先导出作品，再点击分享", Toast.LENGTH_SHORT).show(); return; }
        Intent share = new Intent(Intent.ACTION_SEND); share.setType("image/png"); share.putExtra(Intent.EXTRA_STREAM, Uri.parse(lastExportUri)); share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(share, "分享皮影作品"));
    }

    @Override protected void onDestroy() { imageExecutor.shutdown(); super.onDestroy(); }
}
