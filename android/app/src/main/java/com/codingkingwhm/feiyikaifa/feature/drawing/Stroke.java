package com.codingkingwhm.feiyikaifa.feature.drawing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Stroke {

    private String tool;
    private int color;
    private float width;
    private List<StrokePoint> points = new ArrayList<>();

    public Stroke() {
    }

    public Stroke(DrawingTool tool, int color, float width) {
        this.tool = tool.name();
        this.color = color;
        this.width = width;
    }

    public Stroke(Stroke source) {
        this.tool = source.tool;
        this.color = source.color;
        this.width = source.width;
        this.points = new ArrayList<>();
        for (StrokePoint point : source.points) {
            this.points.add(new StrokePoint(point));
        }
    }

    public DrawingTool getTool() {
        try {
            return DrawingTool.valueOf(tool);
        } catch (IllegalArgumentException | NullPointerException ignored) {
            return DrawingTool.PEN;
        }
    }

    public int getColor() {
        return color;
    }

    public float getWidth() {
        return width;
    }

    public List<StrokePoint> getPoints() {
        return Collections.unmodifiableList(points);
    }

    public void addPoint(float normalizedX, float normalizedY) {
        points.add(new StrokePoint(normalizedX, normalizedY));
    }
}
