package com.dedicatedtechie.imonit.components.reminderComponents;


public class ReminderComponents {


    private final MPs mediaPlayers;
    private final TimerVibrator timerVibrator;

    public ReminderComponents(MPs _mediaPlayers, TimerVibrator _timerVibrator) {
        this.mediaPlayers = _mediaPlayers;
        this.timerVibrator = _timerVibrator;

    }

    public MPs getMediaPlayers() {
        return mediaPlayers;
    }

    public TimerVibrator getTimerVibrator() {
        return timerVibrator;
    }




}
