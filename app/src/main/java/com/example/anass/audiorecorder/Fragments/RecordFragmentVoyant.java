package com.example.anass.audiorecorder.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.anass.audiorecorder.Activities.MainActivity;
import com.example.anass.audiorecorder.Adapters.FileViewerAdapterVoyant;
import com.example.anass.audiorecorder.Database.DataBase;
import com.example.anass.audiorecorder.Database.Repositories.RecordRepository;
import com.example.anass.audiorecorder.Helper.OnLoadCompleted;
import com.example.anass.audiorecorder.Managers.RecyclerViewManager;
import com.example.anass.audiorecorder.Models.RecordingItem;
import com.example.anass.audiorecorder.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RecordFragmentVoyant extends Fragment implements OnLoadCompleted {

    @Bind(R.id.voyants_records_recycler)
    public RecyclerView mainRecycler;


    MainActivity activity;

    FileViewerAdapterVoyant adapter;

    RecordRepository.getRecordsAsyncTask recordsAsyncTask;

    DataBase db;

    public static final String LOG_TAG = "RecordFragmenetVoyant";

    public static RecordFragmentVoyant newInstance() {
        RecordFragmentVoyant fragment = new RecordFragmentVoyant();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.records_list_voyant_fragmenet, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(LOG_TAG, "Lunched");
        activity = (MainActivity) getActivity();
        init();
    }

    private void init() {
        adapter = new FileViewerAdapterVoyant(activity);
        RecyclerViewManager.configureRecycleView(activity, mainRecycler);
        mainRecycler.setAdapter(adapter);
        getData();
    }

    private void getData() {
        db = DataBase.getInstance(activity.getApplicationContext());
        recordsAsyncTask = new RecordRepository.getRecordsAsyncTask(db.recordDao(), this);
        recordsAsyncTask.execute();
    }

    @OnClick(R.id.button_add_record)
    public void adjclick() {
        activity.navigateTo(RecordFragment.newInstance());
    }

    @Override
    public void OnLoadCompleted() {
        refreshRecyclerView();
    }

    private void refreshRecyclerView() {
        List<RecordingItem> recordingItems = recordsAsyncTask.getRecords();
        if (recordingItems != null && recordingItems.size() > 0) {
            adapter.addAllItems(recordingItems);
        }
    }
}
