package com.codingkingwhm.feiyikaifa.data.local;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ImageExporter {

    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    private ImageExporter() {
    }

    public static void saveToGallery(
            Context context,
            Bitmap bitmap,
            String baseName,
            ResultCallback<String> callback
    ) {
        Context appContext = context.getApplicationContext();
        EXECUTOR.execute(() -> {
            try {
                String fileName = sanitize(baseName) + "_" + System.currentTimeMillis() + ".png";
                String result;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    result = saveWithMediaStore(appContext, bitmap, fileName);
                } else {
                    result = saveLegacy(appContext, bitmap, fileName);
                }
                bitmap.recycle();
                new HandlerCallback().success(callback, result);
            } catch (Throwable error) {
                if (!bitmap.isRecycled()) {
                    bitmap.recycle();
                }
                new HandlerCallback().error(callback, error);
            }
        });
    }

    private static String saveWithMediaStore(Context context, Bitmap bitmap, String fileName) throws Exception {
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/FeiyiKaifa");
        values.put(MediaStore.Images.Media.IS_PENDING, 1);

        Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        if (uri == null) {
            throw new IllegalStateException("无法创建相册文件");
        }
        try {
            try (OutputStream outputStream = resolver.openOutputStream(uri)) {
                if (outputStream == null || !bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)) {
                    throw new IllegalStateException("图片写入失败");
                }
            }
            values.clear();
            values.put(MediaStore.Images.Media.IS_PENDING, 0);
            resolver.update(uri, values, null, null);
            return uri.toString();
        } catch (Throwable error) {
            resolver.delete(uri, null, null);
            throw error;
        }
    }

    @SuppressWarnings("deprecation")
    private static String saveLegacy(Context context, Bitmap bitmap, String fileName) throws Exception {
        File pictures = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File directory = new File(pictures, "FeiyiKaifa");
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IllegalStateException("无法创建相册目录");
        }
        File output = new File(directory, fileName);
        try (FileOutputStream stream = new FileOutputStream(output)) {
            if (!bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)) {
                throw new IllegalStateException("图片写入失败");
            }
        }
        MediaScannerConnection.scanFile(
                context,
                new String[]{output.getAbsolutePath()},
                new String[]{"image/png"},
                null
        );
        return output.getAbsolutePath();
    }

    private static String sanitize(String value) {
        String sanitized = value == null ? "artwork" : value.replaceAll("[^a-zA-Z0-9_\\-\\u4e00-\\u9fa5]", "_");
        return sanitized.isEmpty() ? "artwork" : sanitized;
    }

    private static final class HandlerCallback {
        private final android.os.Handler handler = new android.os.Handler(android.os.Looper.getMainLooper());

        <T> void success(ResultCallback<T> callback, T result) {
            handler.post(() -> callback.onSuccess(result));
        }

        <T> void error(ResultCallback<T> callback, Throwable error) {
            handler.post(() -> callback.onError(error));
        }
    }
}
