package com.example.anass.audiorecorder.Helper;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import java.util.HashMap;

public class Utils {

    public static void makeToast(Context context, String text){
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }


}
