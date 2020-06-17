package com.dedicatedtechie.imonit.components.visualComponents;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;

import com.dedicatedtechie.imonit.components.controls.Controls;
import com.dedicatedtechie.imonit.components.watchComponents.Hand;
import com.dedicatedtechie.imonit.components.watchComponents.WatchComponents;
import com.dedicatedtechie.imonit.settings.SettingsObject;

import java.util.Locale;

import static com.dedicatedtechie.imonit.components.timers.TimerFunctions.TIMER_DOWN;

public class VisualComponents {

    private final Controls controls;
    private final WatchComponents watchComponents;
    private final TextView digitalReadout;
    private final ProgressBar progressBar;

    public VisualComponents(Controls _controls, WatchComponents _watchComponents, AppCompatTextView _digitalReadout, ProgressBar _timerProgressBar) {

        //controls
        this.controls = _controls;

        //watchComponents
        ImageView _watchView = _watchComponents.getWatch();
        Hand _handView = _watchComponents.getHand();
        ImageView _faceView = _watchComponents.getTappingArea();
        this.watchComponents = new WatchComponents(_watchView, _handView, _faceView);

        //digitalReadout
        this.digitalReadout = _digitalReadout;

        //progressBar
        this.progressBar = _timerProgressBar;
    }

    public void showOrHideProgressBar() {
        SettingsObject _settingsObject = controls.getComponents().getSettingsObject();
        ProgressBar progressBar = getProgressBar();
        if (_settingsObject.progressBarIsWanted()
                && _settingsObject.timerCounts(TIMER_DOWN)
                && _settingsObject.isCounting()) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    public WatchComponents getWatchComponents() {
        return watchComponents;
    }


    private ProgressBar getProgressBar() {
        return progressBar;
    }


    public void update(long _timeRemainingInMillis) {
        updateWatchComponents(_timeRemainingInMillis);
        updateDigitalReadout(_timeRemainingInMillis);
        updateProgressBar(_timeRemainingInMillis);
    }

    private void updateWatchComponents(long _timeRemainingInMillis) {
        watchComponents.update(_timeRemainingInMillis);
    }

    private void updateProgressBar(long _timeRemainingInMillis) {
        long _taskLength = controls.getComponents().getSettingsObject().getTaskLengthInMillis();

        progressBar.setProgress((int) _timeRemainingInMillis);
        progressBar.setMax((int) _taskLength);
    }

    private void updateDigitalReadout(long _timeRemainingInMillis) {
        float _timeRemainingInSeconds = (long) (_timeRemainingInMillis / 1000f);
        digitalReadout.setText(formatMmSs((long) _timeRemainingInSeconds));
    }

    //utility to convert seconds into nicely formatted "mm:ss" text
    private String formatMmSs(long timeInSeconds) {
        float minutes = (float) Math.floor(((float) timeInSeconds) / 60f);
        float seconds = (float) Math.ceil(((float) timeInSeconds) % 60f);
        return String.format(Locale.getDefault(), "%02d:%02d", (int) minutes, (int) seconds);
    }

    public void initialize() {
        showOrHideProgressBar();
        watchComponents.initialize(controls);
    }
}
