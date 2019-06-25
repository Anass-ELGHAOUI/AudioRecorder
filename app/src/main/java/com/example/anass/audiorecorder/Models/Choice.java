package com.example.anass.audiorecorder.Models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Choice implements Serializable{
    @PrimaryKey(autoGenerate = true)
    private int id;

    private int choix;
}
