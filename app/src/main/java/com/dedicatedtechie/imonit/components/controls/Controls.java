package com.dedicatedtechie.imonit.components.controls;

import android.app.Activity;
import android.view.View;

import androidx.appcompat.widget.SwitchCompat;

import com.dedicatedtechie.imonit.R;
import com.dedicatedtechie.imonit.components.Components;
import com.dedicatedtechie.imonit.components.ViewList;
import com.dedicatedtechie.imonit.components.timers.TaskTimer;
import com.dedicatedtechie.imonit.components.visualComponents.VisualComponents;
import com.dedicatedtechie.imonit.components.watchComponents.WatchComponents;
import com.dedicatedtechie.imonit.settings.SettingsObject;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static com.dedicatedtechie.imonit.components.timers.TimerFunctions.TIMER_DOWN;
import static com.dedicatedtechie.imonit.components.timers.TimerFunctions.TIMER_UP;
import static com.dedicatedtechie.imonit.settings.NotificationSettings.TYPE_ONGOING;

public class Controls {
    private final Components components;
    private final IncrementButtonsLayout incrementButtonsLayout;
    private final FloatingActionButton startStopButton;
    private final SwitchCompat countUpDownSwitch;
    private final TimerSeekBar timerSeekBar;

    /**
     * @param _components             The Components object that holds the Controls
     * @param _incrementViewArray     MIN_UP, MIN_DOWN, SEC_UP, SEC_DOWN, MIN_TEXT, SEC_TEXT, INC_LAYOUT
     * @param _startStopButtonArray   size 1 array of FAB
     * @param _countUpDownSwitchArray size 1 array of SwitchCompat
     * @param _seekBarArray           size 1 array of TimerSeekBar
     */
    public Controls(Components _components, View[] _incrementViewArray, View[] _startStopButtonArray, View[] _countUpDownSwitchArray, View[] _seekBarArray) {
        this.components = _components;
        this.incrementButtonsLayout = (IncrementButtonsLayout) _incrementViewArray[6];
        this.startStopButton = (FloatingActionButton) _startStopButtonArray[0];
        this.countUpDownSwitch = (SwitchCompat) _countUpDownSwitchArray[0];
        this.timerSeekBar = (TimerSeekBar) _seekBarArray[0];
        //timerSeekBar.startListening(); //this gets done in the "initialize" method

        SettingsObject _settingsObject = components.getSettingsObject();
        long _taskLength = _settingsObject.getTaskLengthInMillis();
        incrementButtonsLayout.initIncrementButtons(this, _incrementViewArray, _taskLength);
        this.timerSeekBar.setControls(this);

    }


    public Components getComponents() {
        return components;
    }


    public FloatingActionButton getStartStopButton() {
        return startStopButton;
    }


    @SuppressWarnings("unused")
    public void startStopClicked(Activity activity, View view) {
        //Log.i(TAG, "Controls.startStopClicked@90: CLICK!!!");

        SettingsObject _settingsObject = components.getSettingsObject();
        TaskTimer _taskTimer = getTaskTimer(activity, _settingsObject);

        //find out if we're stopping or starting
        boolean _isCounting = _settingsObject.isCounting();
        if (_isCounting) {//this click is a STOP click
            _settingsObject.setIsCounting(false);
            //Log.i(TAG, "Controls.startStopClicked@98: Stopping.");
            _taskTimer.stopTimer();
            components.setTaskTimer(null);
            //make the TASK time equal the CURRENT time.
            long _CurrentMillisOnTimer = _settingsObject.getCurrentTimeInMillis();
            _settingsObject.setTimerTo(_CurrentMillisOnTimer);
            _settingsObject.setCurrentTimeInMillis(_CurrentMillisOnTimer);
            startListening();
            changeStartStopButtonIcon(false, true);
            //Log.i(TAG, "Controls.startStopClicked@107: stopped.");
            components.getNotificationSettings().cancelNotifications();

        } else { //not yet counting
            _settingsObject.setIsCounting(true);
            //Log.i(TAG, "Controls.startStopClicked@110: starting");
            int level = _settingsObject.getLevel();
            _taskTimer.startTimer(level);
            stopListening();
            changeStartStopButtonIcon(true, false);
            //Log.i(TAG, "Controls.startStopClicked@118: started.");
            components.getNotificationSettings().doNotification(TYPE_ONGOING);
        }
        _settingsObject.updateSharedPreferences();
    }

