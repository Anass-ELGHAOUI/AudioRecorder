package com.example.anass.audiorecorder.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.anass.audiorecorder.Activities.MainActivity;
import com.example.anass.audiorecorder.Adapters.FileViewerAdapter;
import com.example.anass.audiorecorder.Database.DataBase;
import com.example.anass.audiorecorder.Database.Repositories.RecordRepository;
import com.example.anass.audiorecorder.Helper.OnLoadCompleted;
import com.example.anass.audiorecorder.Managers.RecyclerViewManager;
import com.example.anass.audiorecorder.Models.RecordingItem;
import com.example.anass.audiorecorder.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RecordsListFragment extends Fragment implements OnLoadCompleted {

    @Bind(R.id.records_recycler)
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
    }
}
