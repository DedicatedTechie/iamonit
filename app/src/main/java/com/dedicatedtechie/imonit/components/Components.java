package com.dedicatedtechie.imonit.components;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatTextView;

import com.dedicatedtechie.imonit.R;
import com.dedicatedtechie.imonit.components.controls.Controls;
import com.dedicatedtechie.imonit.components.reminderComponents.MPs;
import com.dedicatedtechie.imonit.components.reminderComponents.ReminderComponents;
import com.dedicatedtechie.imonit.components.reminderComponents.TimerVibrator;
import com.dedicatedtechie.imonit.components.timers.FocusTimer;
import com.dedicatedtechie.imonit.components.timers.TaskTimer;
import com.dedicatedtechie.imonit.components.timers.TimerFunctions;
import com.dedicatedtechie.imonit.components.visualComponents.VisualComponents;
import com.dedicatedtechie.imonit.components.watchComponents.Hand;
import com.dedicatedtechie.imonit.components.watchComponents.WatchComponents;
import com.dedicatedtechie.imonit.settings.NotificationSettings;
import com.dedicatedtechie.imonit.settings.SettingsObject;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static com.dedicatedtechie.imonit.components.timers.TimerFunctions.TIMER_DOWN;
import static com.dedicatedtechie.imonit.components.timers.TimerFunctions.TIMER_UP;
import static com.dedicatedtechie.imonit.settings.NotificationSettings.TYPE_COMPLETE;
import static com.dedicatedtechie.imonit.settings.NotificationSettings.TYPE_REMINDER;

public class Components {
    public static final int GRANULARITY = 15;
    public static final int TIMER_MAX = 3600000;
    private final SettingsObject settingsObject;
    private final Activity activity;
    private final NotificationSettings notificationSettings;
    private ReminderComponents reminderComponents;
    private TaskTimer taskTimer;
    private VisualComponents visualComponents;
    private Controls controls;


    public Components(Activity _activity, SettingsObject _settingsObject) {
        this.settingsObject = _settingsObject;
        boolean _reminderWanted = settingsObject.reminderSoundIsWanted();
        boolean _completionWanted = settingsObject.completeSoundIsWanted();
        this.activity = _activity;
        MPs _mPs = new MPs(_activity, _reminderWanted, _completionWanted);
        TimerVibrator _timerVibrator = new TimerVibrator();
        this.reminderComponents = new ReminderComponents(_mPs, _timerVibrator);
        this.notificationSettings = new NotificationSettings(getActivity().getApplicationContext(), this);
    }

    public VisualComponents getVisualComponents() {
        return visualComponents;
    }

    private long clip(long var) {
        final long min = 0;
        int chunks = (int) (((float) var / (float) Components.GRANULARITY) / 1000f);
        long roundedVar = (long) (chunks * 1000f * Components.GRANULARITY);

        if (roundedVar < min)
            return min;
        if (roundedVar > TIMER_MAX)
            return TIMER_MAX;
        else return roundedVar;
    }

    /// Called by Main Activity's onCreate.  Finds the views(stored in the viewList) and associates them with the objects
    public void sendViewsToComponents(Activity _activity, ViewList _viewList) {
        //Controls
        View[] _incrementViewArray = _viewList.getIncrementButtonViewArray();
        View[] _startStopButtonViewArray = _viewList.getStartStopButtonViewArray();
        View[] _countUpDownSwitchArray = _viewList.getCountUpDownSwitchArray();
        View[] _seekBarViewArray = _viewList.getSeekBarViewArray();
        //Log.i(TAG, "sendViewsToComponents: controls updated with arrays");
        this.controls = new Controls(this, _incrementViewArray, _startStopButtonViewArray, _countUpDownSwitchArray, _seekBarViewArray);

        //ReminderComponents
        MPs _mediaPlayers = new MPs(_activity, settingsObject.reminderSoundIsWanted(), settingsObject.completeSoundIsWanted());
        TimerVibrator _timerVibrator = new TimerVibrator();
        this.reminderComponents = new ReminderComponents(_mediaPlayers, _timerVibrator);

        //WatchComponents
        Hand _hand = (Hand) _viewList.getWatchHandView();
        ImageView _watchView = (ImageView) _viewList.getWatchImageView();
        ImageView _face = (ImageView) _viewList.getWatchFaceView();
        WatchComponents watchComponents = new WatchComponents(_watchView, _hand, _face);

        //VisualComponents
        AppCompatTextView _digitalReadoutTextView = (AppCompatTextView) _viewList.getDigitalReadout();
        ProgressBar _progressBar = (ProgressBar) _viewList.getProgressBar();
        this.visualComponents = new VisualComponents(this.controls, watchComponents, _digitalReadoutTextView, _progressBar);
    }

