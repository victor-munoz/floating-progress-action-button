package com.victormunoz.material.widget;

import android.animation.AnimatorSet;
import android.animation.TypeEvaluator;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import java.lang.reflect.Field;
import java.util.ArrayList;


class Utils {
    public static ArrayList<AnimatorSet> getAnimationSet(AnimatedVectorDrawableCompat animVectorDrawable) {
        ArrayList<AnimatorSet> r = new ArrayList<>();
        try {
            Field f = animVectorDrawable.getClass().getDeclaredField("mCachedConstantStateDelegate");
            f.setAccessible(true);
            Object mCachedConstantStateDelegate = f.get(animVectorDrawable);
            Field f2 = mCachedConstantStateDelegate.getClass().getDeclaredField("mDelegateState");
            f2.setAccessible(true);
            Object mDelegateState = f2.get(mCachedConstantStateDelegate);
            Field f3 = mDelegateState.getClass().getDeclaredField("mAnimators");
            f3.setAccessible(true);
            ArrayList<AnimatorSet> mAnimators = (ArrayList<AnimatorSet>) f3.get(mDelegateState);
            r.addAll(mAnimators);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return r;

    }
    public static class FabTintEvaluator implements TypeEvaluator<ColorStateList> {
        @Override
        public ColorStateList evaluate(float fraction, ColorStateList startValue, ColorStateList endValue) {

            int startInt = startValue.getDefaultColor();
            int startA = (startInt >> 24) & 0xff;
            int startR = (startInt >> 16) & 0xff;
            int startG = (startInt >> 8) & 0xff;
            int startB = startInt & 0xff;

            int endInt = endValue.getDefaultColor();
            int endA = (endInt >> 24) & 0xff;
            int endR = (endInt >> 16) & 0xff;
            int endG = (endInt >> 8) & 0xff;
            int endB = endInt & 0xff;

            return ColorStateList.valueOf(((startA + (int) (fraction * (endA - startA))) << 24) |
                    ((startR + (int) (fraction * (endR - startR))) << 16) |
                    ((startG + (int) (fraction * (endG - startG))) << 8) |
                    ((startB + (int) (fraction * (endB - startB)))));
        }
    }
    public static int lighter(int color, float factor) {
        int red = (int) ((Color.red(color) * (1 - factor) / 255 + factor) * 255);
        int green = (int) ((Color.green(color) * (1 - factor) / 255 + factor) * 255);
        int blue = (int) ((Color.blue(color) * (1 - factor) / 255 + factor) * 255);
        return Color.argb(Color.alpha(color), red, green, blue);
    }
}
