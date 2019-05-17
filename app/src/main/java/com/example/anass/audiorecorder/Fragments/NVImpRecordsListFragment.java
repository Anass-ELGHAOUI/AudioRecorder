package com.example.anass.audiorecorder.Fragments;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.anass.audiorecorder.Activities.MainActivity;
import com.example.anass.audiorecorder.Adapters.ImpRecordsAdapter;
import com.example.anass.audiorecorder.Adapters.NVImpRecordsAdapter;
import com.example.anass.audiorecorder.Database.DataBase;
import com.example.anass.audiorecorder.Database.Repositories.ImportantRecordRepository;
import com.example.anass.audiorecorder.Helper.OnLoadCompleted;
import com.example.anass.audiorecorder.Helper.OnSwipeTouchListener;
import com.example.anass.audiorecorder.Managers.RecyclerViewManager;
import com.example.anass.audiorecorder.Models.ImportantRecord;
import com.example.anass.audiorecorder.Models.RecordingItem;
import com.example.anass.audiorecorder.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static butterknife.ButterKnife.unbind;


public class NVImpRecordsListFragment extends Fragment implements OnLoadCompleted {

    @Bind(R.id.iv_imp_record_list)
    public ImageView ivImpList;


    MainActivity activity;
    private List<ImportantRecord> importantRecordList = new ArrayList<>();
    private static int listPosition = 0;
    private int sizeList = 0;
    private TextToSpeech mTTS;

    ImportantRecordRepository.getRecordsAsyncTask recordsAsyncTask;
    DataBase db;
    private int idRecord;
    private RecordingItem recordingItem;
    public static final String ARGS_ID = "id_record";
    public static final String ARGS_ITEM = "item_record";

    public static NVImpRecordsListFragment newInstance(int idRecord, RecordingItem recordingItem) {
        NVImpRecordsListFragment fragment = new NVImpRecordsListFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_ID, idRecord);
        args.putSerializable(ARGS_ITEM, recordingItem);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.nv_important_record_list_fragment, container, false);
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

    public void init() {
        idRecord = getArguments().getInt(ARGS_ID);
        recordingItem = (RecordingItem) getArguments().getSerializable(ARGS_ITEM);
        getData();
        textToSpeechConfiguration();
        swipeConfiguration();
    }

    private void swipeConfiguration(){
        ivImpList.setOnTouchListener(new OnSwipeTouchListener(activity) {
            public void onSwipeTop() {
                if (mTTS.isSpeaking()) {
                    mTTS.stop();
                    mTTS.shutdown();
                }
                if(sizeList!=0){
                    activity.navigateTo(NVDisplayImpRecordFragment.newInstance(importantRecordList.get(listPosition),recordingItem));
                }
            }
            public void onSwipeRight() {
                if (mTTS.isSpeaking()) {
                    mTTS.stop();
                    mTTS.shutdown();
                }
                if(sizeList!=0) {
                    listPosition = (listPosition + 1) % sizeList;
                    textToSpeechConverter("Record Important " + listPosition + ". pour choisir ce record glisser vers le haut, glisser vers la droite pour avancer, glisser vers la gauche pour revenir en arriére. glisser vers le bas pour retourner.");
                }
            }
            public void onSwipeLeft() {
                if (mTTS.isSpeaking()) {
                    mTTS.stop();
                    mTTS.shutdown();
                }
                if(sizeList!=0){
                    listPosition = (listPosition - 1 + sizeList )%sizeList;
                    textToSpeechConverter("Record Important "+listPosition+". pour choisir ce record glisser vers le haut, glisser vers la droite pour avancer, glisser vers la gauche pour revenir en arriére. glisser vers le bas pour retourner.");
                }
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
                        if(sizeList==0){
                            textToSpeechConverter("pas de record important. glisser vers le bas pour retourner.");
                        }else{
                            textToSpeechConverter("Record Important "+listPosition+". pour choisir ce record glisser vers le haut, glisser vers la droite pour avancer, glisser vers la gauche pour revenir en arriére. glisser vers le bas pour retourner.");
                        }

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

    @Override
    public void OnLoadCompleted() {
        refreshRecyclerView();
    }

    public void getData() {
        db = DataBase.getInstance(activity.getApplicationContext());
        recordsAsyncTask = new ImportantRecordRepository.getRecordsAsyncTask(db.importantRecordDao(), this);
        recordsAsyncTask.execute(idRecord);
    }

    private void refreshRecyclerView() {
        List<ImportantRecord> recordingItems = recordsAsyncTask.getImportantRecords();
        if (recordingItems != null && recordingItems.size() > 0) {
            importantRecordList = recordingItems;
            sizeList = importantRecordList.size();
        }
    }

}

