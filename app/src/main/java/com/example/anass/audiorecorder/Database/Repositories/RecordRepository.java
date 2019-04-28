package com.example.anass.audiorecorder.Database.Repositories;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import com.example.anass.audiorecorder.Database.DAO.RecordDao;
import com.example.anass.audiorecorder.Database.DataBase;
import com.example.anass.audiorecorder.Helper.OnLoadCompleted;
import com.example.anass.audiorecorder.Models.RecordingItem;

import java.util.List;

public class RecordRepository {

    private RecordDao mRecordDao;
    List<RecordingItem> mRecordingItems;

    public RecordRepository(Application application) {
        DataBase database = DataBase.getInstance(application);
        mRecordDao = database.recordDao();
    }

    public RecordDao getRecordDao() {
        return mRecordDao;
    }

    public void addRecord(RecordingItem mRecordingItem) {
        new addRecordAsyncTask(mRecordDao).execute(mRecordingItem);
    }

    public void deleteAllRecords() {
        new deleteRecordsAsyncTask(mRecordDao).execute();
    }

    private static class addRecordAsyncTask extends AsyncTask<RecordingItem, Void, Void> {
        private RecordDao mRecordDao;

        private addRecordAsyncTask(RecordDao mRecordDao) {
            this.mRecordDao = mRecordDao;
        }

        @Override
        protected Void doInBackground(RecordingItem... recordingItems) {
            mRecordDao.addRecord(recordingItems[0]);
            Log.i("ADDED", recordingItems[0].getFilePath());
            return null;
        }
    }

    /*public static class getRecordByName extends AsyncTask<String, Void, RecordingItem> {

        private RecordDao mRecordDao;
        private RecordingItem mRecordingItemTask;
        OnLoadCompleted callback;

        public getRecordByName(RecordDao mRecordDao, OnLoadCompleted callback) {
            this.mRecordDao = mRecordDao;
            this.callback = callback;
        }

        @Override
        protected RecordingItem doInBackground(String... strings) {
            return mRecordDao.getRecordByName(strings[0]);
        }

        @Override
        protected void onPostExecute(RecordingItem recordingItem) {
            super.onPostExecute(recordingItem);
            mRecordingItemTask = recordingItem;
            this.callback.OnLoadCompleted();
        }

        public RecordingItem getRecordByName() {
            return mRecordingItemTask;
        }
    }*/

    public static class getRecordsAsyncTask extends AsyncTask<Void, Void, List<RecordingItem>> {

        private RecordDao mRecordDao;
        private List<RecordingItem> mRecordsTask;
        OnLoadCompleted callback;

        public getRecordsAsyncTask(RecordDao mRecordDao, OnLoadCompleted callback) {
            this.mRecordDao = mRecordDao;
            this.callback = callback;
        }

        @Override
        protected List<RecordingItem> doInBackground(Void... voids) {
            return mRecordDao.getRecords();
        }

        @Override
        protected void onPostExecute(List<RecordingItem> records) {
            super.onPostExecute(records);
            mRecordsTask = records;
            this.callback.OnLoadCompleted();
        }

        public List<RecordingItem> getRecords() {
            return mRecordsTask;
        }
    }

    private static class deleteRecordsAsyncTask extends AsyncTask<Void, Void, Void> {
        private RecordDao mRecordDao;

        private deleteRecordsAsyncTask(RecordDao mRecordDao) {
            this.mRecordDao = mRecordDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mRecordDao.deleteAllRecords();
            return null;
        }
    }

    public static class getRecordAsyncTask extends AsyncTask<String, Void, RecordingItem> {

        private RecordDao recordDao;
        private RecordingItem recordTask;
        OnLoadCompleted callback;

        public getRecordAsyncTask(RecordDao recordDao, OnLoadCompleted callback) {
            this.recordDao = recordDao;
            this.callback = callback;
        }

        @Override
        protected void onPostExecute(RecordingItem recordingItem) {
            super.onPostExecute(recordingItem);
            recordTask = recordingItem;
            this.callback.OnLoadCompleted();
        }

        public RecordingItem getRecordByName() {
            return recordTask;
        }

        @Override
        protected RecordingItem doInBackground(String... strings) {
            return recordDao.getRecordByName(strings[0]);
        }
    }

    public static class getLastIdAsyncTask extends AsyncTask<Void, Void, Integer> {

        private RecordDao recordDao;
        OnLoadCompleted callback;
        private int lastId;

        public getLastIdAsyncTask(RecordDao recordDao, OnLoadCompleted callback) {
            this.recordDao = recordDao;
            this.callback = callback;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            this.callback.OnLoadCompleted();
            lastId = integer;
            Log.i("Last Id",""+lastId);
        }

        public Integer getLastId() {
            return lastId;
        }

        @Override
        protected Integer doInBackground(Void... Voids) {
            return recordDao.getLastId();
        }
    }

}
