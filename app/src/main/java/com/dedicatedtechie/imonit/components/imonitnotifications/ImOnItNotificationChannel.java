package com.dedicatedtechie.imonit.components.imonitnotifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import com.dedicatedtechie.imonit.R;
import com.dedicatedtechie.imonit.settings.NotificationSettings;

import static androidx.core.app.NotificationCompat.VISIBILITY_SECRET;
import static com.dedicatedtechie.imonit.settings.NotificationSettings.TYPE_ONGOING;

public class ImOnItNotificationChannel {
    private final Context context;
    private final int type;


    private final String id;
    private final String description;
    private final String name;
    private final int importance;
    private final NotificationChannel channel;

    public ImOnItNotificationChannel(Context _context, int _type) {
        this.type = _type;
        this.context = _context;

        switch (_type) {
            case NotificationSettings.TYPE_REMINDER:
                this.id = _context.getString(R.string.reminder_channel_id);
                this.description = _context.getString(R.string.reminder_channel_description);
                this.name = _context.getString(R.string.reminder_channel_name);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    this.importance = NotificationManager.IMPORTANCE_HIGH;
                } else importance = 3;
                break;
            case NotificationSettings.TYPE_COMPLETE:
                this.id = _context.getString(R.string.completion_channel_id);
                this.description = _context.getString(R.string.completion_channel_description);
                this.name = _context.getString(R.string.completion_channel_name);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    this.importance = NotificationManager.IMPORTANCE_MAX;
                } else importance = 3;
                break;
            default: //ongoing
                this.id = _context.getString(R.string.ongoing_channel_id);
                this.description = _context.getString(R.string.ongoing_channel_description);
                this.name = _context.getString(R.string.ongoing_channel_description);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    this.importance = NotificationManager.IMPORTANCE_LOW;
                } else importance = 3;
                break;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            channel = new NotificationChannel(id, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
                if (_type == TYPE_ONGOING) {
                    channel.setSound(null, null);
                    channel.setLockscreenVisibility(VISIBILITY_SECRET);
                    channel.setVibrationPattern(new long[]{0});
                }
            }
        } else channel = null;
    }

    public NotificationChannel getChannel() {
        return channel;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public int getImportance() {
        return importance;
    }

    public int getType() {
        return type;
    }

    public int getPriority() {
        return importance;
    }
}
