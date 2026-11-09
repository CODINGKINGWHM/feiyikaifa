package com.codingkingwhm.feiyikaifa.feature.poster;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.codingkingwhm.feiyikaifa.R;
import com.codingkingwhm.feiyikaifa.common.ShadowPuppetPainter;

public class PosterCanvasView extends View {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    private Bitmap background;
    private Bitmap puppetPair;
    private float puppetX, puppetY, puppetScale = 1f, puppetRotation;
    private float lastX, lastY, oldDistance, oldAngle;
    private int puppetVariant, frameVariant;
    private String caption = "今日开演";
    private boolean exportMode;

    public PosterCanvasView(Context context) { this(context, null); }
    public PosterCanvasView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        background = BitmapFactory.decodeResource(getResources(), R.drawable.courtyard_stage_v2);
        puppetPair = BitmapFactory.decodeResource(getResources(), R.drawable.shadow_pair_v2);
    }

    public void setBackgroundBitmap(Bitmap bitmap) {
        if (background != null && background != bitmap && !background.isRecycled()) background.recycle();
        background = bitmap; invalidate();
    }
    public void setPuppetVariant(int value) { puppetVariant = Math.floorMod(value, 4); invalidate(); }
    public int getPuppetVariant() { return puppetVariant; }
    public void setFrameVariant(int value) { frameVariant = Math.floorMod(value, 3); invalidate(); }
    public int getFrameVariant() { return frameVariant; }
    public void setCaption(String value) { caption = value == null || value.trim().isEmpty() ? "今日开演" : value.trim(); invalidate(); }
    public String getCaption() { return caption; }

    public Bitmap renderBitmap() {
        Bitmap output = Bitmap.createBitmap(Math.max(1, getWidth()), Math.max(1, getHeight()), Bitmap.Config.ARGB_8888);
        exportMode = true; draw(new Canvas(output)); exportMode = false; invalidate();
        return output;
    }

    @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (puppetX == 0) { puppetX = w * .53f; puppetY = h * .62f; }
    }

    @Override protected void onDraw(Canvas canvas) {
        float w = getWidth(), h = getHeight();
        if (background == null) drawDefaultScene(canvas, w, h); else drawBackground(canvas, w, h);
        paint.setShader(new LinearGradient(0, 0, 0, h, Color.argb(40, 91, 18, 12), Color.argb(105, 40, 10, 7), Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, w, h, paint); paint.setShader(null);

        canvas.save(); canvas.translate(puppetX, puppetY); canvas.rotate(puppetRotation); canvas.scale(puppetScale, puppetScale);
        float selectionHalfWidth;
        float selectionHalfHeight;
        if (puppetVariant == 0 && puppetPair != null) {
            float targetWidth = w * .82f;
            float targetHeight = targetWidth * puppetPair.getHeight() / puppetPair.getWidth();
            if (targetHeight > h * .54f) {
                targetHeight = h * .54f;
                targetWidth = targetHeight * puppetPair.getWidth() / puppetPair.getHeight();
            }
            RectF puppetRect = new RectF(-targetWidth / 2f, -targetHeight / 2f, targetWidth / 2f, targetHeight / 2f);
            canvas.drawBitmap(puppetPair, null, puppetRect, paint);
            selectionHalfWidth = targetWidth / 2f;
            selectionHalfHeight = targetHeight / 2f;
        } else {
            ShadowPuppetPainter.draw(canvas, 0, 0, Math.min(w, h) * .43f, puppetVariant, 12f);
            selectionHalfWidth = w * .17f;
            selectionHalfHeight = h * .22f;
        }
        if (!exportMode) {
            paint.setStyle(Paint.Style.STROKE); paint.setStrokeWidth(2f); paint.setColor(Color.argb(190, 255, 238, 187));
            canvas.drawRect(-selectionHalfWidth, -selectionHalfHeight, selectionHalfWidth, selectionHalfHeight, paint); paint.setStyle(Paint.Style.FILL);
        }
        canvas.restore();
        drawTitle(canvas, w, h);
        drawFrame(canvas, w, h);
    }

    private void drawBackground(Canvas canvas, float w, float h) {
        float scale = Math.max(w / background.getWidth(), h / background.getHeight());
        float dw = background.getWidth() * scale, dh = background.getHeight() * scale;
        RectF target = new RectF((w-dw)/2f, (h-dh)/2f, (w+dw)/2f, (h+dh)/2f);
        canvas.drawBitmap(background, null, target, paint);
    }

    private void drawDefaultScene(Canvas canvas, float w, float h) {
        paint.setShader(new LinearGradient(0, 0, 0, h, new int[]{0xFFFFE2A6,0xFFF4BB66,0xFF6E291B}, null, Shader.TileMode.CLAMP));
        canvas.drawRect(0,0,w,h,paint); paint.setShader(null);
        paint.setColor(0x663D120E); canvas.drawCircle(w*.18f,h*.18f,w*.27f,paint); canvas.drawCircle(w*.85f,h*.12f,w*.35f,paint);
        paint.setColor(0xAA4B1712); canvas.drawRect(0,h*.70f,w,h,paint);
        paint.setColor(0xFF5B2018); canvas.drawRect(w*.08f,h*.18f,w*.14f,h*.72f,paint); canvas.drawRect(w*.86f,h*.18f,w*.92f,h*.72f,paint);
        canvas.drawRect(w*.06f,h*.17f,w*.94f,h*.22f,paint);
        paint.setColor(0xFFDFA34D); for(int i=0;i<7;i++) canvas.drawCircle(w*(.15f+i*.12f),h*.23f,6,paint);
    }

    private void drawTitle(Canvas canvas, float w, float h) {
        paint.setTextAlign(Paint.Align.CENTER); paint.setTypeface(android.graphics.Typeface.create("serif", android.graphics.Typeface.BOLD));
        paint.setTextSize(Math.min(w,h)*.075f); paint.setColor(Color.rgb(255,244,210)); paint.setShadowLayer(10,0,3,Color.rgb(71,16,12));
        canvas.drawText(caption, w/2f, h*.14f, paint); paint.clearShadowLayer();
        paint.setTextSize(Math.min(w,h)*.025f); paint.setLetterSpacing(.22f); paint.setColor(Color.rgb(246,203,111));
        canvas.drawText("影 间 · 皮 影 创 作", w/2f, h*.185f, paint); paint.setLetterSpacing(0); paint.setTextAlign(Paint.Align.LEFT);
    }

    private void drawFrame(Canvas canvas, float w, float h) {
        paint.setStyle(Paint.Style.STROKE);
        if (frameVariant == 0) {
            paint.setColor(Color.rgb(239,195,99)); paint.setStrokeWidth(5f); canvas.drawRect(14,14,w-14,h-14,paint);
            paint.setStrokeWidth(1.5f); canvas.drawRect(23,23,w-23,h-23,paint);
        } else if (frameVariant == 1) {
            paint.setColor(Color.rgb(121,25,20)); paint.setStrokeWidth(18f); canvas.drawRect(9,9,w-9,h-9,paint);
            paint.setColor(Color.rgb(239,195,99)); paint.setStrokeWidth(3f); canvas.drawRect(19,19,w-19,h-19,paint);
        } else {
            paint.setColor(Color.argb(220,255,240,196)); paint.setStrokeWidth(4f);
            for(int i=0;i<4;i++) canvas.drawRect(12+i*8,12+i*8,w-12-i*8,h-12-i*8,paint);
        }
        paint.setStyle(Paint.Style.FILL);
    }

    @Override public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: lastX=event.getX(); lastY=event.getY(); getParent().requestDisallowInterceptTouchEvent(true); return true;
            case MotionEvent.ACTION_POINTER_DOWN: if(event.getPointerCount()>=2){oldDistance=distance(event);oldAngle=angle(event);} return true;
            case MotionEvent.ACTION_MOVE:
                if(event.getPointerCount()>=2){float d=distance(event);if(oldDistance>0)puppetScale=clamp(puppetScale*d/oldDistance,.45f,2f);float a=angle(event);puppetRotation+=a-oldAngle;oldDistance=d;oldAngle=a;}
                else {puppetX=clamp(puppetX+event.getX()-lastX,0,getWidth());puppetY=clamp(puppetY+event.getY()-lastY,0,getHeight());lastX=event.getX();lastY=event.getY();}
                invalidate(); return true;
            case MotionEvent.ACTION_UP: case MotionEvent.ACTION_CANCEL: getParent().requestDisallowInterceptTouchEvent(false); performClick(); return true;
            default:return true;
        }
    }
    @Override public boolean performClick(){super.performClick();return true;}
    private float distance(MotionEvent e){float x=e.getX(0)-e.getX(1),y=e.getY(0)-e.getY(1);return(float)Math.sqrt(x*x+y*y);}
    private float angle(MotionEvent e){return(float)Math.toDegrees(Math.atan2(e.getY(1)-e.getY(0),e.getX(1)-e.getX(0)));}
    private float clamp(float v,float min,float max){return Math.max(min,Math.min(max,v));}
}
