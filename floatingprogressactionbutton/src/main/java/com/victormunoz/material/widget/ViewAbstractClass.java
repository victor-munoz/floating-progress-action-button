package com.victormunoz.material.widget;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;


abstract class ViewAbstractClass extends FloatingActionButton {

    public ViewAbstractClass(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //change state
    protected abstract void updateState(State state);

    //indeterminate or determinate
    protected abstract boolean isIndeterminate();
    public abstract void setIndeterminate(boolean isIndeterminate);

    //progress
    protected abstract void setProgressIndeterminate(int progress);
    public abstract void setProgress(int progress);
    protected abstract int getProgress();

    //progress width
    public abstract float getProgressWidth();
    public abstract void setProgressWidth(int strokeWidth);

    //current progress width
    protected abstract void setCurrentProgressWidth(float strokeWidth);
    protected abstract float getCurrentProgressWidth();

    //show progress
    public abstract void setProgressShowTime(long duration);
    protected abstract long getProgressShowTime();

    //hide progress
    public abstract void setProgressHideTime(long duration);
    protected abstract long getProgressHideTime();

    //progress rotation
    protected abstract int getProgressRotation();
    protected abstract void setProgressRotation(int rotation);

    //getColor
    protected abstract int getColorReady();
    protected abstract int getColorSuccess();
    protected abstract int getColorFailure();

    //success animation
    public abstract void setSuccessShowTime(long duration);
    public abstract long getSuccessShowTime();
    public abstract void setSuccessStayTime(long duration);
    public abstract long getSuccessStayTime();

    //failure animation
    public abstract void setFailureShowTime(long duration);
    public abstract long getFailureShowTime();
    public abstract void setFailureStayTime(long duration);
    public abstract long getFailureStayTime();

    //success animation
    public abstract void setReadyShowTime(long duration);
    public abstract long getReadyShowTime();

    //user actions
    public abstract void startProgressAnimation();
    public abstract void startSuccessAnimation();
    public abstract void startFailureAnimation();

}
