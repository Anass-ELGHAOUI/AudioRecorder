package com.example.anass.audiorecorder.Fragments;

import android.Manifest;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anass.audiorecorder.Activities.MainActivity;
import com.example.anass.audiorecorder.Database.DataBase;
import com.example.anass.audiorecorder.Database.Repositories.ImportantRecordRepository;
import com.example.anass.audiorecorder.Database.Repositories.RecordRepository;
import com.example.anass.audiorecorder.Helper.OnLoadCompleted;
import com.example.anass.audiorecorder.Helper.RecordingService;
import com.example.anass.audiorecorder.Helper.Utils;
import com.example.anass.audiorecorder.Models.ImportantRecord;
import com.example.anass.audiorecorder.R;
import com.melnykov.fab.FloatingActionButton;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class RecordFragment extends Fragment implements OnLoadCompleted {

    private MainActivity activity;

    RecordRepository mRecordRepository;

    ImportantRecordRepository mImportantRecordRepository;

    RecordRepository.getLastIdAsyncTask lastIdAsyncTask;

    DataBase db;

    ImportantRecord mImportantRecord;

    @Bind(R.id.btn_menu)
    ImageButton btnMenu;

    @Bind(R.id.btnRecord)
    FloatingActionButton mRecordButton;

    @Bind(R.id.btnStratEvaluation)
    FloatingActionButton startEvaluation;

    @Bind(R.id.btnStopEvaluation)
    FloatingActionButton StopEvaluation;

    @Bind(R.id.chronometer)
    Chronometer mChronometer;

    @Bind(R.id.recordProgressBar)
    ProgressBar recordProgressBar;

    @Bind(R.id.recording_status_text)
    public TextView mRecordingPrompt;

    private int mRecordPromptCount = 0;

    private boolean mStartRecording = false;

    private boolean mStartImpRecording = false;

    private static final String TAG = "RecordFragment";
    private static final String NAME_ARG = "nameRecord";
    public static String recordName;

    public static RecordFragment newInstance(String recordName) {
        RecordFragment fragment = new RecordFragment();
        Bundle args = new Bundle();
        args.putString(NAME_ARG,recordName);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.record_fragment, container, false);
        ButterKnife.bind(this, view);
        mRecordRepository = new RecordRepository(getActivity().getApplication());
        mImportantRecordRepository = new ImportantRecordRepository(getActivity().getApplication());
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "Lunched");
        activity = (MainActivity) getActivity();
        this.recordName = getArguments().getString(NAME_ARG);
        init();
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO},
                    10);
        }
    }

    public void init() {

        db = DataBase.getInstance(activity.getApplicationContext());
        lastIdAsyncTask = new RecordRepository.getLastIdAsyncTask(db.recordDao(), this);
        lastIdAsyncTask.execute();

        if (!mStartRecording) {
            startEvaluation.setVisibility(View.GONE);
            StopEvaluation.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.btnRecord)
    public void btnRecordOnClick() {
        mStartRecording = !mStartRecording;
        onRecord(mStartRecording);
        if (mStartImpRecording) {
            if (mImportantRecord != null) {
                mImportantRecord.setStopTime(System.currentTimeMillis());
                mImportantRecord.setRecordId(lastIdAsyncTask.getLastId() + 1);
                mImportantRecordRepository.addImportantRecord(mImportantRecord);
                startEvaluation.setVisibility(View.VISIBLE);
                StopEvaluation.setVisibility(View.GONE);
                Utils.makeToast(activity.getApplicationContext(), "Important record saved.");
                mStartImpRecording = !mStartImpRecording;
            }
        }

    }

    @OnClick(R.id.btnStratEvaluation)
    public void StartImprtantRecord() {
        mStartImpRecording = !mStartImpRecording;
        Log.i(TAG, "Important record started");
        Utils.makeToast(activity.getApplicationContext(), "Important record started.");
        mImportantRecord = new ImportantRecord();
        mImportantRecord.setStartTime(System.currentTimeMillis());
        startEvaluation.setVisibility(View.GONE);
        StopEvaluation.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.btnStopEvaluation)
    public void StopImprtantRecord() {
        if (mImportantRecord != null) {
            mImportantRecord.setStopTime(System.currentTimeMillis());
            mImportantRecord.setRecordId(lastIdAsyncTask.getLastId() + 1);
            mImportantRecordRepository.addImportantRecord(mImportantRecord);
            startEvaluation.setVisibility(View.VISIBLE);
            StopEvaluation.setVisibility(View.GONE);
            Utils.makeToast(activity.getApplicationContext(), "Important record saved.");
            mStartImpRecording = !mStartImpRecording;
        }
    }

    @OnClick(R.id.btn_back)
    public void btnBackOnClick() {
        activity.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 10) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onRecord(mStartRecording);
            } else {
                //User denied Permission.
            }
        }
    }


    // Recording Start/Stop
    //TODO: recording pause
    private void onRecord(boolean start) {

        Intent intent = new Intent(getActivity(), RecordingService.class);
        intent.putExtra("recordName", this.recordName);

        if (start) {
            // start recording
            mRecordButton.setImageResource(R.mipmap.ic_media_stop);
            //mPauseButton.setVisibility(View.VISIBLE);
            Toast.makeText(getActivity(), R.string.toast_recording_start, Toast.LENGTH_SHORT).show();
            File folder = new File(Environment.getExternalStorageDirectory() + "/SoundRecorder");
            if (!folder.exists()) {
                //folder /SoundRecorder doesn't exist, create the folder
                folder.mkdir();
            }
            //start Chronometer
            mChronometer.setBase(SystemClock.elapsedRealtime());
            mChronometer.start();
            mChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                @Override
                public void onChronometerTick(Chronometer chronometer) {
                    if (mRecordPromptCount == 0) {
                        mRecordingPrompt.setText(getString(R.string.record_in_progress) + ".");
                    } else if (mRecordPromptCount == 1) {
                        mRecordingPrompt.setText(getString(R.string.record_in_progress) + "..");
                    } else if (mRecordPromptCount == 2) {
                        mRecordingPrompt.setText(getString(R.string.record_in_progress) + "...");
                        mRecordPromptCount = -1;
                    }

                    mRecordPromptCount++;
                }
            });
            //start RecordingService
            getActivity().startService(intent);

            //keep screen on while recording
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            mRecordingPrompt.setText(getString(R.string.record_in_progress) + ".");
            mRecordPromptCount++;
            startEvaluation.setVisibility(View.VISIBLE);


        } else {
            //stop recording
            mRecordButton.setImageResource(R.mipmap.ic_mic_white_36dp);
            //mPauseButton.setVisibility(View.GONE);
            mChronometer.stop();
            mChronometer.setBase(SystemClock.elapsedRealtime());
            mRecordingPrompt.setText(getString(R.string.record_prompt));
            getActivity().stopService(intent);
            //allow the screen to turn off again once recording is finished
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            startEvaluation.setVisibility(View.GONE);
            StopEvaluation.setVisibility(View.GONE);
            activity.navigateTo(RecordFragmentVoyant.newInstance());
        }
    }

    @Override
    public void OnLoadCompleted() {

    }

}