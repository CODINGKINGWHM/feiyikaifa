package com.codingkingwhm.feiyikaifa.data.local;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;

import com.codingkingwhm.feiyikaifa.feature.drawing.DrawingDocument;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DraftRepository {

    public static final String STATUS_LOCAL = "LOCAL";

    private final Context context;
    private final DraftDao draftDao;
    private final Gson gson = new Gson();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public DraftRepository(Context context) {
        this.context = context.getApplicationContext();
        this.draftDao = AppDatabase.getInstance(context).draftDao();
    }

    public void saveDraft(
            long existingId,
            String title,
            DrawingDocument document,
            Bitmap preview,
            ResultCallback<Long> callback
    ) {
        executor.execute(() -> {
            try {
                DraftEntity entity = existingId > 0 ? draftDao.findById(existingId) : null;
                long now = System.currentTimeMillis();
                if (entity == null) {
                    entity = new DraftEntity();
                    entity.localId = UUID.randomUUID().toString();
                    entity.createdAt = now;
                }

                File draftDirectory = new File(new File(context.getFilesDir(), "drafts"), entity.localId);
                if (!draftDirectory.exists() && !draftDirectory.mkdirs()) {
                    throw new IllegalStateException("无法创建草稿目录");
                }

                File strokeFile = new File(draftDirectory, "strokes.json");
                try (OutputStreamWriter writer = new OutputStreamWriter(
                        new FileOutputStream(strokeFile),
                        StandardCharsets.UTF_8
                )) {
                    gson.toJson(document, writer);
                }

                File previewFile = new File(draftDirectory, "preview.png");
                try (FileOutputStream outputStream = new FileOutputStream(previewFile)) {
                    if (!preview.compress(Bitmap.CompressFormat.PNG, 100, outputStream)) {
                        throw new IllegalStateException("草稿预览图保存失败");
                    }
                } finally {
                    preview.recycle();
                }

                entity.patternId = document.getPatternId();
                entity.patternName = document.getPatternName();
                entity.guideKey = document.getGuideKey();
                entity.title = title;
                entity.previewPath = previewFile.getAbsolutePath();
                entity.strokeFilePath = strokeFile.getAbsolutePath();
                entity.syncStatus = STATUS_LOCAL;
                entity.updatedAt = now;

                long savedId = draftDao.save(entity);
                postSuccess(callback, savedId);
            } catch (Throwable error) {
                if (!preview.isRecycled()) {
                    preview.recycle();
                }
                postError(callback, error);
            }
        });
    }

    public void loadDraft(long id, ResultCallback<DraftData> callback) {
        executor.execute(() -> {
            try {
                DraftEntity entity = draftDao.findById(id);
                if (entity == null) {
                    throw new IllegalArgumentException("草稿不存在");
                }
                DrawingDocument document;
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                        new FileInputStream(entity.strokeFilePath),
                        StandardCharsets.UTF_8
                ))) {
                    document = gson.fromJson(reader, DrawingDocument.class);
                }
                postSuccess(callback, new DraftData(entity, document));
            } catch (Throwable error) {
                postError(callback, error);
            }
        });
    }

    public void loadAll(ResultCallback<List<DraftEntity>> callback) {
        executor.execute(() -> {
            try {
                postSuccess(callback, draftDao.findAll());
            } catch (Throwable error) {
                postError(callback, error);
            }
        });
    }

    public void delete(DraftEntity entity, ResultCallback<Boolean> callback) {
        executor.execute(() -> {
            try {
                draftDao.delete(entity);
                deleteFileIfPresent(entity.previewPath);
                deleteFileIfPresent(entity.strokeFilePath);
                File parent = entity.previewPath == null ? null : new File(entity.previewPath).getParentFile();
                if (parent != null && parent.isDirectory()) {
                    File[] remaining = parent.listFiles();
                    if (remaining != null && remaining.length == 0) {
                        parent.delete();
                    }
                }
                postSuccess(callback, true);
            } catch (Throwable error) {
                postError(callback, error);
            }
        });
    }

    private static void deleteFileIfPresent(String path) {
        if (path != null) {
            File file = new File(path);
            if (file.isFile()) {
                file.delete();
            }
        }
    }

    private <T> void postSuccess(ResultCallback<T> callback, T result) {
        mainHandler.post(() -> callback.onSuccess(result));
    }

    private <T> void postError(ResultCallback<T> callback, Throwable error) {
        mainHandler.post(() -> callback.onError(error));
    }
}
