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
import com.example.anass.audiorecorder.Database.Repositories.RecordRepository;
import com.example.anass.audiorecorder.Helper.OnLoadCompleted;
import com.example.anass.audiorecorder.Helper.OnSwipeTouchListener;
import com.example.anass.audiorecorder.Models.RecordingItem;
import com.example.anass.audiorecorder.R;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RecordsListFragment extends Fragment implements OnLoadCompleted {

    @Bind(R.id.record_iv_nv_choice)
    public ImageView ivChoice;

    MainActivity activity;
    RecordRepository.getRecordsAsyncTask recordsAsyncTask;
    DataBase db;
    List<RecordingItem> items;
    int recordIndex = -1;
    int recordsSize = 0;
    private TextToSpeech mTTS;
    private static final String TAG = "RecordsListFragment";


    public static RecordsListFragment newInstance() {
        RecordsListFragment fragment = new RecordsListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.records_list_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "Lunched");
        activity = (MainActivity) getActivity();
        init();
    }

    public void init() {
        getData();
        textToSpeechConfiguration();
        swipeConfiguration();
    }

    public void getData() {
        db = DataBase.getInstance(activity.getApplicationContext());
        recordsAsyncTask = new RecordRepository.getRecordsAsyncTask(db.recordDao(), this);
        recordsAsyncTask.execute();
    }

    @Override
    public void OnLoadCompleted() {
        items = recordsAsyncTask.getRecords();
        recordsSize = items.size();
        Log.i(TAG, "OnLoadCompleted: list size = " + recordsSize);
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
                        textToSpeechConverter("Il y'a " + items.size() + " enregistrements. Pour naviguer glissez à droite ou à gauche." +
                                "Pour revenir au menu précédent glissez vers le bas. Pour séléctionner un record glissez vers le haut." +
                                "Long clique pour écouter la consigne.");                    }
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
                if(recordIndex == -1) {
                    recordIndex += 1;
                }
                activity.navigateTo(NvDisplayRecordFragment.newInstance(items.get(recordIndex)));
            }
            public void onSwipeRight() {
                if (mTTS.isSpeaking()) {
                    mTTS.stop();
                    mTTS.shutdown();
                }
                recordIndex = (recordIndex + 1) % recordsSize;
                textToSpeechConverter("Record " + (recordIndex + 1) + ". Titre du record: " + items.get(recordIndex).getName() + "." +
                        " Pour séléctionner ce record glissez vers le haut.");
            }
            public void onSwipeLeft() {
                if (mTTS.isSpeaking()) {
                    mTTS.stop();
                    mTTS.shutdown();
                }
                if (recordIndex == -1) {
                    recordIndex += 1;
                }
                recordIndex = (recordsSize + (recordIndex - 1)) % recordsSize;
                textToSpeechConverter("Record " + (recordIndex + 1) + ". Titre du record: " + items.get(recordIndex).getName() + "." +
                        " Pour séléctionner ce record glissez vers le haut.");
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

    /*@Bind(R.id.records_recycler)
    public RecyclerView mainRecycler;

    MainActivity activity;
    FileViewerAdapter adapter;
    RecordRepository.getRecordsAsyncTask recordsAsyncTask;
    DataBase db;

    public static RecordsListFragment newInstance() {
        RecordsListFragment fragment = new RecordsListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.records_list_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i("Recycler fragment", "lanched");
        activity = (MainActivity) getActivity();
        init();
    }

    public void init(){
        adapter = new FileViewerAdapter(activity);
        RecyclerViewManager.configureRecycleView(activity,mainRecycler);
        mainRecycler.setAdapter(adapter);
        getData();
    }

    @Override
    public void OnLoadCompleted() {
        refreshRecyclerView();
    }

    public void getData(){
        db = DataBase.getInstance(activity.getApplicationContext());
        recordsAsyncTask = new RecordRepository.getRecordsAsyncTask(db.recordDao(),this);
        recordsAsyncTask.execute();
    }

    private void refreshRecyclerView() {
        List<RecordingItem> recordingItems = recordsAsyncTask.getRecords();
        if(recordingItems!= null && recordingItems.size()>0){
            adapter.addAllItems(recordingItems);
        }
    }*/
}
