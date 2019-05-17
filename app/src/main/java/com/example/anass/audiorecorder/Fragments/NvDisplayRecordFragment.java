package com.example.anass.audiorecorder.Fragments;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.anass.audiorecorder.Activities.MainActivity;
import com.example.anass.audiorecorder.Database.Repositories.RecordRepository;
import com.example.anass.audiorecorder.Helper.OnSwipeTouchListener;
import com.example.anass.audiorecorder.Models.RecordingItem;
import com.example.anass.audiorecorder.R;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NvDisplayRecordFragment extends Fragment {

    private RecordingItem item;

    @Bind(R.id.choosen_record_iv_nv_choice)
    public ImageView ivChoice;

    MainActivity activity;

    private MediaPlayer mediaPlayer;

    private static final String TAG = "NvDisplayRecordFragment";

    public static final String key = "com.example.anass.audiorecorder.Fragments.ChoosenRecord";

    public static final String consigne = "Pour lancer le record glissez vers le haut. Pour consulter la liste des record important" +
            "glissez vers la droite. Pour supprimer le record glissez ves la gauche." +
            "Pour revenir à la liste des records glissez vers le bas.";
    private TextToSpeech mTTS;
    RecordRepository recordsAsyncTask;

    public static NvDisplayRecordFragment newInstance(RecordingItem recordingItem) {
        NvDisplayRecordFragment fragment = new NvDisplayRecordFragment();
        Bundle args = new Bundle();
        args.putSerializable(key, recordingItem);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.nv_choosen_record_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "Lunched");
        item = (RecordingItem) savedInstanceState.getSerializable(key);
        activity = (MainActivity) getActivity();
        init();
    }

    private void init() {
        recordsAsyncTask = new RecordRepository(activity.getApplication());
        textToSpeechConfiguration();
        swipeConfiguration();
    }

    private void textToSpeechConfiguration(){
        mTTS = new TextToSpeech(activity, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = mTTS.setLanguage(Locale.FRANCE);

                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    } else {
                        textToSpeechConverter(consigne);                    }
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void swipeConfiguration(){
        ivChoice.setOnTouchListener(new OnSwipeTouchListener(activity) {
            public void onSwipeTop() {
                if (mTTS.isSpeaking()) {
                    mTTS.stop();
                    mTTS.shutdown();
                }
                if (mediaPlayer.isPlaying()) {
                    stopPlayingAndSetConsigne();
                }
                mediaPlayer = new MediaPlayer();

                try {
                    mediaPlayer.setDataSource(activity.getApplicationContext(), Uri.parse(item.getFilePath()));
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "prepare() failed");
                }

            }
            public void onSwipeRight() {
                if (mTTS.isSpeaking()) {
                    mTTS.stop();
                    mTTS.shutdown();
                }
                if (mediaPlayer.isPlaying()) {
                    stopPlayingAndSetConsigne();
                } else {
                    activity.navigateTo(NVImpRecordsListFragment.newInstance(item.getId(), item));
                }

            }
            public void onSwipeLeft() {
                if (mTTS.isSpeaking()) {
                    mTTS.stop();
                    mTTS.shutdown();
                }
                if (mediaPlayer.isPlaying()) {
                    stopPlayingAndSetConsigne();
                } else {
                    recordsAsyncTask.deleteRecord(item);
                    textToSpeechConverter("Record Supprimé");
                    activity.navigateTo(RecordsListFragment.newInstance());
                }
            }
            public void onSwipeBottom() {
                if (mTTS.isSpeaking()) {
                    mTTS.stop();
                    mTTS.shutdown();
                }
                if (mediaPlayer.isPlaying()) {
                    stopPlayingAndSetConsigne();
                } else {
                    activity.onBackPressed();
                }
            }

        });
    }

    public void stopPlayingAndSetConsigne() {
        stopRecord();
        textToSpeechConverter(consigne);
    }

    public void stopRecord() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer = null;
        } else {
            mediaPlayer = null;
        }
    }

    private void textToSpeechConverter(String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ttsGreater21(text);
        } else {
            ttsUnder20(text);
        }
    }

    @SuppressWarnings("deprecation")
    private void ttsUnder20(String text) {
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, map);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ttsGreater21(String text) {
        String utteranceId=this.hashCode() + "";
        mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }

}
