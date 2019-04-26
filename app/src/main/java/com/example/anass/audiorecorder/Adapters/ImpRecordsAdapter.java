package com.example.anass.audiorecorder.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.anass.audiorecorder.Activities.MainActivity;
import com.example.anass.audiorecorder.Models.ImportantRecord;
import com.example.anass.audiorecorder.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ImpRecordsAdapter extends RecyclerView.Adapter<ImpRecordsAdapter.RecordingsViewHolder> {
    List<ImportantRecord> liste;
    MainActivity activity;
    private int recordId;

    public ImpRecordsAdapter(MainActivity activity, List<ImportantRecord> liste) {
        super();
        this.liste = new ArrayList<>();
        this.liste.addAll(liste);
        this.activity = activity;
    }

    public ImpRecordsAdapter(MainActivity activity) {
        super();
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
        recordId = item.getRecordId();
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


        public RecordingsViewHolder(View v, final Context context) {
            super(v);
            vName = v.findViewById(R.id.file_name_text);
            vLength = v.findViewById(R.id.file_length_text);
            vDateAdded = v.findViewById(R.id.file_date_added_text);
            this.activity = context;
            v.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

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


