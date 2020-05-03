package com.dedicatedtechie.imonit.components.timers;

import android.os.CountDownTimer;

import com.dedicatedtechie.imonit.settings.NotificationSettings;


public abstract class TimerFunctions extends CountDownTimer {

    public static final int TYPE_REMINDER = NotificationSettings.TYPE_REMINDER;
    public static final int TYPE_COMPLETE = NotificationSettings.TYPE_COMPLETE;

    //constants used for direction of counting.
    public static final boolean TIMER_UP = true;
    public static final boolean TIMER_DOWN = false;
    private static final int ticksPerSecond = 10;
    private static final int countDownInterval = 1000 / ticksPerSecond;


    TimerFunctions(long millisInFuture) {
        super(millisInFuture + 100, countDownInterval);
    }

    @Override
    public abstract void onTick(long l);

    @Override
    public abstract void onFinish();

}