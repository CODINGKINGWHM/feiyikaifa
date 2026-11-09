package com.codingkingwhm.feiyikaifa.feature.drawing;

import java.util.ArrayList;
import java.util.List;

public class DrawingDocument {

    private int version = 1;
    private long patternId;
    private String patternName;
    private String guideKey;
    private float guideAlpha = 0.35f;
    private List<Stroke> strokes = new ArrayList<>();

    public DrawingDocument() {
    }

    public DrawingDocument(
            long patternId,
            String patternName,
            String guideKey,
            float guideAlpha,
            List<Stroke> strokes
    ) {
        this.patternId = patternId;
        this.patternName = patternName;
        this.guideKey = guideKey;
        this.guideAlpha = guideAlpha;
        this.strokes = strokes;
    }

    public int getVersion() {
        return version;
    }

    public long getPatternId() {
        return patternId;
    }

    public String getPatternName() {
        return patternName;
    }

    public String getGuideKey() {
        return guideKey;
    }

    public float getGuideAlpha() {
        return guideAlpha;
    }

    public List<Stroke> getStrokes() {
        return strokes;
    }
}
