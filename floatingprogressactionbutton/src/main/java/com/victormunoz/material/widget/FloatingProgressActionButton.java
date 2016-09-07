package com.victormunoz.material.widget;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatDrawableManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewOutlineProvider;

import com.victormunoz.material.R;

import static com.victormunoz.material.widget.Utils.lighter;

/**
 * FabProgress extends {@link FloatingActionButton} adding a circular progress around it and
 * animations for the success and failure states.
 * <p/>
 * <p/>
 * <P>The Animation of the progress is set to determinate by default but can be change it
 * via {@link #setIndeterminate(boolean)}.
 * </P>
 */

public class FloatingProgressActionButton extends ViewAbstractClass {
    //shadow elevation
    private ViewOutlineProvider viewOutlineProvider;
    //drawable
    private Drawable srcDrawable;
    //rect
    private Rect rectButton =new Rect();
    private RectF rectClip = new RectF();
    private RectF rectArc = new RectF();
    //paint
    private Paint paintProgressPrimary;
    private Paint paintProgressBackground;
    //color
    private int colorReady;
    private int colorSuccess;
    private int colorFailure;
    private int colorProgressSecondary;
    private int colorProgressPrimary;
    //progress
    private boolean isIndeterminate;
    private float maxProgressStrokeWidth;
    private int rotation = 0;
    private int progress = 0;
    //animation's duration
    private long progressShowTime;
    private long progressHideTime;
    private long successShowTime;
    private long successStayTime;
    private long failureShowTime;
    private long failureStayTime;
    private long readyShowTime;
    //Animator listeners
    private Contract.AnimationListener mSuccessListener;
    private Contract.AnimationListener mFailureListener;
    private Contract.ProgressListener mProgressListener;
    private Contract.AnimationReadyListener mReadyListener;
    //current state
    private State currentState = State.READY;
    private Boolean isSuccessCalled;
    private Contract.StateListener mListener;



    //L Y F E   C Y C L E

