package com.example.anass.audiorecorder.Database.Repositories;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import com.example.anass.audiorecorder.Database.DAO.ChoiceDao;
import com.example.anass.audiorecorder.Database.DataBase;
import com.example.anass.audiorecorder.Helper.OnLoadCompleted;
import com.example.anass.audiorecorder.Models.Choice;

public class ChoiceRepository {

    private ChoiceDao mChoiceDao;

    public ChoiceRepository(Application application) {
        DataBase database = DataBase.getInstance(application);
        mChoiceDao = database.choiceDao();
    }

    public void addChoice(Choice choice) {
        new ChoiceRepository.addChoiceAsyncTask(mChoiceDao).execute(choice);
    }

    private static class addChoiceAsyncTask extends AsyncTask<Choice, Void, Void> {
        private ChoiceDao mChoiceDao;

        private addChoiceAsyncTask(ChoiceDao mChoiceDao) {
            this.mChoiceDao = mChoiceDao;
        }

        @Override
        protected Void doInBackground(Choice... choices) {
            mChoiceDao.addChoice(choices[0]);
            Log.i("ADDED", "choice added");
            return null;
        }
    }

    public static class getChoiceAsyncTask extends AsyncTask<Void, Void, Choice> {

        private ChoiceDao mChoiceDao;
        private Choice mChoice;
        OnLoadCompleted callback;

        public getChoiceAsyncTask(ChoiceDao mChoiceDao, OnLoadCompleted callback) {
            this.mChoiceDao = mChoiceDao;
            this.callback = callback;
        }

        @Override
        protected Choice doInBackground(Void... voids) {
            return mChoiceDao.getChoice();
        }

        @Override
        protected void onPostExecute(Choice choice) {
            super.onPostExecute(choice);
            mChoice = choice;
            this.callback.OnLoadCompleted();
        }

        public Choice getChoice() {
            return mChoice;
        }
    }
}
