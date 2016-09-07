package com.victormunoz.material.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatDrawableManager;
import android.view.animation.LinearInterpolator;

import com.victormunoz.material.R;

/**

 */
public class AnimatorReady implements Contract.AnimationReadyListener {

    private Drawable advToReady;
    private AnimatorSet readyAnim;
    private ViewAbstractClass button;
    private long previousPlayTime;

    public AnimatorReady(Context context, @NonNull ViewAbstractClass contractView) {
        this.button = contractView;
        advToReady = AppCompatDrawableManager.get().getDrawable(context, R.drawable.ic_ready);
    }

    @Override
    public void play(boolean isSuccessCalled) {
        resume(0,isSuccessCalled);
    }

    @Override
    @SuppressWarnings("ResourceAsColor")
    public void resume( final long currentPlayTime,boolean isSuccessCalled) {
        previousPlayTime=currentPlayTime;
        int colorStart = isSuccessCalled ? button.getColorSuccess():button.getColorFailure();
        //animator to change the background tint and the elevation of the button
        PropertyValuesHolder tint = PropertyValuesHolder.ofObject("backgroundTintList"
                ,new Utils.FabTintEvaluator()
                ,ColorStateList.valueOf(colorStart)
                , ColorStateList.valueOf(button.getColorReady()));
        ObjectAnimator tintAndElevation=ObjectAnimator.ofPropertyValuesHolder(button, tint);
        tintAndElevation.setDuration(button.getReadyShowTime()-currentPlayTime);
        tintAndElevation.setCurrentPlayTime(currentPlayTime);
        tintAndElevation.setStartDelay(0);
        //animation for the alpha of the drawable
        ObjectAnimator alpha = ObjectAnimator.ofInt(advToReady, "alpha", 0, 255);
        alpha.setDuration(button.getReadyShowTime()-currentPlayTime);
        alpha.setCurrentPlayTime(currentPlayTime);
        alpha.setStartDelay(0);
        readyAnim=new AnimatorSet();
        readyAnim.playTogether(alpha,tintAndElevation);
        readyAnim.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animator) {
                button.setImageDrawable(advToReady);
            }

            public void onAnimationEnd(Animator animator) {
                button.updateState(State.READY);
            }
        });
        readyAnim.start();

    }

    @Override
    public void stop() {
        readyAnim.cancel();
    }

    @Override
    public long getCurrentPlayTime() {
        ValueAnimator anim = (ValueAnimator) readyAnim.getChildAnimations().get(0);
        return anim.getCurrentPlayTime()+previousPlayTime;
    }

    @Override
    public void recreate(Context context) {
        advToReady = ContextCompat.getDrawable(context, R.drawable.ic_ready);
    }
}

