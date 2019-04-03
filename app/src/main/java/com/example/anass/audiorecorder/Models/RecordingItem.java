package com.example.anass.audiorecorder.Models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "records_table")
public class RecordingItem implements Comparable<RecordingItem>{

    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int mId;

    @ColumnInfo(name = "record_name")
    private String mName;

    @ColumnInfo(name = "record_path")
    private String mFilePath;

    @ColumnInfo(name = "record_length")
    private int mLength;

    @ColumnInfo(name = "record_time")
    private long mTime;

    public RecordingItem() {

    }

    public RecordingItem(String Name, String FilePath, int Length, long Time) {
        this.mName = Name;
        this.mFilePath = FilePath;
        this.mLength = Length;
        this.mTime = Time;
    }

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getFilePath() {
        return mFilePath;
    }

    public int getLength() {
        return mLength;
    }

    public long getTime() {
        return mTime;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public void setFilePath(String mFilePath) {
        this.mFilePath = mFilePath;
    }

    public void setLength(int mLength) {
        this.mLength = mLength;
    }

    public void setTime(long mTime) {
        this.mTime = mTime;
    }

    @Override
    public int compareTo(@NonNull RecordingItem o) {
        if(this.mName != null)
            return this.mName.toLowerCase().compareTo(o.getName().toLowerCase());
        else
            throw new IllegalArgumentException();
    }
}
