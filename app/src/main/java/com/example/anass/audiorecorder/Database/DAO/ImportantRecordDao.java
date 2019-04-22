package com.example.anass.audiorecorder.Database.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.anass.audiorecorder.Models.ImportantRecord;

import java.util.List;

@Dao
public interface ImportantRecordDao {

    @Insert
    void addImportantRecord(ImportantRecord importantRecord);

    @Query("SELECT * FROM ImportantRecord WHERE original_record_id = :recordId")
    List<ImportantRecord> getImportantRecord(int recordId);

}
