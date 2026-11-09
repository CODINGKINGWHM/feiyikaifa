package com.codingkingwhm.feiyikaifa.feature.stage;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.codingkingwhm.feiyikaifa.common.ShadowPuppetPainter;

import java.util.ArrayList;
import java.util.List;

public class PuppetStageView extends View {
    public interface Listener {
        void onStatus(String status);
        default void onRecordingChanged(boolean recording) { }
    }
    private static final class Frame {
        final long time; final float x, y, scale, rotation, arm;
        Frame(long time, float x, float y, float scale, float rotation, float arm) {
            this.time = time; this.x = x; this.y = y; this.scale = scale; this.rotation = rotation; this.arm = arm;
        }
    }

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final List<Frame> frames = new ArrayList<>();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private float puppetX, puppetY, puppetScale = 1f, puppetRotation, armAngle = 8f;
    private float lastX, lastY, oldDistance, oldAngle;
    private long recordStarted, lastFrame;
    private boolean recording, playing;
    private int variant;
    private Listener listener;

    public PuppetStageView(Context context) { this(context, null); }
    public PuppetStageView(Context context, @Nullable AttributeSet attrs) { super(context, attrs); }

    public void setListener(Listener listener) { this.listener = listener; }
    public void setVariant(int variant) { this.variant = variant; invalidate(); }

    public void togglePose() {
        armAngle = armAngle > 18 ? -14f : armAngle + 18f;
        captureFrame(); invalidate();
    }

    public void resetPuppet() {
        puppetX = getWidth() / 2f; puppetY = getHeight() * .62f;
        puppetScale = 1f; puppetRotation = 0f; armAngle = 8f;
        frames.clear(); recording = false; playing = false; handler.removeCallbacksAndMessages(null);
        if (listener != null) listener.onRecordingChanged(false);
        notifyStatus("影人已回到舞台中央"); invalidate();
    }

    public void startRecording() {
        if (playing) return;
        frames.clear(); recording = true; recordStarted = System.currentTimeMillis(); lastFrame = 0;
        if (listener != null) listener.onRecordingChanged(true);
        captureFrame(); notifyStatus("● 正在记录动作，最长 10 秒");
        handler.postDelayed(() -> { if (recording) stopRecording(); }, 10_000);
    }

    public void stopRecording() {
        if (!recording) return;
        captureFrame(); recording = false;
        if (listener != null) listener.onRecordingChanged(false);
        notifyStatus("已记录 " + frames.size() + " 个动作关键帧");
    }

    public void playRecording() {
        if (recording) stopRecording();
        if (frames.size() < 2 || playing) { notifyStatus("请先拖动影人并记录一段动作"); return; }
        playing = true; notifyStatus("▶ 正在回放舞台动作");
        long base = frames.get(0).time;
        for (int i = 0; i < frames.size(); i++) {
            Frame frame = frames.get(i);
            boolean last = i == frames.size() - 1;
            handler.postDelayed(() -> {
                puppetX = frame.x; puppetY = frame.y; puppetScale = frame.scale;
                puppetRotation = frame.rotation; armAngle = frame.arm; invalidate();
                if (last) { playing = false; notifyStatus("回放完成，可以继续操控"); }
            }, Math.min(10_000, frame.time - base));
        }
    }

    @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (puppetX == 0) { puppetX = w / 2f; puppetY = h * .62f; }
    }

    @Override protected void onDraw(Canvas canvas) {
        float w = getWidth(), h = getHeight();
        paint.setShader(new LinearGradient(0, 0, 0, h,
                new int[]{Color.rgb(35, 9, 9), Color.rgb(112, 28, 21), Color.rgb(238, 184, 91)},
                null, Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, w, h, paint); paint.setShader(null);
        paint.setColor(Color.argb(110, 255, 227, 170));
        canvas.drawCircle(w * .5f, h * .38f, w * .42f, paint);
        paint.setColor(Color.rgb(89, 18, 16));
        Path left = new Path(); left.moveTo(0,0); left.lineTo(w*.22f,0); left.quadTo(w*.12f,h*.48f,w*.27f,h); left.lineTo(0,h); left.close(); canvas.drawPath(left,paint);
        Path right = new Path(); right.moveTo(w,0); right.lineTo(w*.78f,0); right.quadTo(w*.88f,h*.48f,w*.73f,h); right.lineTo(w,h); right.close(); canvas.drawPath(right,paint);
        paint.setColor(Color.argb(150, 72, 20, 14)); canvas.drawRect(0, h*.83f, w, h, paint);
        paint.setColor(Color.rgb(241, 204, 126)); paint.setStrokeWidth(3f);
        for (int i = 0; i < 5; i++) canvas.drawLine(0, h*.86f+i*12, w, h*.86f+i*12, paint);
        canvas.save(); canvas.translate(puppetX, puppetY); canvas.rotate(puppetRotation); canvas.scale(puppetScale, puppetScale);
        ShadowPuppetPainter.draw(canvas, 0, 0, Math.min(w, h) * .39f, variant, armAngle);
        canvas.restore();
        if (recording) { paint.setColor(Color.RED); canvas.drawCircle(w-28, 28, 9, paint); }
    }

    @Override public boolean onTouchEvent(MotionEvent event) {
        if (playing) return true;
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                lastX = event.getX(); lastY = event.getY(); getParent().requestDisallowInterceptTouchEvent(true); return true;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (event.getPointerCount() >= 2) { oldDistance = distance(event); oldAngle = angle(event); } return true;
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() >= 2) {
                    float distance = distance(event); if (oldDistance > 0) puppetScale = clamp(puppetScale * distance / oldDistance, .55f, 1.7f);
                    float angle = angle(event); puppetRotation += angle - oldAngle; oldDistance = distance; oldAngle = angle;
                } else {
                    puppetX += event.getX() - lastX; puppetY += event.getY() - lastY;
                    puppetX = clamp(puppetX, 40, getWidth()-40); puppetY = clamp(puppetY, 80, getHeight()-40);
                    lastX = event.getX(); lastY = event.getY();
                }
                captureFrame(); invalidate(); return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                captureFrame(); getParent().requestDisallowInterceptTouchEvent(false); performClick(); return true;
            default: return true;
        }
    }

    @Override public boolean performClick() { super.performClick(); return true; }

    private void captureFrame() {
        if (!recording) return;
        long now = System.currentTimeMillis();
        if (lastFrame != 0 && now - lastFrame < 45) return;
        frames.add(new Frame(now - recordStarted, puppetX, puppetY, puppetScale, puppetRotation, armAngle)); lastFrame = now;
    }
    private float distance(MotionEvent e) { float x=e.getX(0)-e.getX(1), y=e.getY(0)-e.getY(1); return (float)Math.sqrt(x*x+y*y); }
    private float angle(MotionEvent e) { return (float)Math.toDegrees(Math.atan2(e.getY(1)-e.getY(0), e.getX(1)-e.getX(0))); }
    private float clamp(float value, float min, float max) { return Math.max(min, Math.min(max, value)); }
    private void notifyStatus(String value) { if (listener != null) listener.onStatus(value); }
}
