package com.dedicatedtechie.imonit.components.timers;

import android.content.Context;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;

import com.dedicatedtechie.imonit.components.Components;
import com.dedicatedtechie.imonit.components.ViewList;
import com.dedicatedtechie.imonit.components.controls.Controls;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static com.dedicatedtechie.imonit.MainActivity.TAG;

public class TaskTimer extends TimerFunctions {

    private final boolean countingUp;
    private final Context context;
    private final Components components;
    private final long time;
    private FocusTimer focusTimer;

    public TaskTimer(Context _context, long _time, boolean _countingUp, Components _components) {
        super(_time);
        this.time = _time;
        this.context = _context;
        this.components = _components;
        this.countingUp = _countingUp;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        //Log.i(TAG, "TaskTimer.onTick (line 28): TICK! " + new DecimalFormat("#.00").format(millisUntilFinished / 1000f) + " sec");
        components.notifyComponentsOfChange(millisUntilFinished, ViewList.ControlComponentNumber.TIMER_TICK_AS_CONTROL, false);

    }

    @Override
    public void onFinish() {
        if (focusTimer != null) {
            focusTimer.cancel();
            focusTimer = null;
        }
        components.finish(context, this, TYPE_COMPLETE);

    }

    /**
     * called by onCreate
     * <p>
     * Uses the concept of "LEVELS" to adjust the interval between reminders.  The number of seconds between reminders is set here.
     * Note:  on the screen, these levels should be displayed as ranging from 1-10.
     * <p>
     * sets "reminderInterval" to match the user's level.
     *
     * @param lev the index number (0-9) representing 1 less than the user's level (1-10).
     */
    private int reminderIntervalFromLevel(int lev) {
        final int MINUTE = 60 * 1000; //1k millis in a sec * 60 sec in a min

        //translate levels to seconds
        SparseIntArray secondsPerLevel = new SparseIntArray();
        //array.put (lev, sec)
        secondsPerLevel.put(0, MINUTE / 2); //level 1 = 30 seconds
        secondsPerLevel.put(1, MINUTE);
        secondsPerLevel.put(2, MINUTE * 2);
        secondsPerLevel.put(3, MINUTE * 3);//level 4 = 3 minutes
        secondsPerLevel.put(4, MINUTE * 4);
        secondsPerLevel.put(5, MINUTE * 5);
        secondsPerLevel.put(6, MINUTE * 10);//level 7 = 10 minutes
        secondsPerLevel.put(7, MINUTE * 15);
        secondsPerLevel.put(8, MINUTE * 20);
        secondsPerLevel.put(9, MINUTE * 30); //level 10 = 30 minutes

        return secondsPerLevel.get(lev - 1);
    }

    /**
     * starts taskTimer (big-timer) & focusTimer(reminder-timer)
     * toasts "next reminder in X" (when appropriate)
     */
    public void startTimer(int _level) {
        components.getControls().changeStartStopButtonIcon(true, false);
        components.setTaskTimer(this);
        components.stopListening(); //turns off increment buttons, seekBar, etc
        int reminderInterval = reminderIntervalFromLevel(_level);
        start();
        if (time > reminderInterval || countingUp) {
            //we're counting up or there's plenty of time for a reminder
            if (focusTimer == null) {
                Log.i(TAG, "TaskTimer.startTimer@91: reminderInterval = " + reminderInterval);
                focusTimer = new FocusTimer(context, reminderInterval, components);
            }
            focusTimer.start();
        }
    }

    //Stop all timers
    public void stopTimer() {
        components.getControls().getCountUpDownSwitch().setVisibility(View.VISIBLE);

        //re-enable watch-face-tapping, buttons, etc
        if (!countingUp) {
            tellComponentsToStartListening();
        }

        //change the button text
        Controls _controls = components.getControls();
        FloatingActionButton _button = _controls.getStartStopButton();
        _controls.changeStartStopButtonIcon(false, true);
        _button.invalidate();

        //get rid of the reminders
        if (focusTimer != null) {
            focusTimer.cancel();
            focusTimer = null;
        }

        //stop the task timer
        cancel();
    }


    public FocusTimer getFocusTimer() {
        return focusTimer;
    }

    private void tellComponentsToStartListening() {
        components.startListening();
    }

}
