package com.codingkingwhm.feiyikaifa.feature.drawing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

import java.util.List;

public class PatternDrawingView extends View {

    private static final float DESIGN_CANVAS_SIZE = 1080f;
    private static final float MIN_MOVE_DISTANCE = 2f;

    private final StrokeHistory history = new StrokeHistory();
    private final Paint guidePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    private final Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
    private final RectF guideDestination = new RectF();

    private Bitmap guideBitmap;
    private Bitmap drawingBitmap;
    private Canvas drawingCanvas;
    private Stroke currentStroke;
    private float lastX;
    private float lastY;

    private DrawingTool currentTool = DrawingTool.PEN;
    private int currentColor = Color.BLACK;
    private float currentWidth = 14f;
    private float guideAlpha = 0.35f;
    private boolean guideVisible = true;
    private boolean dirty;

    public PatternDrawingView(Context context) {
        this(context, null);
    }

    public PatternDrawingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PatternDrawingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackgroundColor(Color.WHITE);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeCap(Paint.Cap.ROUND);
        strokePaint.setStrokeJoin(Paint.Join.ROUND);
    }

    public void setGuideResource(@DrawableRes int drawableResId) {
        if (guideBitmap != null) {
            guideBitmap.recycle();
        }
        guideBitmap = BitmapFactory.decodeResource(getResources(), drawableResId);
        updateGuideDestination();
        invalidate();
    }

    public void setGuideAlpha(float alpha) {
        guideAlpha = Math.max(0f, Math.min(1f, alpha));
        invalidate();
    }

    public float getGuideAlpha() {
        return guideAlpha;
    }

    public void setGuideVisible(boolean visible) {
        guideVisible = visible;
        invalidate();
    }

    public void usePen(int color) {
        currentTool = DrawingTool.PEN;
        currentColor = color;
    }

    public void useEraser() {
        currentTool = DrawingTool.ERASER;
    }

    public void setStrokeWidth(float width) {
        currentWidth = Math.max(2f, width);
    }

    public void undo() {
        if (history.undo()) {
            rebuildDrawingLayer();
            dirty = true;
        }
    }

    public void redo() {
        if (history.redo()) {
            rebuildDrawingLayer();
            dirty = true;
        }
    }

    public void clearDrawing() {
        if (history.size() == 0) {
            return;
        }
        history.clear();
        rebuildDrawingLayer();
        dirty = true;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void markSaved() {
        dirty = false;
    }

    public DrawingDocument createDocument(long patternId, String patternName, String guideKey) {
        return new DrawingDocument(
                patternId,
                patternName,
                guideKey,
                guideAlpha,
                history.snapshot()
        );
    }

    public void restoreDocument(DrawingDocument document) {
        history.replaceAll(document.getStrokes());
        setGuideAlpha(document.getGuideAlpha());
        rebuildDrawingLayer();
        dirty = false;
    }

    public Bitmap exportBitmap(boolean includeGuide) {
        int width = Math.max(1, getWidth());
        int height = Math.max(1, getHeight());
        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        canvas.drawColor(Color.WHITE);
        if (includeGuide && guideVisible) {
            drawGuide(canvas);
        }
        if (drawingBitmap != null) {
            canvas.drawBitmap(drawingBitmap, 0f, 0f, null);
        }
        return output;
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        if (width <= 0 || height <= 0) {
            return;
        }
        if (drawingBitmap != null) {
            drawingBitmap.recycle();
        }
        drawingBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        drawingCanvas = new Canvas(drawingBitmap);
        updateGuideDestination();
        rebuildDrawingLayer();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);
        if (guideVisible) {
            drawGuide(canvas);
        }
        if (drawingBitmap != null) {
            canvas.drawBitmap(drawingBitmap, 0f, 0f, null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (drawingCanvas == null || getWidth() == 0 || getHeight() == 0) {
            return false;
        }
        float x = Math.max(0f, Math.min(getWidth(), event.getX()));
        float y = Math.max(0f, Math.min(getHeight(), event.getY()));

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                currentStroke = new Stroke(currentTool, currentColor, currentWidth);
                currentStroke.addPoint(x / getWidth(), y / getHeight());
                lastX = x;
                lastY = y;
                drawDot(currentStroke, x, y);
                dirty = true;
                invalidate();
                return true;

            case MotionEvent.ACTION_MOVE:
                if (currentStroke == null) {
                    return false;
                }
                if (distance(lastX, lastY, x, y) >= MIN_MOVE_DISTANCE) {
                    currentStroke.addPoint(x / getWidth(), y / getHeight());
                    drawSegment(currentStroke, lastX, lastY, x, y);
                    lastX = x;
                    lastY = y;
                    invalidate();
                }
                return true;

            case MotionEvent.ACTION_UP:
                if (currentStroke != null) {
                    if (distance(lastX, lastY, x, y) >= MIN_MOVE_DISTANCE) {
                        currentStroke.addPoint(x / getWidth(), y / getHeight());
                        drawSegment(currentStroke, lastX, lastY, x, y);
                    }
                    history.add(currentStroke);
                    currentStroke = null;
                }
                getParent().requestDisallowInterceptTouchEvent(false);
                performClick();
                invalidate();
                return true;

            case MotionEvent.ACTION_CANCEL:
                currentStroke = null;
                getParent().requestDisallowInterceptTouchEvent(false);
                rebuildDrawingLayer();
                return true;

            default:
                return super.onTouchEvent(event);
        }
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }

    private void drawGuide(Canvas canvas) {
        if (guideBitmap == null || guideBitmap.isRecycled()) {
            return;
        }
        guidePaint.setAlpha(Math.round(guideAlpha * 255));
        canvas.drawBitmap(guideBitmap, null, guideDestination, guidePaint);
    }

    private void updateGuideDestination() {
        if (guideBitmap == null || getWidth() == 0 || getHeight() == 0) {
            return;
        }
        float scale = Math.min(
                (float) getWidth() / guideBitmap.getWidth(),
                (float) getHeight() / guideBitmap.getHeight()
        );
        float width = guideBitmap.getWidth() * scale;
        float height = guideBitmap.getHeight() * scale;
        float left = (getWidth() - width) / 2f;
        float top = (getHeight() - height) / 2f;
        guideDestination.set(left, top, left + width, top + height);
    }

    private void rebuildDrawingLayer() {
        if (drawingCanvas == null || drawingBitmap == null) {
            return;
        }
        drawingCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        for (Stroke stroke : history.getDone()) {
            drawCompleteStroke(stroke);
        }
        invalidate();
    }

    private void drawCompleteStroke(Stroke stroke) {
        List<StrokePoint> points = stroke.getPoints();
        if (points.isEmpty()) {
            return;
        }
        configurePaint(stroke);
        if (points.size() == 1) {
            StrokePoint point = points.get(0);
            drawingCanvas.drawCircle(
                    point.getX() * getWidth(),
                    point.getY() * getHeight(),
                    strokePaint.getStrokeWidth() / 2f,
                    strokePaint
            );
            return;
        }
        Path path = new Path();
        StrokePoint first = points.get(0);
        path.moveTo(first.getX() * getWidth(), first.getY() * getHeight());
        for (int index = 1; index < points.size(); index++) {
            StrokePoint point = points.get(index);
            path.lineTo(point.getX() * getWidth(), point.getY() * getHeight());
        }
        drawingCanvas.drawPath(path, strokePaint);
        strokePaint.setXfermode(null);
    }

    private void drawDot(Stroke stroke, float x, float y) {
        configurePaint(stroke);
        drawingCanvas.drawCircle(x, y, strokePaint.getStrokeWidth() / 2f, strokePaint);
        strokePaint.setXfermode(null);
    }

    private void drawSegment(Stroke stroke, float startX, float startY, float endX, float endY) {
        configurePaint(stroke);
        drawingCanvas.drawLine(startX, startY, endX, endY, strokePaint);
        strokePaint.setXfermode(null);
    }

    private void configurePaint(Stroke stroke) {
        strokePaint.setColor(stroke.getColor());
        strokePaint.setStrokeWidth(Math.max(1f, stroke.getWidth() * getWidth() / DESIGN_CANVAS_SIZE));
        strokePaint.setXfermode(
                stroke.getTool() == DrawingTool.ERASER
                        ? new PorterDuffXfermode(PorterDuff.Mode.CLEAR)
                        : null
        );
    }

    private static float distance(float startX, float startY, float endX, float endY) {
        float x = endX - startX;
        float y = endY - startY;
        return (float) Math.sqrt(x * x + y * y);
    }
}
