package com.example.anass.audiorecorder.Models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(entity = RecordingItem.class, parentColumns = "id",
            childColumns = "original_record_id", onDelete = CASCADE))
@Data @NoArgsConstructor @AllArgsConstructor
public class ImportantRecord implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "record_starting_time")
    long startTime;

    @ColumnInfo(name = "record_ending_time")
    long stopTime;

    @ColumnInfo(name = "original_record_id")
    int recordId;
}