    public FloatingProgressActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mSuccessListener = new AnimatorSuccess(getContext(), this);
        mFailureListener = new AnimatorFailure(getContext(), this);
        mReadyListener = new AnimatorReady(getContext(),this);
        mProgressListener = new AnimatorProgress(this);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.FloatingProgressActionButton,
                0, 0);

        maxProgressStrokeWidth = (int) a.getDimension(
                R.styleable.FloatingProgressActionButton_progressWidth,
                getResources().getDimension(R.dimen.default_progress_width));
        colorSuccess = a.getColor(
                R.styleable.FloatingProgressActionButton_colorTintSuccess,
                ContextCompat.getColor(context, R.color.colorPrimary));
        colorFailure = a.getColor(
                R.styleable.FloatingProgressActionButton_colorTintFailure,
                Color.RED);
        colorProgressPrimary = a.getColor(
                R.styleable.FloatingProgressActionButton_colorProgressPrimary,
                ContextCompat.getColor(context, R.color.colorPrimary));
        colorProgressSecondary = a.getColor(
                R.styleable.FloatingProgressActionButton_colorProgressSecondary,
                lighter(colorProgressPrimary,0.5f));
        isIndeterminate = a.getBoolean(
                R.styleable.FloatingProgressActionButton_isIndeterminate,
                false);
        progressShowTime =(long)a.getInt(R.styleable.FloatingProgressActionButton_progressShowTime,400);
        progressHideTime =a.getInt(R.styleable.FloatingProgressActionButton_progressHideTime,400);
        successShowTime =a.getInt(R.styleable.FloatingProgressActionButton_successShowTime,400);
        successStayTime =a.getInt(R.styleable.FloatingProgressActionButton_successStayTime,1200);
        failureShowTime =a.getInt(R.styleable.FloatingProgressActionButton_failureShowTime,400);
        failureStayTime =a.getInt(R.styleable.FloatingProgressActionButton_failureStayTime,1200);
        readyShowTime =a.getInt(R.styleable.FloatingProgressActionButton_readyShowTime,400);

        a.recycle();
        srcDrawable = getDrawable();
        if (srcDrawable == null) {
            srcDrawable = AppCompatDrawableManager.get().getDrawable(getContext(), R.drawable.ic_ready);
            setImageDrawable(srcDrawable);
        }
        colorReady = getBackgroundTintList() != null ?
                getBackgroundTintList().getDefaultColor() :
                ContextCompat.getColor(context, R.color.colorAccent);

        paintProgressPrimary = new Paint(1);
        paintProgressPrimary.setColor(colorProgressPrimary);
        paintProgressPrimary.setStyle(Paint.Style.STROKE);
        paintProgressPrimary.setStrokeCap(Paint.Cap.BUTT);
        paintProgressPrimary.setStrokeWidth(0);

        paintProgressBackground = new Paint(1);
        paintProgressBackground.setColor(colorProgressSecondary);
        paintProgressBackground.setStyle(Paint.Style.STROKE);
        paintProgressBackground.setStrokeCap(Paint.Cap.BUTT);
        paintProgressBackground.setStrokeWidth(0);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            viewOutlineProvider = new ViewOutlineProvider() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void getOutline(View view, Outline outline) {
                    int x= (int) paintProgressPrimary.getStrokeWidth();
                    outline.setOval(-x,-x,rectButton.width()+x,rectButton.height()+x);
                }
            };
        }




    }
    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("superState", super.onSaveInstanceState());

        bundle.putInt("rotation", this.rotation);
        bundle.putInt("progress", this.progress);
        bundle.putBoolean("isIndeterminate", isIndeterminate);
        if(isSuccessCalled!=null) bundle.putBoolean("isSuccessCalled", isSuccessCalled);
        bundle.putFloat("currentProgressStrokeWidth", paintProgressPrimary.getStrokeWidth());
        bundle.putFloat("maxProgressStrokeWidth", maxProgressStrokeWidth);
        bundle.putLong("progressShowTime", progressShowTime);
        bundle.putLong("progressHideTime", progressHideTime);
        bundle.putLong("successShowTime", successShowTime);
        bundle.putLong("failureShowTime", failureShowTime);
        bundle.putLong("readyShowTime", readyShowTime);
        bundle.putLong("successStayTime", successStayTime);
        bundle.putLong("failureStayTime", failureStayTime);
        bundle.putString("currentState", currentState.name());
        switch (currentState) {
            case SHOW_PROGRESS:
                bundle.putLong("currentPlayTime", mProgressListener.getShowProgressCurrentPlayTime());
                mProgressListener.cancelShowProgress();
                break;
            case PROGRESS_RUNNING:
                if (isIndeterminate) {
                    bundle.putLong("currentPlayTime", mProgressListener.getIndeterminateCurrentPlayTime());
                }
                mProgressListener.cancelRunningProgress();
                break;
            case HIDE_PROGRESS:
                bundle.putLong("currentPlayTime", mProgressListener.getHideProgressCurrentPlayTime());
                mProgressListener.cancelHideProgress();
                break;
            case SHOW_SUCCESS:
                bundle.putLong("currentPlayTime", mSuccessListener.getCurrentPlayTime());
                mSuccessListener.stop();
                break;
            case SHOW_FAILURE:
                bundle.putLong("currentPlayTime", mFailureListener.getCurrentPlayTime());
                mFailureListener.stop();
                break;
            case SHOW_READY:
                bundle.putLong("currentPlayTime", mReadyListener.getCurrentPlayTime());
                mReadyListener.stop();
                break;
        }
        return bundle;
    }
    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) // implicit null check
        {
            Bundle bundle = (Bundle) state;
            rotation = bundle.getInt("rotation");
            progress = bundle.getInt("progress");
            progressShowTime = bundle.getLong("progressShowTime");
            progressHideTime = bundle.getLong("progressHideTime");
            successShowTime = bundle.getLong("successShowTime");
            failureShowTime = bundle.getLong("failureShowTime");
            readyShowTime = bundle.getLong("readyShowTime");
            successStayTime = bundle.getLong("successStayTime");
            failureStayTime = bundle.getLong("failureStayTime");
            maxProgressStrokeWidth = bundle.getFloat("maxProgressStrokeWidth");
            isIndeterminate = bundle.getBoolean("isIndeterminate");
            currentState = State.valueOf(bundle.getString("currentState"));
            if(bundle.containsKey("isSuccessCalled")) isSuccessCalled = bundle.getBoolean("isSuccessCalled");
            switch (currentState) {
                case READY:
                    isSuccessCalled=null;
                    break;
                case SHOW_PROGRESS:
                    mProgressListener.resumeShowProgress(bundle.getLong("currentPlayTime"));
                    break;
                case PROGRESS_RUNNING:
                    setCurrentProgressWidth(maxProgressStrokeWidth);
                    mProgressListener.resumeRunningProgress();
                    break;
                case HIDE_PROGRESS:
                    setCurrentProgressWidth(bundle.getFloat("currentProgressStrokeWidth"));
                    mProgressListener.resumeHideProgress(bundle.getLong("currentPlayTime"));
                    break;
                case SHOW_SUCCESS:
                    mSuccessListener.resume( bundle.getLong("currentPlayTime"));
                    break;
                case SHOW_FAILURE:
                    mFailureListener.resume(bundle.getLong("currentPlayTime"));
                    break;
                case SHOW_READY:
                    mReadyListener.resume(bundle.getLong("currentPlayTime"),isSuccessCalled);
                    break;
            }
            state = bundle.getParcelable("superState");
        }
        super.onRestoreInstanceState(state);
    }
    @Override
    protected void updateState(State state){
        currentState=state;
        mListener.updateState(state);
        switch (currentState) {
            case READY:
                isSuccessCalled = null;
                break;
            case SHOW_PROGRESS:
                break;
            case PROGRESS_RUNNING:
                break;
            case HIDE_PROGRESS:
                break;
            case PROGRESS_ENDED:
                rotation=270;
                if(isSuccessCalled){
                    updateState(State.SHOW_SUCCESS);
                    mSuccessListener.play();
                }
                else{
                    updateState(State.SHOW_FAILURE);
                    mFailureListener.play();
                }
            case SHOW_SUCCESS:
            case SHOW_FAILURE:
                break;
            case SHOW_READY:
                mReadyListener.play(isSuccessCalled);
                break;
        }

    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (currentState==State.SHOW_PROGRESS
                ||currentState==State.PROGRESS_RUNNING
                ||currentState==State.HIDE_PROGRESS  ) {
            canvas.clipRect(rectClip, Region.Op.REPLACE);
            canvas.drawArc(rectArc, rotation + progress - 1, 360 - progress + 2, false, paintProgressBackground);
            canvas.drawArc(rectArc, rotation, progress, false, paintProgressPrimary);


        }


    }
    @SuppressLint("PrivateResource")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width;
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        final Resources res = getResources();

        switch (getSize()) {
            case SIZE_AUTO:
                int desiredWidth = res.getDimensionPixelSize(R.dimen.design_fab_size_normal);
                if (widthMode == View.MeasureSpec.EXACTLY) {
                    width = widthSize;
                } else if (widthMode == View.MeasureSpec.AT_MOST) {
                    width = Math.min(desiredWidth, widthSize);
                } else {
                    width = desiredWidth;
                }
                break;
            case SIZE_MINI:
                width = res.getDimensionPixelSize(R.dimen.design_fab_size_mini);
                break;
            case SIZE_NORMAL:
            default:
                width = res.getDimensionPixelSize(R.dimen.design_fab_size_normal);
        }
        //noinspection SuspiciousNameCombination
        setMeasuredDimension(width, width);
    }
    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
        super.onSizeChanged(xNew, yNew, xOld, yOld);
        rectButton.set(0, 0, xNew, yNew);
        updateBound(paintProgressPrimary.getStrokeWidth());



    }
    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (currentState == State.READY) {
                startProgressAnimation();
                return performClick();
            }

        }
        return true;
    }



    //P U B L I C  M E T H O D S

    //listener
    public void setStateListener(Contract.StateListener listener){
        mListener=listener;
    }

    //progress
    @Override
    public void setProgress(@IntRange(from=0, to=100) int progress) {
        if (isIndeterminate) {
            throw new IllegalArgumentException("Can´t use this method in indeterminate mode");
        } else if (progress < 0 || progress > 100) {
            throw new IllegalArgumentException("the progress need to be between 0 and 100");
        }
        this.progress = (int) (progress * 3.6);
        mProgressListener.updateRotationSpeed();

    }
    @Override
    public int getProgress() {
        return progress;
    }
    @Override
    public boolean isIndeterminate() {
        return isIndeterminate;
    }
    /**
     * Change the indeterminate mode for this progress bar. In indeterminate mode,
     * the progress is ignored and the progress bar shows an infinite animation instead.
     *
     * @param isIndeterminate true to enable the indeterminate mode.
     */
    @Override
    public void setIndeterminate(boolean isIndeterminate) {
        if(currentState!=State.READY){
            throw new IllegalStateException("You can't change the type of progress when it is already running ");
        }
        this.isIndeterminate = isIndeterminate;
    }
    @Override
    public void setProgressWidth(@IntRange(from=2)int maxProgressStrokeWidth) {
        this.maxProgressStrokeWidth = maxProgressStrokeWidth;
    }
    @Override
    public float getProgressWidth() {
        return maxProgressStrokeWidth;
    }

    //progress animation
    @Override
    public void startProgressAnimation() {
        if (currentState != State.READY) {
            throw new IllegalStateException("One progress animation is already started ");
        }
        isSuccessCalled=null;
        mProgressListener.playShowProgress();

    }
    @Override
    public void setProgressShowTime(@IntRange(from=0)long duration) {
        this.progressShowTime = duration;
    }
    @Override
    public long getProgressShowTime() {
        return progressShowTime;
    }
    @Override
    public void setProgressHideTime(@IntRange(from=0)long duration) {
        this.progressHideTime = duration;
    }
    @Override
    public long getProgressHideTime() {
        return progressHideTime;
    }

    //success animation
    @Override
    public void startSuccessAnimation()  {
        if (currentState == State.READY) {
            throw new IllegalStateException("can't call startSuccessAnimation when the progress is not started");
        }
        else if(isSuccessCalled!=null){
            String methodCalled=isSuccessCalled?"isSuccessCalled":"isFailureCalled";
            throw new IllegalStateException("a "+methodCalled+" was previously called");
        }else {
            isSuccessCalled = true;
            if (currentState == State.SHOW_PROGRESS) mProgressListener.cancelShowProgress();
            mProgressListener.playHideProgress();
        }
    }
    @Override
    public void setSuccessShowTime(@IntRange(from=0)long duration) {
        this.successShowTime = duration;
        mSuccessListener.recreate(getContext());
    }
    @Override
    public long getSuccessShowTime() {
        return successShowTime;
    }
    @Override
    public long getSuccessStayTime() {
        return successStayTime;
    }
    @Override
    public void setSuccessStayTime(@IntRange(from=0)long successStayTime) {
        this.successStayTime = successStayTime;
    }

    //failure animation
    @Override
    public void startFailureAnimation()  {
        if (currentState == State.READY) {
            throw new IllegalStateException("can't call startFailureAnimation when the progress is not started");
        }
        else if(isSuccessCalled!=null){
            String methodCalled=isSuccessCalled?"isSuccessCalled":"isFailureCalled";
            throw new IllegalStateException("a "+methodCalled+" was previously called");
        }
        else {
            isSuccessCalled = false;
            if (currentState == State.SHOW_PROGRESS) mProgressListener.cancelShowProgress();
            mProgressListener.playHideProgress();


        }

    }
    @Override
    public void setFailureShowTime(@IntRange(from=0)long duration) {
        this.failureShowTime = duration;
        mFailureListener.recreate(getContext());
    }
    @Override
    public long getFailureShowTime() {
        return failureShowTime;
    }
    @Override
    public void setFailureStayTime(@IntRange(from=0)long failureTime) {
        this.failureStayTime = failureTime;
    }
    @Override
    public long getFailureStayTime() {
        return failureStayTime;
    }

    //ready animation
    @Override
    public void setReadyShowTime(@IntRange(from=0)long duration) {
        this.readyShowTime = duration;
        mReadyListener.recreate(getContext());
    }
    @Override
    public long getReadyShowTime() {
        return readyShowTime;
    }



    //P R O T E C T E D   M E T H O D S

    //colors
    @Override  @ColorInt
    protected int getColorReady() {
        return colorReady;
    }
    @Override  @ColorInt
    protected int getColorSuccess() {
        return colorSuccess;
    }
    @Override  @ColorInt
    protected int getColorFailure() {
        return colorFailure;
    }

    //progress rotation
    @Override
    protected int getProgressRotation() {
        return rotation;
    }
    @Override
    protected void setProgressRotation(int rotation) {
        this.rotation=rotation;
    }

    //indeterminate progress
    @Override
    protected void setProgressIndeterminate(int progress) {
        this.progress=progress;
    }

    //progress width
    @Override
    protected void setCurrentProgressWidth(float strokeWidth) {
        paintProgressPrimary.setStrokeWidth(strokeWidth);
        paintProgressBackground.setStrokeWidth(strokeWidth);
        updateBound(strokeWidth);
    }
    @Override
    protected float getCurrentProgressWidth() {
        return paintProgressPrimary.getStrokeWidth();
    }



    //U T I L S
    private void updateBound(final float strokeWidth) {
        RectF r = new RectF(rectButton);
        r.inset(-strokeWidth / 2, -strokeWidth / 2);
        rectArc.set(r);
        r.inset(-strokeWidth / 2, -strokeWidth / 2);
        rectClip.set(r);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setOutlineProvider(viewOutlineProvider);
        }
        invalidate();
    }



}
