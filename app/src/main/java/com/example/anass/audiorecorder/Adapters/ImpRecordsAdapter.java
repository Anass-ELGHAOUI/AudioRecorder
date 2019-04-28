package com.example.anass.audiorecorder.Adapters;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anass.audiorecorder.Activities.MainActivity;
import com.example.anass.audiorecorder.Helper.Utils;
import com.example.anass.audiorecorder.Models.ImportantRecord;
import com.example.anass.audiorecorder.Models.RecordingItem;
import com.example.anass.audiorecorder.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ImpRecordsAdapter extends RecyclerView.Adapter<ImpRecordsAdapter.RecordingsViewHolder> {
    List<ImportantRecord> liste;
    MainActivity activity;
    private RecordingItem recordingItem;
    public static final String LOG_TAG = "ImportantRecordAdapter";

    public ImpRecordsAdapter(MainActivity activity, List<ImportantRecord> liste) {
        super();
        this.liste = new ArrayList<>();
        this.liste.addAll(liste);
        this.activity = activity;
    }

    public ImpRecordsAdapter(MainActivity activity, RecordingItem recordingItem) {
        super();
        this.recordingItem = recordingItem;
        this.liste = new ArrayList<>();
        this.activity = activity;
    }

    @Override
    public void onBindViewHolder(final RecordingsViewHolder holder, int position) {

        ImportantRecord item = getItem(position);
        long itemDuration = item.getStopTime() - item.getStartTime();

        long minutes = TimeUnit.MILLISECONDS.toMinutes(itemDuration);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(itemDuration)
                - TimeUnit.MINUTES.toSeconds(minutes);

        holder.vName.setText("Important " + (position + 1));
        holder.vLength.setText(String.format("%02d:%02d", minutes, seconds));
        holder.mImportantRecordStart = item.getStartTime();
        holder.mPath = recordingItem.getFilePath();
        holder.mRecordStart = recordingItem.getStart();
        holder.mImportantRecordEnd = item.getStopTime();
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
        protected long mImportantRecordStart;
        protected long mImportantRecordEnd;
        protected long mRecordStart;
        protected String mPath;
        static protected MediaPlayer mediaPlayer;

        public RecordingsViewHolder(View v, final Context context) {
            super(v);
            vName = v.findViewById(R.id.file_name_text);
            vLength = v.findViewById(R.id.file_length_text);
            vDateAdded = v.findViewById(R.id.file_date_added_text);
            this.activity = context;
            v.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                        mediaPlayer = null;
                    } else {
                        mediaPlayer = null;
                    }
                    mediaPlayer = new MediaPlayer();

                    try {
                        Log.e(LOG_TAG, mPath);
                        mediaPlayer.setDataSource(context.getApplicationContext(), Uri.parse(mPath));
                        Utils.makeToast(activity,""+(mImportantRecordStart - mRecordStart));
                        mediaPlayer.prepare();
                        mediaPlayer.seekTo((int) (mImportantRecordStart - mRecordStart));
                        mediaPlayer.start();
                        new CountDownTimer(mImportantRecordEnd-mImportantRecordStart,1000) {

                            @Override
                            public void onTick(long millisUntilFinished) {
                                // TODO Auto-generated method stub

                            }

                            @Override
                            public void onFinish() {
                                // TODO Auto-generated method stub
                                if(mediaPlayer.isPlaying())
                                {
                                    mediaPlayer.stop();
                                }

                            }
                        }.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(LOG_TAG, "prepare() failed");
                    }
                }
            });
        }

    }


    @Override
    public int getItemCount() {
        return liste.size();
    }

    public ImportantRecord getItem(int position) {
        return liste.get(position);
    }

    public void addAllItems(List<ImportantRecord> liste) {
        this.liste.addAll(liste);
        notifyDataSetChanged();
    }
}


