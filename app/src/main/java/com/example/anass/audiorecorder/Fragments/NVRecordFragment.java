package com.example.anass.audiorecorder.Fragments;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.anass.audiorecorder.Activities.MainActivity;
import com.example.anass.audiorecorder.Database.DataBase;
import com.example.anass.audiorecorder.Database.Repositories.ImportantRecordRepository;
import com.example.anass.audiorecorder.Database.Repositories.RecordRepository;
import com.example.anass.audiorecorder.Helper.OnLoadCompleted;
import com.example.anass.audiorecorder.Helper.OnSwipeTouchListener;
import com.example.anass.audiorecorder.Helper.RecordingService;
import com.example.anass.audiorecorder.Models.ImportantRecord;
import com.example.anass.audiorecorder.R;

import java.io.File;
import java.util.Locale;


import butterknife.Bind;
import butterknife.ButterKnife;

public class NVRecordFragment extends Fragment implements OnLoadCompleted {

    private MainActivity activity;


    @Bind(R.id.image_view_foot_print)
    public ImageView imageView;

    private TextToSpeech mTTS;
    private DataBase db;
    private RecordRepository.getLastIdAsyncTask lastIdAsyncTask;
    ImportantRecordRepository mImportantRecordRepository;
    private ImportantRecord mImportantRecord;
    private boolean isImpRecordStarted = false;

    private boolean mStartRecording = false;

    public static NVRecordFragment newInstance() {
        NVRecordFragment fragment = new NVRecordFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.nv_record_fragment, container, false);
        ButterKnife.bind(this, view);
        activity = (MainActivity) getActivity();
        return view;
    }

    public void init() {
        mImportantRecordRepository = new ImportantRecordRepository(activity.getApplication());
        db = DataBase.getInstance(activity.getApplicationContext());
        lastIdAsyncTask = new RecordRepository.getLastIdAsyncTask(db.recordDao(), this);
        lastIdAsyncTask.execute();
        swipeConfiguration();
        mTTS = new TextToSpeech(activity.getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = mTTS.setLanguage(Locale.FRENCH);
                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    } else {
                        getInstructions();
                    }
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void speak(String text) {
        mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }


    public void getInstructions() {
        speak("Glissez ver le haut pour ajouter un nouveau record, vers le bas pour enregistrez le record, " +
                " vers la gauche pour revenir au menu précédent. Aprés le lancement de record glisser vers la droite pour lancer un record imprtant et vers la gauche pour le sauvgarder.");
    }

    @SuppressLint("ClickableViewAccessibility")
    private void swipeConfiguration() {
        imageView.setOnTouchListener(new OnSwipeTouchListener(activity) {
            public void onSwipeTop() {
                if (mTTS.isSpeaking()) {
                    mTTS.stop();
                    mTTS.shutdown();
                }
                if (!mStartRecording) {
                    mStartRecording = !mStartRecording;
                    onRecord(mStartRecording);
                }

            }

            public void onSwipeRight() {
                if (mStartRecording) {
                    mImportantRecord = new ImportantRecord();
                    mImportantRecord.setStartTime(System.currentTimeMillis());
                    isImpRecordStarted = true ;
                }
            }

            public void onSwipeLeft() {
                if (mStartRecording && isImpRecordStarted) {
                    mImportantRecord.setStopTime(System.currentTimeMillis());
                    mImportantRecord.setRecordId(lastIdAsyncTask.getLastId() + 1);
                    mImportantRecordRepository.addImportantRecord(mImportantRecord);
                    isImpRecordStarted = false;
                }else if(!mStartRecording){
                    if (mTTS.isSpeaking()) {
                        mTTS.stop();
                        mTTS.shutdown();
                    }
                    activity.onBackPressed();
                }

            }
            public void onSwipeBottom() {
                if (mTTS.isSpeaking()) {
                    mTTS.stop();
                    mTTS.shutdown();
                }
                if (mStartRecording) {
                    mStartRecording = !mStartRecording;
                    onRecord(mStartRecording);
                    speak("Record enregistrer avec succés.");
                }
            }

        });
    }


    private void onRecord(boolean start) {
        Intent intent = new Intent(getActivity(), RecordingService.class);
        if (start) {
            // start recording
            Toast.makeText(getActivity(), R.string.toast_recording_start, Toast.LENGTH_SHORT).show();
            File folder = new File(Environment.getExternalStorageDirectory() + "/SoundRecorder");
            if (!folder.exists()) {
                //folder /SoundRecorder doesn't exist, create the folder
                folder.mkdir();
            }
            //start RecordingService
            getActivity().startService(intent);

            //keep screen on while recording
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        } else {
            getActivity().stopService(intent);
            //allow the screen to turn off again once recording is finished
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }
    }

    @Override
    public void OnLoadCompleted() {

    }
}
