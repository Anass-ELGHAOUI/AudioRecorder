package com.example.anass.audiorecorder.Helper;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.anass.audiorecorder.Activities.MainActivity;
import com.example.anass.audiorecorder.Database.Repositories.RecordRepository;
import com.example.anass.audiorecorder.Models.RecordingItem;
import com.example.anass.audiorecorder.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.anass.audiorecorder.App.CHANNEL_ID;

/**
 * Created by Anass on 16/03/2019.
 */
public class RecordingService extends Service implements OnLoadCompleted{

    private static final String LOG_TAG = "RecordingService";

    private String mFileName = null;
    private String mFilePath = null;

    private MediaRecorder mRecorder = null;

    private long mStartingTimeMillis = 0;
    private long mElapsedMillis = 0;
    private int mElapsedSeconds = 0;
    private OnTimerChangedListener onTimerChangedListener = null;
    private static final SimpleDateFormat mTimerFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());

    private Timer mTimer = null;
    private TimerTask mIncrementTimerTask = null;

    private RecordRepository mRecordRepository;

    private RecordRepository.getRecordsAsyncTask getRecordsAsyncTask;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void OnLoadCompleted() {
        List<RecordingItem> list = getRecordsAsyncTask.getRecords();
        Log.i("NumberOfRecords", String.valueOf(getRecordsAsyncTask.getRecords().size()));

    }

    public interface OnTimerChangedListener {
        void onTimerChanged(int seconds);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mRecordRepository = new RecordRepository(getApplication());
        mRecordRepository = new RecordRepository(getApplication());
       // mDatabase = new DBHelper(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startRecording();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mRecorder != null) {
            stopRecording();
        }

        super.onDestroy();
    }

    public void startRecording() {
        setFileNameAndPath();

        Log.i("startRecording","started");

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFilePath);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setAudioSamplingRate(44100);
        mRecorder.setAudioEncodingBitRate(192000);
        mRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION) ;
        mRecorder.setAudioChannels(1);

       /* if (MySharedPreferences.getPrefHighQuality(this)) {
            mRecorder.setAudioSamplingRate(44100);
            mRecorder.setAudioEncodingBitRate(192000);
        } */

        try {
            mRecorder.prepare();
            mRecorder.start();
            mStartingTimeMillis = System.currentTimeMillis();

            //startTimer();
            //startForeground(1, createNotification());

        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    public void setFileNameAndPath(){
        int count = 0;
        File f;

        do{
            count++;
            mFilePath = getExternalCacheDir().getAbsolutePath();
            mFilePath += "/audiorecordtest"+count+".mp3";
            f = new File(mFilePath);
        }while (f.exists() && !f.isDirectory());

    }

    public void stopRecording() {

        try{
            mRecorder.stop();
            mElapsedMillis = (System.currentTimeMillis() - mStartingTimeMillis);
            mRecorder.release();
            Toast.makeText(this, getString(R.string.toast_recording_finish) + " " + mFilePath, Toast.LENGTH_LONG).show();

            //remove notification
            if (mIncrementTimerTask != null) {
                mIncrementTimerTask.cancel();
                mIncrementTimerTask = null;
            }
            mRecorder = null;
        }catch(RuntimeException stopException){
            //handle cleanup here
        }


        try {
            //mDatabase.addRecording(mFileName, mFilePath, mElapsedMillis);
            mRecordRepository.addRecord(new RecordingItem(mFileName, mFilePath, mElapsedMillis));
            getRecordsAsyncTask = new RecordRepository.getRecordsAsyncTask(mRecordRepository.getRecordDao(), this);
            getRecordsAsyncTask.execute();
        } catch (Exception e){
            Log.e(LOG_TAG, "exception", e);
        }
    }

    private void startTimer() {
        mTimer = new Timer();
        mIncrementTimerTask = new TimerTask() {
            @Override
            public void run() {
                mElapsedSeconds++;
                if (onTimerChangedListener != null)
                    onTimerChangedListener.onTimerChanged(mElapsedSeconds);
                NotificationManager mgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mgr.notify(1, createNotification());
            }
        };
        mTimer.scheduleAtFixedRate(mIncrementTimerTask, 1000, 1000);
    }

    //TODO:
    private Notification createNotification() {
        Intent mIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, mIntent, 0);
        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Notification mNotification = new  NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_mic_white_36dp)
                .setContentTitle(getString(R.string.notification_recording))
                .setContentText(mTimerFormat.format(mElapsedSeconds * 1000))
                .setOngoing(true)
                .setChannelId(CHANNEL_ID)
                .setContentIntent(mPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();
        /*NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.mipmap.ic_mic_white_36dp)
                        .setContentTitle(getString(R.string.notification_recording))
                        .setContentText(mTimerFormat.format(mElapsedSeconds * 1000))
                        .setOngoing(true);

        mBuilder.setContentIntent(PendingIntent.getActivities(getApplicationContext(), 0,
                new Intent[]{new Intent(getApplicationContext(), MainActivity.class)}, 0));

        return mBuilder.build();*/
       return mNotification;
    }
}
