package com.victormunoz.material.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;

import com.victormunoz.material.R;

import java.util.ArrayList;

public class AnimatorSuccess implements Contract.AnimationListener {
    private final ViewAbstractClass v;
    private ObjectAnimator successAnim;
    private AnimatedVectorDrawableCompat advToSuccess;
    private long currentAnimTime;
    private long currentDelayTime;
    private CountDownTimer delayTime;

    public AnimatorSuccess(Context context, @NonNull ViewAbstractClass contractView){
        this.v = contractView;
        advToSuccess = AnimatedVectorDrawableCompat.create(context, R.drawable.avd_to_success);
    }

    @Override
    public void play() {
        resume(0);
    }

    @Override
    public void resume( final long currentPlayTime) {
        if (currentPlayTime < v.getSuccessShowTime()) {
            currentAnimTime = currentPlayTime;
            currentDelayTime = 0;
        } else {
            currentAnimTime = v.getSuccessShowTime();
            currentDelayTime = currentPlayTime - v.getSuccessShowTime();
        }
        successAnim = ObjectAnimator.ofObject(v, "backgroundTintList",
                new Utils.FabTintEvaluator()
                ,ColorStateList.valueOf(v.getColorReady())
                ,ColorStateList.valueOf(v.getColorSuccess()));
        successAnim.setDuration(v.getSuccessShowTime());
        successAnim.setCurrentPlayTime(currentAnimTime);
        successAnim.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animator) {
                super.onAnimationStart(animator);
                v.setImageDrawable(advToSuccess);
                setSuccessVectorDrawableTime(v.getSuccessShowTime(), currentAnimTime);
                advToSuccess.start();
            }
            public void onAnimationEnd(Animator animator) {
                super.onAnimationEnd(animator);
                currentAnimTime = v.getSuccessShowTime();
                delayTime(v.getSuccessStayTime(), currentDelayTime);
            }
        });
        successAnim.start();
    }
    private void delayTime(long totalTime, final long playedTime) {
        final long duration = totalTime - playedTime;
        delayTime = new CountDownTimer(duration, 10) {
            public void onFinish() {
                v.updateState(State.SHOW_READY);
            }

            public void onTick(long millisUntilFinished) {
                currentDelayTime = playedTime +(duration - millisUntilFinished);
            }
        }.start();


    }

    @Override
    public void stop() {
        successAnim.cancel();
        advToSuccess.stop();
        delayTime.cancel();
    }

    @Override
    public long getCurrentPlayTime() {
        return currentAnimTime + successAnim.getCurrentPlayTime() + currentDelayTime;
    }

    @Override
    public void recreate(Context context) {
        advToSuccess = AnimatedVectorDrawableCompat.create(context, R.drawable.avd_to_success);
    }

    private void setSuccessVectorDrawableTime(long duration, long currentPlayTime){
        ArrayList<AnimatorSet> anim1 = Utils.getAnimationSet(advToSuccess);
        ObjectAnimator a= ((ObjectAnimator)anim1.get(0).getChildAnimations().get(1));
        if(duration==0){
            a.setFloatValues(0,1);
            a.setDuration(0);
            a.setStartDelay(0);
        }
        else {
            float val = a.getInterpolator().getInterpolation((float) currentPlayTime / duration);
            a.setFloatValues(val, 1);
            a.setDuration(duration - currentPlayTime);
            a.setStartDelay(0);
        }

    }
}
