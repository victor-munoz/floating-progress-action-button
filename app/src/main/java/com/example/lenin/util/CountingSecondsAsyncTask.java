package com.example.lenin.util;

import android.os.AsyncTask;

import java.util.Random;


public class CountingSecondsAsyncTask extends AsyncTask<Void, Integer, String> {
    @Override
    protected String doInBackground(Void... params) {
        int x=0;
        publishProgress(x);
        while(x<=99&&!isCancelled()){
            Random rand = new Random();
            try {

                //Thread.sleep( rand.nextInt(10)+1);
                //x += rand.nextInt(10)+1;
                Thread.sleep(10);
                x+=1;

                publishProgress(x);

            } catch (InterruptedException e) {
                // e.printStackTrace();
            }
        }
        return  String.valueOf(x);

    }
}
