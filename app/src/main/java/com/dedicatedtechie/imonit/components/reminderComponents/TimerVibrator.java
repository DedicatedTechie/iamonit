package com.dedicatedtechie.imonit.components.reminderComponents;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Vibrator;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.dedicatedtechie.imonit.R;
import com.dedicatedtechie.imonit.components.timers.TimerFunctions;


public class TimerVibrator {

    private final static int REMINDER_VIBRATE_TIME = 1000;
    private final static int COMPLETION_VIBRATE_TIME = 2500;

    public TimerVibrator() {
        //Log.i(TAG, "method TimerVibrator @22: New TimerVibrator created.");
    }

    public void vibrate(final Context context, final int type) {
        final int duration = type == TimerFunctions.TYPE_REMINDER ? REMINDER_VIBRATE_TIME : COMPLETION_VIBRATE_TIME;
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED) {
            //vibration permission issues.
            Toast.makeText(context, R.string.vibrate_permission_error, Toast.LENGTH_LONG).show();
        } else {
            if (v == null) {
                Toast.makeText(context, R.string.vibrator_null_error, Toast.LENGTH_SHORT).show();
            } else {
                if (v.hasVibrator()) {
                    //Toast.makeText(context, "BUZZ", Toast.LENGTH_SHORT).show();
                    v.vibrate(duration);
                } else {
                    //device has no vibrator
                    Toast.makeText(context, R.string.no_vibrator_error, Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
