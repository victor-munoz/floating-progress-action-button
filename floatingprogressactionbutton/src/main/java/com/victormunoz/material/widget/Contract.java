package com.victormunoz.material.widget;

import android.content.Context;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;

/**
 * Created by Lenin on 7/31/2016.
 */
public interface Contract {

    interface AnimationListener {
        void play();
        void resume(long currentPlayTime);
        void stop();
        long getCurrentPlayTime();
        void recreate(Context context);
    }
    interface AnimationReadyListener {
        void play(boolean isSuccessCalled);
        void resume(long currentPlayTime, boolean isSuccessCalled);
        void stop();
        long getCurrentPlayTime();
        void recreate(Context context);
    }

    interface ProgressListener{
        void playShowProgress();
        void playHideProgress();

        void resumeShowProgress(long currentPlayTime);
        void resumeHideProgress(long currentPlayTime);
        void resumeRunningProgress();

        long getShowProgressCurrentPlayTime();
        long getIndeterminateCurrentPlayTime();
        long getHideProgressCurrentPlayTime();

        void cancelShowProgress();
        void cancelHideProgress();
        void cancelRunningProgress();

        void updateRotationSpeed();
    }
    interface StateListener {
       void updateState(State state);
    }
}
