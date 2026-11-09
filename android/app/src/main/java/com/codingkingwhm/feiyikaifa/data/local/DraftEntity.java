package com.codingkingwhm.feiyikaifa.data.local;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "drafts",
        indices = {@Index(value = {"localId"}, unique = true)}
)
public class DraftEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;
    public String localId;
    public long patternId;
    public String patternName;
    public String guideKey;
    public String title;
    public String previewPath;
    public String strokeFilePath;
    public String syncStatus;
    public Long remoteId;
    public long createdAt;
    public long updatedAt;
}
