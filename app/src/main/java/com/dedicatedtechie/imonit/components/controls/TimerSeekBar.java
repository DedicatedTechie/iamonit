package com.dedicatedtechie.imonit.components.controls;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.SeekBar;

import com.dedicatedtechie.imonit.components.ViewList;

import static com.dedicatedtechie.imonit.components.Components.GRANULARITY;
import static com.dedicatedtechie.imonit.components.Components.TIMER_MAX;


public class TimerSeekBar extends androidx.appcompat.widget.AppCompatSeekBar {

    private Controls controls;

    public TimerSeekBar(Context context, Controls _controls) {
        super(context);
        this.controls = _controls;
    }

    public TimerSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TimerSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setControls(Controls controls) {
        this.controls = controls;
    }

    @Override
    public synchronized void setMax(int max) {
        int adjustedMax = (max / 1000 / GRANULARITY);
        super.setMax(adjustedMax);
    }

    public void stopListening() {
        setEnabled(false);
        setClickable(false);
        this.setVisibility(View.INVISIBLE);
    }

    public void update(long _timeRemainingInMillis) {
        float timeRemainingInSeconds = _timeRemainingInMillis / 1000f;
        int _timeRemainingInChunks = (int) (timeRemainingInSeconds / GRANULARITY);
        setProgress(_timeRemainingInChunks);
    }

    public void startListening() {
        setEnabled(true);
        setClickable(true);
        setVisibility(View.VISIBLE);
        setMax(TIMER_MAX);
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                setEnabled(true);
                //Log.i(TAG, "method onGlobalLayout @53: seekBar enabled? " + isEnabled());
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        //update everything when the user moves the seekBar
        OnSeekBarChangeListener seekBarChangeListener = new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar sb, int progress, boolean fromUser) {
                //Log.i(TAG, "TimerSeekBar.onProgressChanged@77: from user?"+fromUser);

                if (fromUser) {
                    controls.notifyComponentsOfChange(progress * 1000 * GRANULARITY, ViewList.ControlComponentNumber.SEEK_BAR_ARRAY, true);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar sb) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar sb) {

            }
        };
        setOnSeekBarChangeListener(seekBarChangeListener);
        //Log.i(TAG, "TimerSeekBar.startListening@94:");
    }
}

