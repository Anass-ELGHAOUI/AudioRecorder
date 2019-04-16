package com.example.anass.audiorecorder.Fragments;

import android.annotation.TargetApi;
import android.app.Fragment;
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
import com.example.anass.audiorecorder.Helper.OnSwipeTouchListener;
import com.example.anass.audiorecorder.Helper.Utils;
import com.example.anass.audiorecorder.R;

import java.util.HashMap;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnLongClick;

public class NVChoiceFragment extends Fragment{

    @Bind(R.id.iv_nv_choice)
    public ImageView ivChoice;

    MainActivity activity;
    private TextToSpeech mTTS;

    public static NVChoiceFragment newInstance() {
        NVChoiceFragment fragment = new NVChoiceFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.nv_choice_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i("nv choice fragment", "lanched");
        activity = (MainActivity) getActivity();
        init();
    }

    public void init(){
        textToSpeechConfiguration();
        swipeConfiguration();
    }

    private void swipeConfiguration(){
        ivChoice.setOnTouchListener(new OnSwipeTouchListener(activity) {
            public void onSwipeTop() {
                activity.navigateTo(RecordsListFragment.newInstance());
            }
            public void onSwipeRight() {
                activity.onBackPressed();
            }
            public void onSwipeLeft() {
                if (mTTS.isSpeaking()) {
                    mTTS.stop();
                    mTTS.shutdown();
                }
                activity.navigateTo(NVRecordFragment.newInstance());
            }
            public void onSwipeBottom() {
                if (mTTS.isSpeaking()) {
                    mTTS.stop();
                    mTTS.shutdown();
                }
                textToSpeechConverter("pour un nouveau record glisser vers la gauche, pour consulter la liste des enregistrements glisser vers le haut. Pour revenir glisser vers la droite.  glisser vers le bas pour écouter pour écouter le consigne ");
            }

        });
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
                        textToSpeechConverter("pour enregistrer un nouveau record glisser vers la gauche, pour consulter la liste des enregistrements glisser vers le haut. Pour revenir glisser vers la droite.  glisser vers le bas pour écouter pour écouter le consigne ");                    }
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
        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }
    }
}
