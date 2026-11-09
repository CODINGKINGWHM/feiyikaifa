package com.codingkingwhm.feiyikaifa.data.local;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;

import androidx.exifinterface.media.ExifInterface;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public final class LocalImageStore {
    private LocalImageStore() { }

    public static File createBackgroundFile(Context context) {
        File dir = new File(context.getFilesDir(), "backgrounds");
        if (!dir.exists() && !dir.mkdirs()) throw new IllegalStateException("无法创建图片目录");
        return new File(dir, "stage_" + System.currentTimeMillis() + ".jpg");
    }

    public static String copyIntoApp(Context context, Uri source) throws Exception {
        File target = createBackgroundFile(context);
        try (InputStream input = context.getContentResolver().openInputStream(source);
             OutputStream output = new FileOutputStream(target)) {
            if (input == null) throw new IllegalStateException("无法读取所选照片");
            byte[] buffer = new byte[16 * 1024];
            int count;
            while ((count = input.read(buffer)) >= 0) output.write(buffer, 0, count);
        }
        return Uri.fromFile(target).toString();
    }

    public static Bitmap decode(Context context, String uriString, int maxEdge) throws Exception {
        Uri uri = Uri.parse(uriString);
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        try (InputStream input = open(context, uri)) { BitmapFactory.decodeStream(input, null, bounds); }
        int sample = 1;
        while (Math.max(bounds.outWidth / sample, bounds.outHeight / sample) > maxEdge) sample *= 2;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = Math.max(1, sample);
        Bitmap bitmap;
        try (InputStream input = open(context, uri)) { bitmap = BitmapFactory.decodeStream(input, null, options); }
        if (bitmap == null) throw new IllegalStateException("照片解码失败");
        int orientation = ExifInterface.ORIENTATION_NORMAL;
        try (InputStream input = open(context, uri)) {
            orientation = new ExifInterface(input).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        } catch (Throwable ignored) { }
        Matrix matrix = new Matrix();
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) matrix.postRotate(90);
        else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) matrix.postRotate(180);
        else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) matrix.postRotate(270);
        else if (orientation == ExifInterface.ORIENTATION_FLIP_HORIZONTAL) matrix.postScale(-1, 1);
        if (!matrix.isIdentity()) {
            Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            if (rotated != bitmap) bitmap.recycle();
            bitmap = rotated;
        }
        return bitmap;
    }

    private static InputStream open(Context context, Uri uri) throws Exception {
        if ("file".equals(uri.getScheme())) return new FileInputStream(new File(uri.getPath()));
        InputStream input = context.getContentResolver().openInputStream(uri);
        if (input == null) throw new IllegalStateException("无法打开图片");
        return input;
    }
}
