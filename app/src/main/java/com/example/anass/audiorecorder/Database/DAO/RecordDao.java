package com.example.anass.audiorecorder.Database.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import com.example.anass.audiorecorder.Models.RecordingItem;

import java.util.List;

@Dao
public interface RecordDao {

    @Insert
    void addRecord(RecordingItem mRecordingItem);

    @Delete
    void deleteRecord(RecordingItem mRecordingItem);

    @Query("SELECT * FROM records_table")
    List<RecordingItem> getRecords();

    @Query("DELETE  FROM records_table")
    void deleteAllRecords();
}
