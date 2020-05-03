package com.dedicatedtechie.imonit.settings;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.dedicatedtechie.imonit.R;

import static com.dedicatedtechie.imonit.MainActivity.TAG;
import static com.dedicatedtechie.imonit.components.timers.TimerFunctions.TIMER_DOWN;
import static com.dedicatedtechie.imonit.components.timers.TimerFunctions.TIMER_UP;

public class SettingsObject {
    private final Activity activity;
    private final BooleanSettings booleanSettings;
    private int level;
    private long taskLengthInMillis = 0;
    private long currentTimeInMillis;

    public SettingsObject(Activity _activity) {
        this.activity = _activity;
        this.booleanSettings = new BooleanSettings();
        retrieveSharedPreferences();
    }

    public long getCurrentTimeInMillis() {
        return currentTimeInMillis;
    }

    public void setCurrentTimeInMillis(long currentTime_) {
        this.currentTimeInMillis = currentTime_;
    }

    public boolean isCounting() {
        return booleanSettings.isCounting();
    }

    public void setIsCounting(boolean counting) {
        booleanSettings.setCounting(counting);
    }

    public long getTaskLengthInMillis() {
        return taskLengthInMillis;
    }

    public void setTimerTo(long timerSetTo_) {
        this.taskLengthInMillis = timerSetTo_;
    }

    public void setTimerCounts(boolean up) {
        booleanSettings.setTimerCountsUp(up);
    }


    public boolean timerCounts(boolean up) {
        //if up is true, then timer counts up.
        return up == booleanSettings.isTimerCountsUp();
    }

    public int getLevel() {
        if (level == 0) {
            level = 3;//use default
        }
        return level;
    }

    private void setLevel(int level_) {
        this.level = level_;
    }


    public boolean progressBarIsWanted() {
        return booleanSettings.isProgressBarWanted();
    }

    private void setProgressBarWanted(boolean progressBarWanted_) {
        booleanSettings.setProgressBarWanted(progressBarWanted_);
    }

    public boolean vibratesOnReminder() {
        return booleanSettings.isVibrateOnReminder();
    }

    private void setVibrateOnReminder(boolean vibrateOnReminder_) {
        booleanSettings.setVibrateOnReminder(vibrateOnReminder_);
    }

    public boolean vibratesOnFinish() {
        return booleanSettings.isVibrateOnFinish();
    }

    private void setVibrateOnFinish(boolean vibrateOnFinish_) {
        booleanSettings.setVibrateOnFinish(vibrateOnFinish_);
    }

    public boolean completeSoundIsWanted() {
        return booleanSettings.isCompleteSoundWanted();
    }

    private void setCompleteSoundWanted(boolean completeSoundWanted_) {
        booleanSettings.setCompleteSoundWanted(completeSoundWanted_);
    }

    public boolean reminderSoundIsWanted() {
        return booleanSettings.isReminderSoundWanted();
    }


    public void retrieveSharedPreferences() {
        Log.i(TAG, "SettingsObject.retrieveSharedPreferences@110: retrieving SP");
        try {
            SharedPreferences prefs;
            prefs = PreferenceManager.getDefaultSharedPreferences(activity);

            //visuals
            boolean progressBarWanted = prefs.getBoolean(activity.getString(R.string.show_green_to_red_progress_bar), true);
            setProgressBarWanted(progressBarWanted);
            //Log.i(TAG, "SettingsObject.retrieveSharedPreferences@164: progressBarWanted: " + progressBarWanted);

            boolean darkmodeWanted = prefs.getBoolean("darkmode", false);
            setDarkModeWanted(darkmodeWanted);

            //Reminders
            String levelStatementString = prefs.getString(activity.getString(R.string.level_setting), "Level 3 -- 2:00");
            String intString = getLevelIntFromLevelDescriptionString(levelStatementString);
            int intLevel = Integer.parseInt(intString);

            setLevel(intLevel);

            boolean vibrateOnReminder = prefs.getBoolean(activity.getString(R.string.vibrateOnReminder), true);
            setVibrateOnReminder(vibrateOnReminder);

            boolean soundOnReminder = prefs.getBoolean(activity.getString(R.string.play_sound_on_reminder), true);
            setReminderSoundWanted(soundOnReminder);


            //completion
            boolean vibrateOnFinish = prefs.getBoolean(activity.getString(R.string.vibrate_on_finish), true);
            setVibrateOnFinish(vibrateOnFinish);

            boolean soundOnFinish = prefs.getBoolean(activity.getString(R.string.play_sound_on_finish), true);
            setCompleteSoundWanted(soundOnFinish);


            //currentState
            setTimerTo(prefs.getLong(activity.getString(R.string.timerSetTo), 0));
            setIsCounting(prefs.getBoolean(activity.getString(R.string.isCounting), false));
            setCurrentTimeInMillis(prefs.getLong(activity.getString(R.string.currentTime), getTaskLengthInMillis()));
            setTimerCounts(prefs.getBoolean(activity.getString(R.string.timerCountsUp), TIMER_DOWN));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isDarkModeWanted() {
        return booleanSettings.isDarkModeWanted();
    }

    public void setDarkModeWanted(boolean _darkmodeWanted) {
        booleanSettings.setDarkModeWanted(_darkmodeWanted);
    }

    private String getLevelIntFromLevelDescriptionString(String levelStatementString) {
        String levelSubString = null;
        try {
            levelSubString = levelStatementString.substring(6, 8);
            return levelSubString.replaceAll("[^0-9.]", "");
        } catch (Exception e) {
            e.printStackTrace();
            return "3";
        }
    }


    public void updateSharedPreferences() {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(activity).edit();
        //SharedPreferences.Editor editor = activity.getSharedPreferences("com.dedicatedtechie.imonit", MODE_PRIVATE).edit();
        editor.putLong(activity.getString(R.string.timerSetTo), taskLengthInMillis);
        editor.putLong(activity.getString(R.string.currentTime), currentTimeInMillis);
        editor.putBoolean(activity.getString(R.string.isCounting), booleanSettings.isCounting());
        editor.putBoolean(activity.getString(R.string.timerCountsUp), timerCounts(TIMER_UP));
        editor.apply();
    }

    private void setReminderSoundWanted(boolean bool) {
        booleanSettings.setReminderSoundWanted(bool);
    }
}
