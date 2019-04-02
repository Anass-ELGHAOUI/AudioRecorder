package com.example.anass.audiorecorder.Helper;

import android.content.Context;
import android.widget.Toast;

public class Utils {

    public static void makeToast(Context context, String text){
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}
