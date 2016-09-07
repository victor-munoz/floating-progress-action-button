package com.victormunoz.material.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;


public class AnimatorProgress implements Contract.ProgressListener {
    private static final long FAST_ROTATION = 3L;
    private static final long SLOW_ROTATION = 30L;
    private static final float ROTATION_FACTOR = 1f;
    private long rotationUpdateTime;
    private boolean isRotationRunning;
    private final ViewAbstractClass button;
    private AnimatorSet showProgressAnim;
    private ValueAnimator hideProgressAnim;
    private ValueAnimator indeterminateAnim;
    private Thread augmentedRotationThread;

    public AnimatorProgress(ViewAbstractClass contractView) {
        this.button = contractView;
    }
    //show Progress
    @Override
    public void playShowProgress() {
        resumeShowProgress(0);
    }
    @Override
    public void resumeShowProgress(long currentPlayTime) {
        if(button.isIndeterminate()){
            playIndeterminate();
        }
        ValueAnimator widthAnim = ValueAnimator.ofFloat(0f, button.getProgressWidth());
        widthAnim.setDuration(button.getProgressShowTime()-currentPlayTime);
        widthAnim.setCurrentPlayTime(currentPlayTime);
        widthAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                button.setCurrentProgressWidth((Float) valueAnimator.getAnimatedValue());
            }
        });


        showProgressAnim = new AnimatorSet();
        showProgressAnim.playTogether(widthAnim);
        showProgressAnim.setInterpolator(new LinearInterpolator());
        showProgressAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                startRotation();
                button.updateState(State.SHOW_PROGRESS);
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                button.updateState(State.PROGRESS_RUNNING);
            }
        });
        showProgressAnim.start();
    }
    @Override
    public long getShowProgressCurrentPlayTime() {
        ValueAnimator anim = (ValueAnimator) showProgressAnim.getChildAnimations().get(0);
        return anim.getCurrentPlayTime();
    }
    @Override
    public void cancelShowProgress() {
        showProgressAnim.cancel();
        stopRotation();
    }
    //hide Progress
    @Override
    public void playHideProgress() {
        resumeHideProgress(0);
    }
    @Override
    public void resumeHideProgress(long currentPlayTime) {
        if(currentPlayTime>0){
            startRotation();
            if(button.isIndeterminate()){
                resumeIndeterminate(currentPlayTime);
            }
        }
        hideProgressAnim = ValueAnimator.ofFloat(button.getCurrentProgressWidth(), 0f);
        float incompleteShowProgress=new LinearInterpolator().getInterpolation(button.getCurrentProgressWidth()/button.getProgressWidth());
        hideProgressAnim.setDuration((long) ((float)button.getProgressHideTime()*incompleteShowProgress));
        hideProgressAnim.setCurrentPlayTime(currentPlayTime);
        hideProgressAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                button.setCurrentProgressWidth((Float) valueAnimator.getAnimatedValue());
            }
        });
        hideProgressAnim.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                stopRotation();
                button.updateState(State.PROGRESS_ENDED);
            }

            public void onAnimationStart(Animator animator) {
                button.updateState(State.HIDE_PROGRESS);
            }

        });
        hideProgressAnim.start();
    }
    @Override
    public long getHideProgressCurrentPlayTime() {
        return hideProgressAnim.getCurrentPlayTime();
    }
    @Override
    public void cancelHideProgress() {
        hideProgressAnim.cancel();
        stopRotation();
    }
    //running progress
    @Override
    public void resumeRunningProgress() {
        startRotation();
        if(button.isIndeterminate()){
            playIndeterminate();
        }
    }
    @Override
    public void cancelRunningProgress() {
        stopRotation();
    }
    @Override
    public long getIndeterminateCurrentPlayTime() {
        return indeterminateAnim.getCurrentPlayTime();
    }

    //indeterminate progress
    private void playIndeterminate() {
        resumeIndeterminate(0);
    }
    private void resumeIndeterminate(long currentPlayTime) {
        int x = 30;
        indeterminateAnim = ValueAnimator.ofInt(x, 360 - x);
        indeterminateAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        indeterminateAnim.setDuration(900);
        indeterminateAnim.setCurrentPlayTime(currentPlayTime);
        indeterminateAnim.setRepeatMode(ValueAnimator.REVERSE);
        indeterminateAnim.setRepeatCount(ValueAnimator.INFINITE);
        indeterminateAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (int) valueAnimator.getAnimatedValue();
                if (button.getProgress() > value) {
                    int delta = button.getProgress() - value;
                    button.setProgressRotation( (button.getProgressRotation() + delta) % 360);
                }
                button.setProgressIndeterminate(value);
            }
        });
        indeterminateAnim.start();
    }
    private void cancelIndeterminate() {
        if (indeterminateAnim != null && indeterminateAnim.isRunning()) {
            indeterminateAnim.cancel();
        }
    }
    //rotation
    private void startRotation() {
        rotationUpdateTime =FAST_ROTATION;
        isRotationRunning = true;
        augmentedRotationThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRotationRunning) {
                    try {
                        Thread.sleep(rotationUpdateTime);
                        button.setProgressRotation( (button.getProgressRotation() + 1) % 360);
                        button.postInvalidate();
                    } catch (InterruptedException e) {
                        isRotationRunning = false;
                    }
                }
            }
        });
        augmentedRotationThread.start();
    }
    private void stopRotation() {
        if (augmentedRotationThread != null && augmentedRotationThread.isAlive()) {
            isRotationRunning = false;
        }
        if(button.isIndeterminate()){
            cancelIndeterminate();
        }

    }
    public void updateRotationSpeed() {
        float interpolator = new AccelerateInterpolator(ROTATION_FACTOR).getInterpolation(button.getProgress() / 360f);
        rotationUpdateTime = (long) (interpolator * (SLOW_ROTATION - FAST_ROTATION) + FAST_ROTATION);
    }


}
