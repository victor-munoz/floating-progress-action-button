package com.example.lenin.demo;


public interface DemoContract {

    interface View {

        void setProgress(int i);
        void success();
        void failure();


    }

    interface UserActionsListener {

        void callAsyncTask();
        void cancelAsyncTask();


    }
}
