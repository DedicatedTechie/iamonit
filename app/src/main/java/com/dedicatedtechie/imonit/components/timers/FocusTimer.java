package com.dedicatedtechie.imonit.components.timers;

import android.content.Context;
import android.util.Log;

import com.dedicatedtechie.imonit.components.Components;
import com.dedicatedtechie.imonit.settings.NotificationSettings;

import static com.dedicatedtechie.imonit.MainActivity.TAG;
import static com.dedicatedtechie.imonit.settings.NotificationSettings.TYPE_REMINDER;

public class FocusTimer extends TimerFunctions {
    private final Components components;
    private final Context context;

    FocusTimer(Context _context, long reminderInterval, Components _components) {
        super(reminderInterval);
        this.components = _components;
        this.context = _context;

    }

    @Override
    public void onTick(long l) {
        Log.i(TAG, "FocusTimer.onTick@20: "+l+" remaining");
    }

    @Override
    public void onFinish() {
        components.finish(this.context, this, TYPE_REMINDER);
        NotificationSettings notificationSettings = components.getNotificationSettings();
        notificationSettings.doNotification(TYPE_REMINDER);
    }


}
