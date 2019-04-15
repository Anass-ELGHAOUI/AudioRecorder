package com.example.anass.audiorecorder.Models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(tableName = "records_table") @Data @NoArgsConstructor
public class RecordingItem implements Comparable<RecordingItem>{

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "record_name")
    private String name;

    @ColumnInfo(name = "record_path")
    private String filePath;

    @ColumnInfo(name = "record_length")
    private long length;

    //@ColumnInfo(name = "record_time")
    //private long time;

    public RecordingItem(String Name, String FilePath, long Length) {
        this.name = Name;
        this.filePath = FilePath;
        this.length = Length;
        //this.time = Time;
    }


    @Override
    public int compareTo(@NonNull RecordingItem o) {
        if(this.name != null)
            return this.name.toLowerCase().compareTo(o.getName().toLowerCase());
        else
            throw new IllegalArgumentException();
    }
}
