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

public class AnimatorFailure implements Contract.AnimationListener {
    private final ViewAbstractClass v;
    private AnimatedVectorDrawableCompat advToFailure;
    private ObjectAnimator failureAnim;
    private long currentDelayTime;
    private long currentAnimTime;
    private CountDownTimer delayTime;

    public AnimatorFailure(Context context, @NonNull ViewAbstractClass contractView) {
        this.v = contractView;
        advToFailure = AnimatedVectorDrawableCompat.create(context, R.drawable.avd_to_failure);
    }
    @Override
    public void play() {
        resume(0);
    }
    /**
     * animate the failure animation witch consist in the animation of the tint background of the button
     * with the animation of the vector drawable and a delay time in witch the animation will be show
     * it after the next animation.
     * <p/>
     * will be two times involves in this animation: failureShowDuration + failureDuration
     *
     * @param currentPlayTime
     */
    @Override
    public void resume(final long currentPlayTime) {
        if (currentPlayTime < v.getFailureShowTime()) {
            currentAnimTime = currentPlayTime;
            currentDelayTime = 0;
        } else {
            currentAnimTime = v.getFailureShowTime();
            currentDelayTime = currentPlayTime - v.getFailureShowTime();
        }

        failureAnim = ObjectAnimator.ofObject(v, "backgroundTintList"
                , new Utils.FabTintEvaluator()
                , ColorStateList.valueOf(v.getColorReady())
                , ColorStateList.valueOf(v.getColorFailure()));
        failureAnim.setDuration(v.getFailureShowTime());
        failureAnim.setCurrentPlayTime(currentAnimTime);
        failureAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                v.setImageDrawable(advToFailure);
                setFailureVectorDrawableTime(v.getFailureShowTime(), currentAnimTime);
                advToFailure.start();
                v.updateState(State.SHOW_FAILURE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                currentAnimTime = v.getFailureShowTime();
                delayTime(v.getFailureStayTime(), currentDelayTime);
            }
        });
        failureAnim.start();
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
        failureAnim.cancel();
        advToFailure.stop();
        delayTime.cancel();
    }
    @Override
    public long getCurrentPlayTime() {
        return currentAnimTime + failureAnim.getCurrentPlayTime() + currentDelayTime;
    }
    @Override
    public void recreate(Context context) {
        advToFailure = AnimatedVectorDrawableCompat.create(context, R.drawable.avd_to_failure);
    }

    private void setFailureVectorDrawableTime(long duration, long currentPlayTime) {
        long timeToDrawOneLine = duration / 2;
        //get the paths that will be animated
        ArrayList<AnimatorSet> animSet = Utils.getAnimationSet(advToFailure);
        //first line
        ObjectAnimator line1 = ((ObjectAnimator) animSet.get(0).getChildAnimations().get(1));
        //second line
        ObjectAnimator line2 = ((ObjectAnimator) animSet.get(1).getChildAnimations().get(1));

        //no animation
        if (timeToDrawOneLine == 0) {
            line1.setDuration(0);
            line1.setFloatValues(0, 1);
            line2.setFloatValues(0, 1);
            line2.setDuration(0);
            line2.setStartDelay(0);
        }
        //if we still are drawing the first line (backslash)
        else if (currentPlayTime < timeToDrawOneLine) {
            line1.setDuration(timeToDrawOneLine - currentPlayTime);
            line2.setStartDelay(timeToDrawOneLine - currentPlayTime);
            line2.setDuration(timeToDrawOneLine);

            float drawnPercent = line1.getInterpolator().getInterpolation((float) currentPlayTime / timeToDrawOneLine);
            line1.setFloatValues(drawnPercent, 1);
            line2.setFloatValues(0, 1);
        }
        //we are drawing the second line (slash)
        else {
            line1.setDuration(0);
            line2.setStartDelay(0);
            line2.setDuration(duration - currentPlayTime);
            line1.setFloatValues(0, 1);
            float slashPercent = line2.getInterpolator().getInterpolation((float) (currentPlayTime - timeToDrawOneLine) / timeToDrawOneLine);
            line2.setFloatValues(slashPercent, 1);
        }
    }
}
