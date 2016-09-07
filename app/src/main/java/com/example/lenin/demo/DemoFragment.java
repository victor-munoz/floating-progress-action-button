package com.example.lenin.demo;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import com.victormunoz.material.widget.Contract;
import com.victormunoz.material.widget.FloatingProgressActionButton;
import com.victormunoz.material.widget.State;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;


public class DemoFragment extends Fragment implements DemoContract.View {
    private DemoContract.UserActionsListener mActionListener;
    private final static int SEEK_BAR_STEP = 100;
    @BindViews({
            R.id.switch_indeterminate
            , R.id.sb_progress_width
            , R.id.sb_hide_progress
            , R.id.sb_show_failure
            , R.id.sb_show_progress
            , R.id.sb_show_success
            , R.id.sb_time_failure
            , R.id.sb_time_ready
            , R.id.sb_time_success
    })
    List<View> settingsViews;
    static final ButterKnife.Setter<View, Boolean> ENABLED = new ButterKnife.Setter<View, Boolean>() {
        @Override
        public void set(@NonNull View view, Boolean value, int index) {
            view.setEnabled(value);
        }
    };
    @BindView(R.id.myFab)
    FloatingProgressActionButton myFab;
    @BindView(R.id.cancel)
    Button cancel;
    @BindView(R.id.orientation)
    Button orientation;
    @BindView(R.id.switch_indeterminate)
    Switch swIndeterminate;
    //progress width
    @BindView(R.id.sb_progress_width)
    SeekBar sbWidth;
    @BindView(R.id.tv_progress_width)
    TextView tvWidth;
    //show progress
    @BindView(R.id.sb_show_progress)
    SeekBar sbShowProgress;
    @BindView(R.id.tv_show_progress)
    TextView tvShowProgress;
    //hide progress
    @BindView(R.id.sb_hide_progress)
    SeekBar sbHideProgress;
    @BindView(R.id.tv_hide_progress)
    TextView tvHideProgress;
    //show success
    @BindView(R.id.sb_show_success)
    SeekBar sbShowSuccess;
    @BindView(R.id.tv_show_sucess)
    TextView tvShowSuccess;
    //time success
    @BindView(R.id.sb_time_success)
    SeekBar sbTimeSuccess;
    @BindView(R.id.tv_time_success)
    TextView tvTimeSuccess;
    //show failure
    @BindView(R.id.sb_show_failure)
    SeekBar sbShowFailure;
    @BindView(R.id.tv_show_failure)
    TextView tvShowFailure;
    //time failure
    @BindView(R.id.sb_time_failure)
    SeekBar sbTimeFailure;
    @BindView(R.id.tv_time_Failure)
    TextView tvTimeFailure;
    //show ready
    @BindView(R.id.sb_time_ready)
    SeekBar sbTimeReady;
    @BindView(R.id.tv_time_ready)
    TextView tvTimeReady;

    @OnClick(R.id.myFab)
    void callAsyncTask() {
        mActionListener.callAsyncTask();
        ButterKnife.apply(settingsViews, ENABLED, false);
    }

    @OnClick(R.id.cancel)
    void cancelAsyncTask() {
        mActionListener.cancelAsyncTask();
    }

    @OnClick(R.id.orientation)
    void changeOrientation() {
        Display display = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        final int orientation = display.getRotation();
        switch (orientation) {
            case Surface.ROTATION_0:
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
            case Surface.ROTATION_90:
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            case Surface.ROTATION_180:
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
            case Surface.ROTATION_270:
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;

        }
    }

    @OnCheckedChanged(R.id.switch_indeterminate)
    void setIndeterminate(boolean isChecked) {
        myFab.setIndeterminate(isChecked);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_cancel_task, container, false);
        ButterKnife.bind(this, root);
        myFab.setStateListener(new Contract.StateListener() {
            @Override
            public void updateState(State state) {
                if(state==State.READY){
                    ButterKnife.apply(settingsViews, ENABLED, true);
                }

            }
        });
        sbWidth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tvWidth.setText(String.valueOf((i * 2) + 4));
                myFab.setProgressWidth((i * 2) + 4);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sbShowProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tvShowProgress.setText(String.valueOf(i * SEEK_BAR_STEP));
                myFab.setProgressShowTime(i * SEEK_BAR_STEP);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sbHideProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tvHideProgress.setText(String.valueOf(i * SEEK_BAR_STEP));
                myFab.setProgressHideTime(i * SEEK_BAR_STEP);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sbShowSuccess.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tvShowSuccess.setText(String.valueOf(i * SEEK_BAR_STEP));
                myFab.setSuccessShowTime(i * SEEK_BAR_STEP);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sbTimeSuccess.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tvTimeSuccess.setText(String.valueOf(i * SEEK_BAR_STEP));
                myFab.setSuccessStayTime(i * SEEK_BAR_STEP);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sbShowFailure.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tvShowFailure.setText(String.valueOf(i * SEEK_BAR_STEP));
                myFab.setFailureShowTime(i * SEEK_BAR_STEP);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sbTimeFailure.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tvTimeFailure.setText(String.valueOf(i * SEEK_BAR_STEP));
                myFab.setFailureStayTime(i * SEEK_BAR_STEP);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sbTimeReady.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tvTimeReady.setText(String.valueOf(i * SEEK_BAR_STEP));
                myFab.setReadyShowTime(i * SEEK_BAR_STEP);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        setRetainInstance(true);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActionListener = new DemoPresenter(this);
    }

    @Override
    public void setProgress(int i) {
        if (!myFab.isIndeterminate()) {
            myFab.setProgress(i);
        }
    }

    @Override
    public void success() {
        myFab.startSuccessAnimation();
    }

    @Override
    public void failure() {
        myFab.startFailureAnimation();

    }


    public static DemoFragment newInstance() {
        return new DemoFragment();
    }

    public DemoFragment() {
        // Required empty public constructor
    }


}
