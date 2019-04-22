package com.example.anass.audiorecorder.Adapters;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anass.audiorecorder.Activities.MainActivity;
import com.example.anass.audiorecorder.Models.RecordingItem;
import com.example.anass.audiorecorder.R;

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
        return new RecordingsViewHolder(itemView, activity.getApplicationContext());
    }

    public static class RecordingsViewHolder extends RecyclerView.ViewHolder {
        private Context activity;
        protected TextView vName;
        protected TextView vLength;
        protected TextView vDateAdded;
        private TextToSpeech mTTS;


        public RecordingsViewHolder(View v, final Context context) {
            super(v);
            vName =  v.findViewById(R.id.file_name_text);
            vLength = v.findViewById(R.id.file_length_text);
            vDateAdded =  v.findViewById(R.id.file_date_added_text);
            this.activity = context;
            v.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mTTS = new TextToSpeech(activity.getApplicationContext(), new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            if (status == TextToSpeech.SUCCESS) {
                                int result = mTTS.setLanguage(Locale.FRENCH);
                                if (result == TextToSpeech.LANG_MISSING_DATA
                                        || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                                    Log.e("TTS", "Language not supported");
                                } else {
                                    mTTS.speak("" + vName.getText(), TextToSpeech.QUEUE_FLUSH, null, null);
                                    mTTS.speak("Pour afficher les enregistrements importants relatifs à ce record glisser vers la droite, pour revenir à l'acceuil glisser vers la gauche", TextToSpeech.QUEUE_FLUSH, null, null);
                                }
                            } else {
                                Log.e("TTS", "Initialization failed");
                            }
                        }
                    });
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
