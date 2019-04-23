package com.example.anass.audiorecorder.Models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity
@Data @NoArgsConstructor
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