    /**
     * @param _timeInMillis the time in millis that the indicators should be set to
     * @param source        ViewList.ControlComponentNumber that called the update
     * @param fromUser      if the input is from the user, not by automatic timer-tick, then the Task-Length will also be set.
     */
    public void notifyComponentsOfChange(long _timeInMillis, int source, final boolean fromUser) {
        //Log.i(TAG, "Components.notifyComponentsOfChange@123: " + new DecimalFormat("#.###").format((float) _timeInMillis / 1000f / 60f) + " min");
        long currentTimeInMillis;
        long taskLengthInMillis;

        if (fromUser) { //change the overall task length

            //clip to min and max, and round to nearest GRANULARITY seconds, but keep in millis.
            taskLengthInMillis = clip(_timeInMillis);

            //the two are equal if it's not just a tick
            currentTimeInMillis = taskLengthInMillis;

            taskTimer = null;
            settingsObject.setTimerTo(taskLengthInMillis);
        } else { //just a tick

            //Update only the current time using the incoming parameters
            currentTimeInMillis = _timeInMillis;
        }
        updateCurrentTime(source, currentTimeInMillis);
        settingsObject.updateSharedPreferences();
    }

    private void updateCurrentTime(int source, long currentTimeInMillis) {
        long adjustedCurrent = invertIfCountingUp(currentTimeInMillis);

        //controls needs the inverted version
        controls.update(adjustedCurrent, source);
        //visuals needs the inversion
        visualComponents.update(adjustedCurrent);
        //settingsObject needs the NON-inverted number
        settingsObject.setCurrentTimeInMillis(currentTimeInMillis);
    }

    private long invertIfCountingUp(long inputTime) {
        long outputTime;
        if (settingsObject.timerCounts(TIMER_DOWN))
            outputTime = inputTime;
        else //counting up
            outputTime = TIMER_MAX - inputTime;
        return outputTime;
    }

    public TaskTimer getTaskTimer(Context context, SettingsObject _settingsObject) {
        if (taskTimer == null) {

            //When creating a new timer, set it to the CURRENT time, not the ORIGINAL task-length
            //otherwise it will reset to the beginning with every phone rotation
            long _taskLength = _settingsObject.getCurrentTimeInMillis();

            //Log.i(TAG, "Components.getTaskTimer@170: _taskLength " + _taskLength);
            boolean _countingUp = _settingsObject.timerCounts(TIMER_UP);
            taskTimer = new TaskTimer(context, _taskLength, _countingUp, Components.this);
            //Log.i(TAG, "Components.getTaskTimer@175: new TaskTimer: " + _taskLength + " millis");
        }
        return taskTimer;
    }

    public void resetTaskTimerToZero(int perControlComponent) {
        //Log.i(TAG, "Components.resetTaskTimerToZero@184:");
        //marking "fromUser" as TRUE makes sure that the overall taskTimer is set..
        notifyComponentsOfChange(invertIfCountingUp(0), perControlComponent, true);
    }

    public void finish(Context context, TimerFunctions timer, int type) {

        notificationSettings.doNotification(type);

        if (type == TYPE_REMINDER) {
            reminderTimerFinish(context, (FocusTimer) timer);
        } else //its the big timer
            completionTimerFinish(context);
    }

