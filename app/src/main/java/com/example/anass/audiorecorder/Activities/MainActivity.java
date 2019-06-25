package com.example.anass.audiorecorder.Activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.anass.audiorecorder.Database.DataBase;
import com.example.anass.audiorecorder.Database.Repositories.ChoiceRepository;
import com.example.anass.audiorecorder.Fragments.ModeChoiceFragment;
import com.example.anass.audiorecorder.Fragments.NVChoiceFragment;
import com.example.anass.audiorecorder.Fragments.NVRecordFragment;
import com.example.anass.audiorecorder.Fragments.RecordFragment;
import com.example.anass.audiorecorder.Fragments.RecordFragmentVoyant;
import com.example.anass.audiorecorder.Helper.OnLoadCompleted;
import com.example.anass.audiorecorder.Helper.Utils;
import com.example.anass.audiorecorder.Models.Choice;
import com.example.anass.audiorecorder.R;

import static android.Manifest.permission.RECORD_AUDIO;

public class MainActivity extends AppCompatActivity implements OnLoadCompleted {
    DataBase db;
    ChoiceRepository.getChoiceAsyncTask choiceAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = DataBase.getInstance(this.getApplicationContext());
        choiceAsyncTask = new ChoiceRepository.getChoiceAsyncTask(db.choiceDao(), this);
        choiceAsyncTask.execute();
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

    @Override
    public void OnLoadCompleted() {
        Choice choice = choiceAsyncTask.getChoice();
        if(choice == null)
            this.navigateTo(ModeChoiceFragment.newInstance());
        else{
            if(choice.getChoix() == 1)
                this.navigateTo(NVChoiceFragment.newInstance());
            else
                this.navigateTo(RecordFragmentVoyant.newInstance());
        }
    }
}
