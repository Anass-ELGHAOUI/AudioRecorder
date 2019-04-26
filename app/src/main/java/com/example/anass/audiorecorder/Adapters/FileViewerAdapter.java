package com.example.anass.audiorecorder.Adapters;

import android.arch.persistence.room.Database;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.ContactsContract;
import android.speech.tts.TextToSpeech;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anass.audiorecorder.Activities.MainActivity;
import com.example.anass.audiorecorder.Database.DataBase;
import com.example.anass.audiorecorder.Database.Repositories.RecordRepository;
import com.example.anass.audiorecorder.Fragments.ImportantRecordsListFragment;
import com.example.anass.audiorecorder.Models.RecordingItem;
import com.example.anass.audiorecorder.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by Anass on 04/16/2019.
 */
public class FileViewerAdapter extends RecyclerView.Adapter<FileViewerAdapter.RecordingsViewHolder> {

    List<RecordingItem> liste;
    MainActivity activity;
    private static final String LOG_TAG = "FileViewerAdapter";

    public FileViewerAdapter(MainActivity activity, List<RecordingItem> liste) {
        super();
        this.liste = new ArrayList<>();
        this.liste.addAll(liste);
        this.activity = activity;
    }

    public FileViewerAdapter(MainActivity activity) {
        super();
        this.liste = new ArrayList<>();
        this.activity = activity;
    }

    @Override
    public void onBindViewHolder(final RecordingsViewHolder holder, int position) {

        RecordingItem item = getItem(position);
        long itemDuration = item.getLength();

        long minutes = TimeUnit.MILLISECONDS.toMinutes(itemDuration);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(itemDuration)
                - TimeUnit.MINUTES.toSeconds(minutes);

        holder.vName.setText(item.getName());
        holder.vLength.setText(String.format("%02d:%02d", minutes, seconds));
        holder.vFilePath.setText(item.getFilePath());
       /* holder.vDateAdded.setText(
                DateUtils.formatDateTime(
                        activity.getApplicationContext(),
                        item.getTime(),
                        DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_YEAR
                )
        ); */
    }

    @Override
    public RecordingsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.record_item, parent, false);
        return new RecordingsViewHolder(itemView, activity);
    }

    public static class RecordingsViewHolder extends RecyclerView.ViewHolder {
        private MainActivity activity;
        protected TextView vName;
        protected TextView vLength;
        protected TextView vDateAdded;
        protected  TextView vFilePath;
        private TextToSpeech mTTS;
        private boolean isClicked = false;
        private MediaPlayer mediaPlayer;



        public RecordingsViewHolder(View v, final MainActivity context) {
            super(v);
            vName =  v.findViewById(R.id.file_name_text);
            vLength = v.findViewById(R.id.file_length_text);
            vDateAdded =  v.findViewById(R.id.file_date_added_text);
            vFilePath = v.findViewById(R.id.file_path_text);
            this.activity = context;

            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mTTS != null && mTTS.isSpeaking()) {
                        mTTS.stop();
                        mTTS.shutdown();
                    }
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                        mediaPlayer = null;
                    }
                    mediaPlayer = new MediaPlayer();

                    try {
                        mediaPlayer.setDataSource(context.getApplicationContext(), Uri.parse(vFilePath.getText().toString()));

                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(LOG_TAG, "prepare() failed");
                    }
                    return true;
                }
            });

            v.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                        mediaPlayer = null;
                    } else {
                        mediaPlayer = null;
                    }
                    if(isClicked == false){

                        mTTS = new TextToSpeech(activity.getApplicationContext(), new TextToSpeech.OnInitListener() {
                            @Override
                            public void onInit(int status) {
                                if (status == TextToSpeech.SUCCESS) {
                                    int result = mTTS.setLanguage(Locale.FRENCH);
                                    if (result == TextToSpeech.LANG_MISSING_DATA
                                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                                        Log.e("TTS", "Language not supported");
                                    } else {
                                        mTTS.speak(vName.getText() + ". Long click pour écouter le record, clicker une autre fois pour afficher la liste des records important" +
                                                "relatifs à ce record", TextToSpeech.QUEUE_FLUSH, null, null);
                                    }
                                } else {
                                    Log.e("TTS", "Initialization failed");
                                }
                            }
                        });
                        isClicked = true;
                        Log.i(LOG_TAG, "isClicked: " + isClicked);
                    }else if (isClicked == true){
                        isClicked = false;
                        Log.i(LOG_TAG,"ADAPTER POSITION: " + getAdapterPosition());
                        activity.navigateTo(ImportantRecordsListFragment.newInstance(getAdapterPosition()));
                    }

                }
            });

        }

    }


    @Override
    public int getItemCount() {
        return liste.size();
    }

    public RecordingItem getItem(int position) {
        return liste.get(position);
    }

    public void addAllItems(List<RecordingItem> liste) {
        this.liste.addAll(liste);
        notifyDataSetChanged();
    }


}
