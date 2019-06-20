package com.example.anass.audiorecorder.Activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.anass.audiorecorder.Fragments.ModeChoiceFragment;
import com.example.anass.audiorecorder.Fragments.NVRecordFragment;
import com.example.anass.audiorecorder.Fragments.RecordFragment;
import com.example.anass.audiorecorder.Fragments.RecordFragmentVoyant;
import com.example.anass.audiorecorder.Helper.Utils;
import com.example.anass.audiorecorder.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(Utils.checkPermission(getApplicationContext()))
            this.navigateTo(ModeChoiceFragment.newInstance());
        else
            this.navigateTo(RecordFragmentVoyant.newInstance());
    }

    public void navigateTo(Fragment fragment){

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, fragment);
        transaction.addToBackStack(fragment.getClass().toString());
        transaction.commit();
    }

    @Override
    public void onBackPressed() {

        int count = getFragmentManager().getBackStackEntryCount();

        if (count == 1) {
            finish();
        } else {
            super.onBackPressed();
        }
    }
}