    public void
    changeStartStopButtonIcon(boolean pause, boolean play) {
        if (pause == play) {
            try {
                throw new IllegalArgumentException("play and pause arguments cannot be equal to each-other");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (pause) {
            //startStopButton.setBackgroundResource(R.drawable.pause);
            startStopButton.setImageResource(R.drawable.pause_fab);
            //Log.i(TAG, "Controls.changeStartStopButtonIcon@129: to 'pause'");
        } else {            //play
            startStopButton.setImageResource(R.drawable.play_fab);
            //Log.i(TAG, "Controls.changeStartStopButtonIcon@132: to 'play'");
        }
        startStopButton.invalidate();
    }


    private TaskTimer getTaskTimer(Activity activity, SettingsObject _settingsObject) {
        return components.getTaskTimer(activity, _settingsObject);
    }


    public SwitchCompat getCountUpDownSwitch() {
        return countUpDownSwitch;
    }


    public void update(long _timeRemainingInMillis, int source) {

        if (source != ViewList.ControlComponentNumber.INCREMENT_BUTTON_ARRAY)
            incrementButtonsLayout.update(_timeRemainingInMillis);

        if (source != ViewList.ControlComponentNumber.SEEK_BAR_ARRAY) {
            timerSeekBar.update(_timeRemainingInMillis);
        }
        if (source != ViewList.ControlComponentNumber.WATCH_TAP_AS_CONTROL) {
            WatchComponents watchComponents = components.getVisualComponents().getWatchComponents();
            watchComponents.update(_timeRemainingInMillis);
        }
    }

    public void stopListening() {
        showOrHideSwitch();
        incrementButtonsLayout.stopListening();
        timerSeekBar.stopListening();
        VisualComponents visualComponents = components.getVisualComponents();
        visualComponents.getWatchComponents().stopListening();
        visualComponents.showOrHideProgressBar();
    }

    private void showOrHideSwitch() {
        SettingsObject settingsObject = components.getSettingsObject();
        if (settingsObject.isCounting()) {
            countUpDownSwitch.setVisibility(View.INVISIBLE);
        } else {
            countUpDownSwitch.setVisibility(View.VISIBLE);
        }
    }

    public void startListening() {
        //Log.i(TAG, "Controls.startListening@198:");
        showOrHideSwitch();
        if (components.getSettingsObject().timerCounts(TIMER_DOWN)) {
            incrementButtonsLayout.startListening();
            timerSeekBar.startListening();
            VisualComponents visualComponents = components.getVisualComponents();
            visualComponents.getWatchComponents().startListening();
            visualComponents.showOrHideProgressBar();
        }
    }

    public void notifyComponentsOfChange(long timeRemainingInMillis, int source, boolean fromUser) {
        //Log.i(TAG, "method Controls.notifyComponentsOfChange @181: Source " + source);

        if (timeRemainingInMillis != components.getSettingsObject().getCurrentTimeInMillis()) {
            //Log.i(TAG, "Controls.notifyComponentsOfChange@208: no change.  Moving on.");
            //only bother going through all this if it's actually a change
            components.notifyComponentsOfChange(timeRemainingInMillis, source, fromUser);
        }
    }

    public void countUpDownSwitchClicked(Activity mainActivity, View directionSwitch) {
        //Log.i(TAG, "method countUpDownSwitchClicked @85 SWITCH!!!");

        //first, if there's a timer, stop it.
        stopTimer(mainActivity);

        //find out which way the switch is pointed now.  Checked = up
        boolean switchPosition = ((SwitchCompat) directionSwitch).isChecked();
        //Log.i(TAG, "Controls.countUpDownSwitchClicked@217: Up? " + switchPosition);

        SettingsObject settingsObject = components.getSettingsObject();

        //display or hide the progressbar, and start or stop listening
        if (switchPosition) {//Count UP
            switchSetToUp(settingsObject);

        } else {//Count Down
            switchSetToDown(settingsObject);
        }

        //everything resets to zero when the switch is tapped.
        components.resetTaskTimerToZero(ViewList.ControlComponentNumber.COUNT_UP_DOWN_SWITCH_ARRAY);

    }

    private void switchSetToDown(SettingsObject settingsObject) {
        settingsObject.setTimerCounts(TIMER_DOWN);
        //Log.i(TAG, "Controls.countUpDownSwitchClicked@207: DOWN");
        startListening();
    }

    private void switchSetToUp(SettingsObject settingsObject) {
        settingsObject.setTimerCounts(TIMER_UP);
        //Log.i(TAG, "Controls.countUpDownSwitchClicked@205: UP");
        stopListening();
    }

    private void stopTimer(Activity mainActivity) {
        SettingsObject _settingsObject = getComponents().getSettingsObject();
        TaskTimer _timer = components.getTaskTimer(mainActivity, _settingsObject);
        if (_timer != null) {
            _timer.stopTimer();
            components.setTaskTimer(null);
        }
    }
}
