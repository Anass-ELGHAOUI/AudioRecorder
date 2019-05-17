package com.example.anass.audiorecorder.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.anass.audiorecorder.Activities.MainActivity;
import com.example.anass.audiorecorder.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddRecordNameFragment extends Fragment {

    private MainActivity activity;

    @Bind(R.id.et_record_name)
    public EditText recordName;


    public static AddRecordNameFragment newInstance() {
        AddRecordNameFragment fragment = new AddRecordNameFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_record_name_fragment, container, false);
        ButterKnife.bind(this, view);
        activity = (MainActivity) getActivity();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @OnClick(R.id.btn_back_record_name)
    public void btnBackClicked(){
        activity.onBackPressed();
    }

    @OnClick(R.id.btn_valid_name_record)
    public void btnValidNameClicked(){
        String name = recordName.getText().toString();
        if(name.isEmpty()){
            name = "Audio";
        }
        activity.navigateTo(RecordFragment.newInstance(name));
    }


}

