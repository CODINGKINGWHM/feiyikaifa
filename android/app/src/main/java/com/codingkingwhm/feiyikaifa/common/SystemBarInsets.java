package com.codingkingwhm.feiyikaifa.common;

import android.view.View;
import android.view.Window;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

public final class SystemBarInsets {

    private SystemBarInsets() {
    }

    public static void apply(Window window, View root) {
        WindowCompat.setDecorFitsSystemWindows(window, false);
        int left = root.getPaddingLeft();
        int top = root.getPaddingTop();
        int right = root.getPaddingRight();
        int bottom = root.getPaddingBottom();
        ViewCompat.setOnApplyWindowInsetsListener(root, (view, windowInsets) -> {
            Insets bars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(
                    left + bars.left,
                    top + bars.top,
                    right + bars.right,
                    bottom + bars.bottom
            );
            return windowInsets;
        });
        ViewCompat.requestApplyInsets(root);
    }
}
