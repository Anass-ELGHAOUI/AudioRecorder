package com.example.anass.audiorecorder.Fragments;

import android.annotation.SuppressLint;
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
import com.example.anass.audiorecorder.Database.DataBase;
import com.example.anass.audiorecorder.Database.Repositories.ChoiceRepository;
import com.example.anass.audiorecorder.Helper.OnSwipeTouchListener;
import com.example.anass.audiorecorder.Helper.Utils;
import com.example.anass.audiorecorder.Models.Choice;
import com.example.anass.audiorecorder.R;

import java.util.HashMap;
import java.util.Locale;

import butterknife.Bind;
import butterknife.OnLongClick;

import static butterknife.ButterKnife.bind;
import static butterknife.ButterKnife.unbind;

public class ModeChoiceFragment extends Fragment {

    @Bind(R.id.iv_choice)
    public ImageView ivChoice;

    MainActivity activity;
    private TextToSpeech mTTS;
    private Choice mChoice;
    ChoiceRepository choiceRepository;

    public static ModeChoiceFragment newInstance() {
        ModeChoiceFragment fragment = new ModeChoiceFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mode_choice_fragment, container, false);
        bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i("login activity ", "login lanched");
        activity = (MainActivity) getActivity();
        init();
    }

    public void init() {
        textToSpeechConfiguration();
        swipeConfiguration();
        choiceRepository = new ChoiceRepository(activity.getApplication());
        mChoice = new Choice();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void swipeConfiguration() {
        ivChoice.setOnTouchListener(new OnSwipeTouchListener(activity) {
            public void onSwipeTop() {
                Utils.makeToast(activity, "top");
            }

            public void onSwipeRight() {
                if (mTTS.isSpeaking()) {
                    mTTS.stop();
                    mTTS.shutdown();
                }

                mChoice.setChoix(1);
                choiceRepository.addChoice(mChoice);
                activity.navigateTo(NVChoiceFragment.newInstance());
            }

            public void onSwipeLeft() {

                //activity.navigateTo(RecordFragment.newInstance());
                if (mTTS.isSpeaking()) {
                    mTTS.stop();
                    mTTS.shutdown();
                }
                mChoice.setChoix(0);
                choiceRepository.addChoice(mChoice);
                activity.navigateTo(RecordFragmentVoyant.newInstance());
            }

            public void onSwipeBottom() {
                if (mTTS.isSpeaking()) {
                    mTTS.stop();
                    mTTS.shutdown();
                }
                activity.onBackPressed();
            }

        });
    }

    private void textToSpeechConfiguration() {
        mTTS = new TextToSpeech(activity, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = mTTS.setLanguage(Locale.FRANCE);

                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    } else {
                        textToSpeechConverter("Acceuil. Pour l'utilisation de mode normal glisser vers la gauche sinon glisser vers la droite. pour quitter glissez vers le bas.  Appuyer longtemps pour écouter les consigne ");
                    }
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
        String utteranceId = this.hashCode() + "";
        mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }

    @OnLongClick(R.id.iv_choice)
    public boolean onReplay() {
        textToSpeechConverter("Acceuil. Pour l'utilisation de mode normal glisser vers la gauche sinon glisser vers la droite. " +
                "Pour quitter glissez vers la bas.  Appuyer longtemps pour écouter les consignes ");
        return true;
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
