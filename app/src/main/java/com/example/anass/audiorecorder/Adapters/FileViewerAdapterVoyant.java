package com.example.anass.audiorecorder.Adapters;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.anass.audiorecorder.Activities.MainActivity;
import com.example.anass.audiorecorder.Database.DataBase;
import com.example.anass.audiorecorder.Database.Repositories.RecordRepository;
import com.example.anass.audiorecorder.Fragments.ImportantRecordsListFragment;
import com.example.anass.audiorecorder.Models.RecordingItem;
import com.example.anass.audiorecorder.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class FileViewerAdapterVoyant extends RecyclerView.Adapter<FileViewerAdapterVoyant.RecordingsViewHolder> {

    List<RecordingItem> liste;
    MainActivity activity;
    private static final String LOG_TAG = "FileViewerAdapterVoyant";
    DataBase mDatabase;
    static RecordRepository recordRepository;

    public FileViewerAdapterVoyant(MainActivity activity, List<RecordingItem> liste) {
        super();
        this.liste = new ArrayList<>();
        this.liste.addAll(liste);
        this.activity = activity;
    }

    public FileViewerAdapterVoyant(MainActivity activity) {
        super();
        this.liste = new ArrayList<>();
        this.activity = activity;
        mDatabase = DataBase.getInstance(activity.getApplicationContext());
        recordRepository = new RecordRepository(activity.getApplication());
    }

    @Override
    public FileViewerAdapterVoyant.RecordingsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.record_item, parent, false);
        return new FileViewerAdapterVoyant.RecordingsViewHolder(itemView, activity, liste);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordingsViewHolder holder, int position) {
        RecordingItem item = getItem(position);
        long itemDuration = item.getEnd() - item.getStart();

        long minutes = TimeUnit.MILLISECONDS.toMinutes(itemDuration);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(itemDuration)
                - TimeUnit.MINUTES.toSeconds(minutes);

        holder.vName.setText(item.getName());
        holder.vLength.setText(String.format("%02d:%02d", minutes, seconds));
        holder.vFilePath.setText(item.getFilePath());
        holder.recordingItem = item;
    }

    public static class RecordingsViewHolder extends RecyclerView.ViewHolder {
        private MainActivity activity;
        protected TextView vName;
        protected TextView vLength;
        protected TextView vDateAdded;
        protected TextView vFilePath;
        protected RecordingItem recordingItem;
        private MediaPlayer mediaPlayer;
        private List<RecordingItem> privateList;


        public RecordingsViewHolder(View v, final MainActivity context, final List<RecordingItem> privateList) {
            super(v);
            vName = v.findViewById(R.id.file_name_text);
            vLength = v.findViewById(R.id.file_length_text);
            vDateAdded = v.findViewById(R.id.file_date_added_text);
            vFilePath = v.findViewById(R.id.file_path_text);
            this.activity = context;
            this.privateList = privateList;

            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(context.getApplicationContext(), v);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.shareRecord:
                                    Intent shareIntent = new Intent();
                                    shareIntent.setAction(Intent.ACTION_SEND);
                                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                                    StrictMode.setVmPolicy(builder.build());
                                    shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(privateList.get(getAdapterPosition()).getFilePath())));
                                    shareIntent.setType("audio/mp4");
                                    context.startActivity(Intent.createChooser(shareIntent, "Send to"));
                                    return true;
                                case R.id.importantRecords:
                                    int mPosition = getAdapterPosition();
                                    activity.navigateTo(ImportantRecordsListFragment.newInstance(mPosition + 1, privateList.get(mPosition)));
                                    return true;
                                case R.id.deleteRecord:
                                    recordRepository.deleteRecord(recordingItem);
                            }
                            return true;
                        }
                    });
                    popupMenu.inflate(R.menu.pop_up_menu);
                    popupMenu.show();
                    return true;
                }
            });

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
                        mediaPlayer.setDataSource(context.getApplicationContext(), Uri.parse(vFilePath.getText().toString()));

                        mediaPlayer.prepare();
                        mediaPlayer.start();
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

    public RecordingItem getItem(int position) {
        return liste.get(position);
    }

    public void addAllItems(List<RecordingItem> liste) {
        this.liste.addAll(liste);
        notifyDataSetChanged();
    }

}
