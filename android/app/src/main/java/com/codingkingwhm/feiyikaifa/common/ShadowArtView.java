package com.codingkingwhm.feiyikaifa.common;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.Nullable;

public class ShadowArtView extends View {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int variant;
    private float pulse;
    private ValueAnimator animator;

    public ShadowArtView(Context context) { this(context, null); }
    public ShadowArtView(Context context, @Nullable AttributeSet attrs) { super(context, attrs); }

    public void setVariant(int variant) { this.variant = variant; invalidate(); }

    @Override protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(3200);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(a -> { pulse = (float) a.getAnimatedValue(); invalidate(); });
        animator.start();
    }

    @Override protected void onDetachedFromWindow() {
        if (animator != null) animator.cancel();
        super.onDetachedFromWindow();
    }

    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float w = getWidth();
        float h = getHeight();
        paint.setShader(new LinearGradient(0, 0, w, h,
                new int[]{Color.rgb(69, 14, 13), Color.rgb(156, 43, 30), Color.rgb(32, 12, 11)},
                null, Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, w, h, paint);
        paint.setShader(new RadialGradient(w * .66f, h * .38f, Math.max(w, h) * .62f,
                Color.argb(105 + (int) (pulse * 35), 255, 222, 145), Color.TRANSPARENT, Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, w, h, paint);
        paint.setShader(null);

        paint.setColor(Color.argb(80, 255, 226, 166));
        Path mountains = new Path();
        mountains.moveTo(0, h * .73f); mountains.lineTo(w * .18f, h * .49f);
        mountains.lineTo(w * .35f, h * .69f); mountains.lineTo(w * .54f, h * .40f);
        mountains.lineTo(w * .78f, h * .72f); mountains.lineTo(w, h * .50f);
        mountains.lineTo(w, h); mountains.lineTo(0, h); mountains.close();
        canvas.drawPath(mountains, paint);

        float puppetSize = Math.min(w * .58f, h * .76f) * (1f + pulse * .018f);
        ShadowPuppetPainter.draw(canvas, w * .68f, h * .58f, puppetSize, variant, 8f + pulse * 5f);
    }
}
