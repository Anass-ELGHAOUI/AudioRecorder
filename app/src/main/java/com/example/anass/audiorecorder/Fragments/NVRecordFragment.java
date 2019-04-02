package com.example.anass.audiorecorder.Fragments;

import android.Manifest;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.anass.audiorecorder.Activities.MainActivity;
import com.example.anass.audiorecorder.Helper.RecordingService;
import com.example.anass.audiorecorder.R;

import java.io.File;
import java.util.Locale;


import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;


public class NVRecordFragment extends Fragment {

    private MainActivity activity;


    @Bind(R.id.image_view_foot_print)
    public ImageView imageView;

    private TextToSpeech mTTS;

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
        return view;
    }

    public void init() {
        mTTS = new TextToSpeech(activity.getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = mTTS.setLanguage(Locale.FRENCH);
                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    } else {
                        speak("Appuyer une fois pour ajouter un nouveau record, pour enregistrer appuyer une deuxiéme fois, " +
                                " glisser vers la droite pour revenir au menu précédent, Appuyer longtemps pour écouter le consigne");
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
    }

    private void speak(String text) {
        mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    @OnClick(R.id.image_view_foot_print)
    public void Record() {
        mStartRecording = !mStartRecording;
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO},
                    10);
        } else {
            onRecord(mStartRecording);
        }
    }

    @OnLongClick(R.id.image_view_foot_print)
    public void getInstructions() {

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
}
