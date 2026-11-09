package com.codingkingwhm.feiyikaifa.feature.drawing;

public class StrokePoint {

    private float x;
    private float y;

    public StrokePoint() {
    }

    public StrokePoint(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public StrokePoint(StrokePoint source) {
        this(source.x, source.y);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
