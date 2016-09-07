package com.example.lenin.demo;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.example.lenin.util.CountingSecondsAsyncTask;


public class DemoPresenter implements DemoContract.UserActionsListener {

    private final DemoContract.View mAddNoteView;
    private CountingSecondsAsyncTask task;

    public DemoPresenter(@NonNull DemoContract.View addNoteView){

        mAddNoteView = addNoteView;
    }
    @Override
    public void callAsyncTask() {
        if (task == null || task.getStatus() == AsyncTask.Status.FINISHED) {
            task = new CountingSecondsAsyncTask() {
                @Override
                protected void onPostExecute(String result) {
                    super.onPostExecute(result);
                    mAddNoteView.success();

                }

                @Override
                protected void onCancelled() {
                    mAddNoteView.failure();
                }

                @Override
                protected void onProgressUpdate(Integer... result) {
                    super.onProgressUpdate(result);
                    mAddNoteView.setProgress(result[0]);
                }
            };
            task.execute();
        }

    }

    @Override
    public void cancelAsyncTask() {
        if (task != null && task.getStatus() != AsyncTask.Status.FINISHED){
            task.cancel(true);
        }

    }
}
