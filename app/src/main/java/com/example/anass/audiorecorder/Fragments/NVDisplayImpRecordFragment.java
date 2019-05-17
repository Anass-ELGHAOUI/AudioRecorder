package com.example.anass.audiorecorder.Fragments;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
        import android.app.Fragment;
        import android.media.MediaPlayer;
        import android.net.Uri;
        import android.os.Build;
        import android.os.Bundle;
        import android.os.CountDownTimer;
        import android.speech.tts.TextToSpeech;
        import android.support.annotation.Nullable;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ImageView;

        import com.example.anass.audiorecorder.Activities.MainActivity;
        import com.example.anass.audiorecorder.Helper.OnSwipeTouchListener;
        import com.example.anass.audiorecorder.Helper.Utils;
        import com.example.anass.audiorecorder.Models.ImportantRecord;
        import com.example.anass.audiorecorder.Models.RecordingItem;
        import com.example.anass.audiorecorder.R;

        import java.io.IOException;
        import java.util.HashMap;
        import java.util.Locale;

        import butterknife.Bind;
        import butterknife.ButterKnife;
        import butterknife.OnLongClick;

        import static butterknife.ButterKnife.bind;
        import static butterknife.ButterKnife.unbind;

public class NVDisplayImpRecordFragment extends Fragment{

    @Bind(R.id.iv_nv_choice)
    public ImageView ivChoice;

    MainActivity activity;
    private TextToSpeech mTTS;
    private MediaPlayer mediaPlayer;
    private RecordingItem recordingItem;
    private ImportantRecord importantRecord;

    public static final String IMPRECORD_ARGS = "impRecord";
    public static final String RECORDITEM_ARGS = "recordItem";

    public static NVDisplayImpRecordFragment newInstance(ImportantRecord importantRecord, RecordingItem recordingItem) {
        NVDisplayImpRecordFragment fragment = new NVDisplayImpRecordFragment();
        Bundle args = new Bundle();
        args.putSerializable(IMPRECORD_ARGS,importantRecord);
        args.putSerializable(RECORDITEM_ARGS,recordingItem);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.nv_choice_fragment, container, false);
        bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i("nv choice fragment", "lunched");
        activity = (MainActivity) getActivity();
        recordingItem = (RecordingItem) getArguments().getSerializable(RECORDITEM_ARGS);
        importantRecord = (ImportantRecord) getArguments().getSerializable(IMPRECORD_ARGS);
        init();
    }

    public void init(){
        textToSpeechConfiguration();
        swipeConfiguration();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void swipeConfiguration(){
        ivChoice.setOnTouchListener(new OnSwipeTouchListener(activity) {
            public void onSwipeTop() {
                if (mTTS.isSpeaking()) {
                    mTTS.stop();
                    mTTS.shutdown();
                }
                if(mediaPlayer != null && mediaPlayer.isPlaying()){
                    mediaPlayer.stop();
                    textToSpeechConverter("pour lancer le record glisser vers le haut, pour supprimer le record important glisser vers la gauche, pour retourner glisser vers le bas.");
                }else{
                    displayRecord();
                }

            }
            public void onSwipeRight() {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.stop();
                    textToSpeechConverter("pour lancer le record glisser vers le haut, pour supprimer le record important glisser vers la gauche, pour retourner glisser vers le bas.");
                }
            }
            public void onSwipeLeft() {
                if (mTTS.isSpeaking()) {
                    mTTS.stop();
                    mTTS.shutdown();
                }
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.stop();
                    textToSpeechConverter("pour lancer le record glisser vers le haut, pour supprimer le record important glisser vers la gauche, pour retourner glisser vers le bas.");
                }
               //delete record
            }
            public void onSwipeBottom() {
                if (mTTS.isSpeaking()) {
                    mTTS.stop();
                    mTTS.shutdown();
                }
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.stop();
                    textToSpeechConverter("pour lancer le record glisser vers le haut, pour supprimer le record important glisser vers la gauche, pour retourner glisser vers le bas.");
                }
                else{
                    activity.onBackPressed();
                }

            }

        });
    }

    private void displayRecord() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer = null;
        } else {
            mediaPlayer = null;
        }
        mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(activity.getApplicationContext(), Uri.parse(recordingItem.getFilePath()));
            mediaPlayer.prepare();
            mediaPlayer.seekTo((int) (importantRecord.getStartTime() - recordingItem.getStart()));
            mediaPlayer.start();
            new CountDownTimer(importantRecord.getStopTime() - importantRecord.getStartTime(), 1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onFinish() {
                    // TODO Auto-generated method stub
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                    }

                }
            }.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                        textToSpeechConverter("pour lancer le record glisser vers le haut, pour supprimer le record important glisser vers la gauche, pour retourner glisser vers le bas.");                    }
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }
        });
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbind(this);
        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }
    }
}

