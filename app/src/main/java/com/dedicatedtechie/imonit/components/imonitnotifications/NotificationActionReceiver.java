package com.dedicatedtechie.imonit.components.imonitnotifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationActionReceiver extends BroadcastReceiver {
    public NotificationActionReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //This is used to close the notification tray
        Intent closeNotification = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(closeNotification);
    }
}
