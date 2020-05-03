package com.dedicatedtechie.imonit.settings;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.dedicatedtechie.imonit.MainActivity;
import com.dedicatedtechie.imonit.components.Components;
import com.dedicatedtechie.imonit.components.imonitnotifications.ImOnItNotificationChannel;
import com.dedicatedtechie.imonit.components.imonitnotifications.ImonitNotification;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class NotificationSettings {

    public static final int TYPE_REMINDER = 0;
    public static final int TYPE_COMPLETE = 1;
    public static final int TYPE_ONGOING = 2;

    private final ImOnItNotificationChannel reminderChannel;
    private final ImOnItNotificationChannel completionChannel;
    private final ImOnItNotificationChannel ongoingChannel;

    private final Components components;

    public NotificationSettings(Context context, Components _components) {
        this.components = _components;
        reminderChannel = new ImOnItNotificationChannel(context, TYPE_REMINDER);
        completionChannel = new ImOnItNotificationChannel(context, TYPE_COMPLETE);
        ongoingChannel = new ImOnItNotificationChannel(context, TYPE_ONGOING);
    }

    public PendingIntent createIntent(Context _context, int type) {
        Intent notificationIntent = new Intent(_context, MainActivity.class);
        notificationIntent.putExtra("fromNotification", true);
        PendingIntent intent = PendingIntent.getActivity(_context, 0, notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        return intent;
    }

    public void doNotification(int type) {
        cancelNotifications();
        Context context = components.getActivity();
        createNotification(context, type);
        if (type == TYPE_REMINDER) {
            createNotification(context, TYPE_ONGOING
            );
        }
    }

    public void cancelNotifications() {
        Context context = components.getActivity();

        String notificationService = Context.NOTIFICATION_SERVICE;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(notificationService);
        if (notificationManager != null) {
            notificationManager.cancelAll();
        }
    }


    private void createNotification(Context context, int type) {
        new ImonitNotification(this, context, type);
    }

    public ImOnItNotificationChannel getNotificationChannel(int type) {
        switch (type) {
            case TYPE_REMINDER:
                return reminderChannel;
            case TYPE_COMPLETE:
                return completionChannel;
            default:
                return ongoingChannel;
        }
    }

    public Context getContext() {
        return components.getActivity();
    }

    public Components getComponents() {
        return components;
    }

    public void dismissAlarm(View dismissButton, FloatingActionButton startStopButton) {
        dismissButton.setVisibility(View.GONE);
        cancelNotifications();
        startStopButton.setVisibility(View.VISIBLE);
    }
}