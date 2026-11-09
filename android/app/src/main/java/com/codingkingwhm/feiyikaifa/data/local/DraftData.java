package com.codingkingwhm.feiyikaifa.data.local;

import com.codingkingwhm.feiyikaifa.feature.drawing.DrawingDocument;

public final class DraftData {

    private final DraftEntity entity;
    private final DrawingDocument document;

    public DraftData(DraftEntity entity, DrawingDocument document) {
        this.entity = entity;
        this.document = document;
    }

    public DraftEntity getEntity() {
        return entity;
    }

    public DrawingDocument getDocument() {
        return document;
    }
}
