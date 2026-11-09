package com.codingkingwhm.feiyikaifa.common;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

public final class ShadowPuppetPainter {
    private ShadowPuppetPainter() { }

    public static void draw(Canvas canvas, float cx, float cy, float size, int variant, float armAngle) {
        canvas.save();
        canvas.translate(cx, cy);
        float s = size / 220f;
        canvas.scale(s, s);

        int[] reds = {Color.rgb(176, 42, 31), Color.rgb(31, 104, 96), Color.rgb(190, 91, 37), Color.rgb(111, 41, 95)};
        int base = reds[Math.floorMod(variant, reds.length)];
        Paint fill = new Paint(Paint.ANTI_ALIAS_FLAG);
        fill.setStyle(Paint.Style.FILL);
        fill.setColor(base);
        Paint gold = new Paint(Paint.ANTI_ALIAS_FLAG);
        gold.setStyle(Paint.Style.STROKE);
        gold.setStrokeWidth(6f);
        gold.setStrokeJoin(Paint.Join.ROUND);
        gold.setColor(Color.rgb(236, 190, 84));
        Paint ink = new Paint(Paint.ANTI_ALIAS_FLAG);
        ink.setStyle(Paint.Style.STROKE);
        ink.setStrokeWidth(4f);
        ink.setColor(Color.rgb(55, 25, 19));

        Path crown = new Path();
        crown.moveTo(-42, -77); crown.lineTo(-30, -121); crown.lineTo(-10, -93);
        crown.lineTo(5, -130); crown.lineTo(20, -91); crown.lineTo(45, -113);
        crown.lineTo(35, -69); crown.close();
        canvas.drawPath(crown, fill); canvas.drawPath(crown, gold);

        canvas.drawOval(new RectF(-37, -87, 36, -18), fill);
        canvas.drawOval(new RectF(-37, -87, 36, -18), gold);
        canvas.drawCircle(12, -57, 5, gold);

        Path body = new Path();
        body.moveTo(-24, -20); body.quadTo(-63, 35, -83, 112);
        body.lineTo(78, 112); body.quadTo(56, 27, 23, -20); body.close();
        canvas.drawPath(body, fill); canvas.drawPath(body, gold);

        drawArm(canvas, -22, -8, -72, 20, -112, -4, armAngle, fill, gold);
        drawArm(canvas, 24, -8, 72, 24, 112, 5, -armAngle * .65f, fill, gold);

        Paint cut = new Paint(Paint.ANTI_ALIAS_FLAG);
        cut.setColor(Color.argb(220, 255, 231, 174));
        for (int row = 0; row < 4; row++) {
            float y = 15 + row * 22;
            for (int col = -2; col <= 2; col++) {
                float x = col * 22f + (row % 2) * 10f;
                canvas.drawCircle(x, y, 5f, cut);
                canvas.drawCircle(x, y, 6f, ink);
            }
        }
        canvas.drawLine(-54, 106, -35, 25, gold);
        canvas.drawLine(54, 106, 35, 25, gold);
        canvas.restore();
    }

    private static void drawArm(Canvas canvas, float x1, float y1, float x2, float y2,
                                float x3, float y3, float angle, Paint fill, Paint outline) {
        canvas.save();
        canvas.rotate(angle, x1, y1);
        Paint limb = new Paint(fill);
        limb.setStrokeWidth(22f);
        limb.setStrokeCap(Paint.Cap.ROUND);
        limb.setStyle(Paint.Style.STROKE);
        canvas.drawLine(x1, y1, x2, y2, limb);
        canvas.drawLine(x2, y2, x3, y3, limb);
        Paint edge = new Paint(outline);
        edge.setStrokeWidth(4f);
        edge.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawLine(x1, y1, x2, y2, edge);
        canvas.drawLine(x2, y2, x3, y3, edge);
        canvas.drawCircle(x2, y2, 7f, outline);
        canvas.restore();
    }
}
