package com.codingkingwhm.feiyikaifa.feature.drawing;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.codingkingwhm.feiyikaifa.R;
import com.codingkingwhm.feiyikaifa.common.SystemBarInsets;
import com.codingkingwhm.feiyikaifa.data.local.DraftData;
import com.codingkingwhm.feiyikaifa.data.local.DraftRepository;
import com.codingkingwhm.feiyikaifa.data.local.ImageExporter;
import com.codingkingwhm.feiyikaifa.data.local.ResultCallback;
import com.codingkingwhm.feiyikaifa.data.model.PatternCatalog;
import com.codingkingwhm.feiyikaifa.data.model.PatternItem;

public class DrawingActivity extends AppCompatActivity {

    public static final String EXTRA_PATTERN_ID = "pattern_id";
    public static final String EXTRA_DRAFT_ID = "draft_id";

    private static final int REQUEST_WRITE_STORAGE = 1101;

    private PatternDrawingView drawingView;
    private DraftRepository repository;
    private PatternItem currentPattern;
    private long draftId;
    private String draftTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);
        SystemBarInsets.apply(getWindow(), findViewById(R.id.root));

        repository = new DraftRepository(this);
        drawingView = findViewById(R.id.drawingView);
        bindControls();

        draftId = getIntent().getLongExtra(EXTRA_DRAFT_ID, 0L);
        if (draftId > 0) {
            loadDraft(draftId);
        } else {
            long patternId = getIntent().getLongExtra(EXTRA_PATTERN_ID, PatternCatalog.DEFAULT_PATTERN_ID);
            configurePattern(PatternCatalog.findById(patternId));
        }
    }

    private void bindControls() {
        findViewById(R.id.buttonBack).setOnClickListener(view -> handleBack());
        findViewById(R.id.buttonBlack).setOnClickListener(view -> drawingView.usePen(Color.BLACK));
        findViewById(R.id.buttonRed).setOnClickListener(view -> drawingView.usePen(Color.rgb(169, 47, 47)));
        findViewById(R.id.buttonBlue).setOnClickListener(view -> drawingView.usePen(Color.rgb(31, 78, 121)));
        findViewById(R.id.buttonEraser).setOnClickListener(view -> drawingView.useEraser());
        findViewById(R.id.buttonUndo).setOnClickListener(view -> drawingView.undo());
        findViewById(R.id.buttonRedo).setOnClickListener(view -> drawingView.redo());
        findViewById(R.id.buttonClear).setOnClickListener(view -> confirmClear());
        findViewById(R.id.buttonSave).setOnClickListener(view -> showSaveDialog());
        findViewById(R.id.buttonExport).setOnClickListener(view -> exportWithPermissionCheck());

        CheckBox checkGuide = findViewById(R.id.checkGuide);
        checkGuide.setOnCheckedChangeListener((button, checked) -> drawingView.setGuideVisible(checked));

        SeekBar guideAlpha = findViewById(R.id.seekGuideAlpha);
        guideAlpha.setOnSeekBarChangeListener(new SimpleSeekBarListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                drawingView.setGuideAlpha(progress / 100f);
            }
        });

        SeekBar strokeWidth = findViewById(R.id.seekStrokeWidth);
        strokeWidth.setOnSeekBarChangeListener(new SimpleSeekBarListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                drawingView.setStrokeWidth(progress + 2f);
            }
        });
    }

    private void configurePattern(PatternItem pattern) {
        currentPattern = pattern;
        drawingView.setGuideResource(pattern.getDrawableResId());
        TextView title = findViewById(R.id.textPatternTitle);
        title.setText(pattern.getName());
        if (draftTitle == null) {
            draftTitle = pattern.getName() + "临摹";
        }
    }

    private void loadDraft(long id) {
        repository.loadDraft(id, new ResultCallback<DraftData>() {
            @Override
            public void onSuccess(DraftData result) {
                currentPattern = PatternCatalog.findByGuideKey(result.getEntity().guideKey);
                draftTitle = result.getEntity().title;
                configurePattern(currentPattern);
                drawingView.restoreDocument(result.getDocument());
                ((SeekBar) findViewById(R.id.seekGuideAlpha)).setProgress(
                        Math.round(result.getDocument().getGuideAlpha() * 100)
                );
            }

            @Override
            public void onError(Throwable error) {
                showError("读取草稿失败", error);
                finish();
            }
        });
    }

    private void showSaveDialog() {
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint(R.string.draft_title_hint);
        input.setText(draftTitle);
        input.setSelectAllOnFocus(true);
        int horizontalPadding = Math.round(24 * getResources().getDisplayMetrics().density);
        input.setPadding(horizontalPadding, 0, horizontalPadding, 0);

        new AlertDialog.Builder(this)
                .setTitle(R.string.save_draft)
                .setView(input)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.confirm, (dialog, which) -> {
                    String title = input.getText().toString().trim();
                    if (title.isEmpty()) {
                        title = currentPattern.getName() + "临摹";
                    }
                    saveDraft(title);
                })
                .show();
    }

    private void saveDraft(String title) {
        draftTitle = title;
        DrawingDocument document = drawingView.createDocument(
                currentPattern.getId(),
                currentPattern.getName(),
                currentPattern.getGuideKey()
        );
        Bitmap preview = drawingView.exportBitmap(true);
        repository.saveDraft(draftId, title, document, preview, new ResultCallback<Long>() {
            @Override
            public void onSuccess(Long savedId) {
                draftId = savedId;
                drawingView.markSaved();
                Toast.makeText(DrawingActivity.this, "草稿已保存", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable error) {
                showError("保存草稿失败", error);
            }
        });
    }

    private void exportWithPermissionCheck() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE
            );
            return;
        }
        exportImage();
    }

    private void exportImage() {
        Bitmap bitmap = drawingView.exportBitmap(true);
        String name = draftTitle == null ? currentPattern.getName() : draftTitle;
        ImageExporter.saveToGallery(this, bitmap, name, new ResultCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Toast.makeText(DrawingActivity.this, "已导出到相册 FeiyiKaifa", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(Throwable error) {
                showError("导出失败", error);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_STORAGE
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            exportImage();
        }
    }

    private void confirmClear() {
        new AlertDialog.Builder(this)
                .setMessage("确定清空全部用户笔迹吗？底图不会被删除。")
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.clear, (dialog, which) -> drawingView.clearDrawing())
                .show();
    }

    private void handleBack() {
        if (!drawingView.isDirty()) {
            finish();
            return;
        }
        new AlertDialog.Builder(this)
                .setMessage("当前修改还没有保存，确定退出吗？")
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton("退出", (dialog, which) -> finish())
                .show();
    }

    @Override
    public void onBackPressed() {
        handleBack();
    }

    private void showError(String prefix, Throwable error) {
        String detail = error.getMessage() == null ? error.getClass().getSimpleName() : error.getMessage();
        Toast.makeText(this, prefix + "：" + detail, Toast.LENGTH_LONG).show();
    }

    private abstract static class SimpleSeekBarListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    }
}