    private void completionTimerFinish(Context context) {
        taskTimer.stopTimer();
        taskTimer = null;

        //make sure to show we're at 0, and not a few millis off
        notifyComponentsOfChange(0, ViewList.ControlComponentNumber.TIMER_TICK_AS_CONTROL, false);

        //we're not counting anymore
        settingsObject.setIsCounting(false);

        //sound your notifications if wanted.
        if (settingsObject.completeSoundIsWanted()) {
            try {
                MPs _mediaPlayers = reminderComponents.getMediaPlayers();
                MediaPlayer _completionMediaPlayer = _mediaPlayers.getMediaPlayer(TYPE_COMPLETE);
                _completionMediaPlayer.start();
            } catch (IllegalStateException e) {
                //Log.i(TAG, "method completionTimerFinish @233: Can't start completion media player");
                e.printStackTrace();
            }
        }
        if (settingsObject.vibratesOnFinish()) {
            TimerVibrator _vibrator = reminderComponents.getTimerVibrator();
            _vibrator.vibrate(context, TYPE_COMPLETE);
        }

        //just in case "reset" it didn't work (i.e. the popup failed, or "OK" wasn't startStopClicked) reset the timer back to the original amount of time.
        if (settingsObject.getCurrentTimeInMillis() != 0) {
            settingsObject.setCurrentTimeInMillis(settingsObject.getTaskLengthInMillis());
        }

        showDismissButton();
    }

    private void showDismissButton() {
        FloatingActionButton dismissFab = activity.findViewById(R.id.dismissFab);
        FloatingActionButton startStopFab = activity.findViewById(R.id.startStopButton);
        dismissFab.setVisibility(View.VISIBLE);
        startStopFab.setVisibility(View.INVISIBLE);
        shakeDismissButtonIcon(dismissFab);
    }

    private void shakeDismissButtonIcon(FloatingActionButton dismissFab) {
        //does nothing for now
        // TODO: 4/3/2020 shake it

    }


    private void reminderTimerFinish(Context context, FocusTimer focusTimer) {
        //Reminder-Timer went off: Stay Focused!
        final SettingsObject _settingsObject = settingsObject;

        //make a sound
        if (_settingsObject.reminderSoundIsWanted())
            try {
                MPs _mediaPlayers = reminderComponents.getMediaPlayers();
                _mediaPlayers.getMediaPlayer(TYPE_REMINDER).start();
            } catch (Exception e) {
                e.printStackTrace();
                //Log.i(TAG, "method reminderTimerFinish @255: can't start reminder media player.");
                Toast.makeText(context, "ERROR: Can't play reminder sound!", Toast.LENGTH_LONG).show();
            }
        //vibrate
        if (_settingsObject.vibratesOnReminder()) {
            TimerVibrator _vibrator = reminderComponents.getTimerVibrator();
            _vibrator.vibrate(context, TYPE_REMINDER);
        }

        //stop the timer and then start it back up again
        focusTimer.cancel();
        try {
            taskTimer.getFocusTimer().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ///turns off touch controls (seekBar, increment buttons, etc) while the timer is counting
    public void stopListening() {
        controls.stopListening();
        visualComponents.getWatchComponents().stopListening();
    }

    public void startListening() {
        //Log.i(TAG, "Components.startListening@282:");
        controls.startListening();
    }

    public SettingsObject getSettingsObject() {
        return settingsObject;
    }


    public void setTaskTimer(TaskTimer _taskTimer) {
        this.taskTimer = _taskTimer;
    }

    public Controls getControls() {
        return controls;
    }

    public void initialize(TaskTimer taskTimer) {
        setTaskTimer(taskTimer);
        long timeInMillis = settingsObject.getCurrentTimeInMillis();
        notifyComponentsOfChange(timeInMillis, ViewList.ControlComponentNumber.SYSTEM, false);
        visualComponents.initialize();

        if (settingsObject.timerCounts(TIMER_DOWN) && !settingsObject.isCounting()) {
            //it's set to DOWN and it's NOT counting, so controls are active
            startListening();
        } else { //perhaps it's UP, or perhaps it's currently counting... either way...
            stopListening();
        }
    }

    public Context getActivity() {
        return activity;
    }

    public NotificationSettings getNotificationSettings() {
        return notificationSettings;
    }
}
