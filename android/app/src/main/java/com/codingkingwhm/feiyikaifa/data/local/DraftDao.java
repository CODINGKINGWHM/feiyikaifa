package com.codingkingwhm.feiyikaifa.data.local;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DraftDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long save(DraftEntity entity);

    @Query("SELECT * FROM drafts WHERE id = :id LIMIT 1")
    DraftEntity findById(long id);

    @Query("SELECT * FROM drafts ORDER BY updatedAt DESC")
    List<DraftEntity> findAll();

    @Delete
    void delete(DraftEntity entity);
}
