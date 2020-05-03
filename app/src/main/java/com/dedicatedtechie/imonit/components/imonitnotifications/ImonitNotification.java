package com.dedicatedtechie.imonit.components.imonitnotifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationCompat.Action;

import com.dedicatedtechie.imonit.MainActivity;
import com.dedicatedtechie.imonit.R;
import com.dedicatedtechie.imonit.components.Components;
import com.dedicatedtechie.imonit.settings.NotificationSettings;

import static com.dedicatedtechie.imonit.settings.NotificationSettings.TYPE_COMPLETE;
import static com.dedicatedtechie.imonit.settings.NotificationSettings.TYPE_ONGOING;

public class ImonitNotification {

    private final ImOnItNotificationChannel channel;
    boolean alertOnce;
    private String message;
    private String title;
    private String actionText;
    private NotificationSettings notificationSettings;
    private int iconResource;
    private boolean ongoing;

    public ImonitNotification(NotificationSettings notificationSettings, Context context, int _type) {
        this.notificationSettings = notificationSettings;
        ImOnItNotificationChannel notificationChannel = notificationSettings.getNotificationChannel(_type);
        this.channel = notificationChannel;

        initializeFields(_type);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, notificationChannel.getId());
        PendingIntent intent = notificationSettings.createIntent(context, _type);
        Action action = new Action(iconResource, actionText, intent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String id = notificationChannel.getId();
            builder.setChannelId(id);
        } else {//no channel
            int priority = notificationChannel.getPriority();
            builder.setPriority(priority);
        }


        builder.setSmallIcon(iconResource)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setOngoing(ongoing)
                .setOnlyAlertOnce(alertOnce)
                .setContentIntent(intent);
        //.addAction(action)

// TODO: 4/3/2020 actions w/ intents that run methods in onCreate by having MainActivity parse the extras

        Notification notification = builder.build();

        if (_type == TYPE_COMPLETE) {
            notification.flags = Notification.FLAG_INSISTENT;
            notification.category = Notification.CATEGORY_ALARM;
        } else if (_type == TYPE_ONGOING) {
            builder.setSound(null);
        }
        Components components = notificationSettings.getComponents();
        MainActivity mainActivity = (MainActivity) components.getActivity();
        mainActivity.makeNotification(_type, notification);
    }


    private void initializeFields(int type) {
        Context context = notificationSettings.getContext();
        switch (type) {
            case NotificationSettings.TYPE_REMINDER:
                this.message = context.getString(R.string.reminder_notification_message);
                this.title = context.getString(R.string.reminder_notification_title);
                this.actionText = context.getString(R.string.reminder_notification_action_text);
                iconResource = R.drawable.ic_reminder;
                ongoing = false;
                alertOnce = true;
                break;
            case TYPE_COMPLETE:
                this.message = context.getString(R.string.completion_notification_message);
                this.title = context.getString(R.string.completion_notification_title);
                this.actionText = context.getString(R.string.completion_notification_action_text);
                iconResource = R.drawable.ic_checked;
                ongoing = false;
                alertOnce = false;
                break;
            default: //ongoing
                this.message = context.getString(R.string.ongoing_notification_message);
                this.title = context.getString(R.string.ongoing_notification_title);
                this.actionText = context.getString(R.string.ongoing_notification_action_text);
                iconResource = R.drawable.ic_timer;
                ongoing = true;
                alertOnce = true;
                break;
        }
    }

    public String getMessage() {
        return message;
    }

    public String getTitle() {
        return title;
    }

    public String getActionText() {
        return actionText;
    }

    public NotificationSettings getNotificationSettings() {
        return notificationSettings;
    }
}