package com.example.anass.audiorecorder.Models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(tableName = "records_table") @Data @NoArgsConstructor
public class RecordingItem implements Comparable<RecordingItem>, Serializable{

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "record_name")
    private String name;

    @ColumnInfo(name = "record_path")
    private String filePath;

    @ColumnInfo(name = "record_start")
    private long start;

    @ColumnInfo(name = "record_end")
    private long end;

    //@ColumnInfo(name = "record_time")
    //private long time;

    public RecordingItem(String Name, String FilePath, long start, long end) {
        this.name = Name;
        this.filePath = FilePath;
        this.start = start;
        this.end = end;
    }


    @Override
    public int compareTo(@NonNull RecordingItem o) {
        if(this.name != null)
            return this.name.toLowerCase().compareTo(o.getName().toLowerCase());
        else
            throw new IllegalArgumentException();
    }
}
