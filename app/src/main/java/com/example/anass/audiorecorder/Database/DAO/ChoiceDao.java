package com.example.anass.audiorecorder.Database.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.anass.audiorecorder.Models.Choice;

@Dao
public interface ChoiceDao {
    @Insert
    void addChoice(Choice choice);

    @Query("SELECT * FROM choice")
    Choice getChoice();
}
