package com.dedicatedtechie.imonit.components.reminderComponents;

import android.content.Context;
import android.content.res.Resources;
import android.media.MediaPlayer;

import com.dedicatedtechie.imonit.R;
import com.dedicatedtechie.imonit.components.timers.TimerFunctions;

public class MPs {

    private MediaPlayer reminderMediaPlayer;
    private MediaPlayer completionMediaPlayer;


    public MPs(Context context, boolean reminderWanted, boolean completionWanted) {
        initializeMediaPlayers(context, reminderWanted, completionWanted);
    }

    public MediaPlayer getMediaPlayer(int type) {
        if (type == TimerFunctions.TYPE_REMINDER)
            return reminderMediaPlayer;
        else if (type == TimerFunctions.TYPE_COMPLETE)
            return completionMediaPlayer;
        else try {
                throw new Resources.NotFoundException();
            } catch (Exception e) {
                e.printStackTrace();
            }
        return null;
    }

    private void initializeMediaPlayers(Context context, boolean reminderWanted, boolean completionWanted) {
        if (reminderWanted) {
            reminderMediaPlayer = MediaPlayer.create(context, R.raw.quickbell);
        }
        if (completionWanted) {
            completionMediaPlayer = MediaPlayer.create(context, R.raw.complete);
        }
    }
}
