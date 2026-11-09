package com.codingkingwhm.feiyikaifa.data.model;

import androidx.annotation.DrawableRes;

public final class PatternItem {

    private final long id;
    private final String name;
    private final String guideKey;
    private final int difficulty;
    @DrawableRes
    private final int drawableResId;

    public PatternItem(long id, String name, String guideKey, int difficulty, int drawableResId) {
        this.id = id;
        this.name = name;
        this.guideKey = guideKey;
        this.difficulty = difficulty;
        this.drawableResId = drawableResId;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getGuideKey() {
        return guideKey;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public int getDrawableResId() {
        return drawableResId;
    }
}
