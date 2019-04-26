package com.example.anass.audiorecorder.Database.Repositories;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import com.example.anass.audiorecorder.Database.DAO.ImportantRecordDao;
import com.example.anass.audiorecorder.Database.DataBase;
import com.example.anass.audiorecorder.Helper.OnLoadCompleted;
import com.example.anass.audiorecorder.Models.ImportantRecord;

import java.util.List;

public class ImportantRecordRepository {

    private ImportantRecordDao importantRecordDao;

    public ImportantRecordRepository(Application application) {
        DataBase database = DataBase.getInstance(application);
        importantRecordDao = database.importantRecordDao();
    }

    public ImportantRecordDao getRecordDao() {
        return importantRecordDao;
    }

    public void addImportantRecord(ImportantRecord importantRecord) {
        new addRecordAsyncTask(importantRecordDao).execute(importantRecord);
    }

    private static class addRecordAsyncTask extends AsyncTask<ImportantRecord, Void, Void> {
        private ImportantRecordDao importantRecordDao;

        private addRecordAsyncTask(ImportantRecordDao importantRecordDao) {
            this.importantRecordDao = importantRecordDao;
        }

        @Override
        protected Void doInBackground(ImportantRecord... recordingItems) {
            importantRecordDao.addImportantRecord(recordingItems[0]);
            Log.i("IMPORTANT RECORD ADDED", recordingItems[0].getRecordId() + " ");

            return null;
        }
    }

    public static class getRecordsAsyncTask extends AsyncTask<Integer, Void, List<ImportantRecord>> {

        private ImportantRecordDao mRecordDao;
        private List<ImportantRecord> mRecordsTask;
        OnLoadCompleted callback;

        public getRecordsAsyncTask(ImportantRecordDao mRecordDao, OnLoadCompleted callback) {
            this.mRecordDao = mRecordDao;
            this.callback = callback;
        }

        @Override
        protected List<ImportantRecord> doInBackground(Integer... integers) {
            return mRecordDao.getImportantRecord(integers[0]);
        }

        @Override
        protected void onPostExecute(List<ImportantRecord> records) {
            super.onPostExecute(records);
            mRecordsTask = records;
            this.callback.OnLoadCompleted();
        }

        public List<ImportantRecord> getImportantRecords() {
            return mRecordsTask;
        }
    }


}
