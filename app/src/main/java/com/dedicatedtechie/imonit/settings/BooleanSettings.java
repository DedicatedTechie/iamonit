package com.dedicatedtechie.imonit.settings;


class BooleanSettings {
    private boolean progressBarWanted;
    private boolean vibrateOnReminder;
    private boolean vibrateOnFinish;
    private boolean completeSoundWanted;
    private boolean reminderSoundWanted;
    private boolean timerCountsUp;
    private boolean isCounting;
    private boolean darkModeWanted;

    public boolean isDarkModeWanted() {
        return darkModeWanted;
    }

    boolean isProgressBarWanted() {
        return progressBarWanted;
    }

    void setProgressBarWanted(boolean progressBarWanted) {
        this.progressBarWanted = progressBarWanted;
    }

    boolean isVibrateOnReminder() {
        return vibrateOnReminder;
    }

    void setVibrateOnReminder(boolean vibrateOnReminder) {
        this.vibrateOnReminder = vibrateOnReminder;
    }

    boolean isVibrateOnFinish() {
        return vibrateOnFinish;
    }

    void setVibrateOnFinish(boolean vibrateOnFinish) {
        this.vibrateOnFinish = vibrateOnFinish;
    }

    boolean isCompleteSoundWanted() {
        return completeSoundWanted;
    }

    void setCompleteSoundWanted(boolean completeSoundWanted) {
        this.completeSoundWanted = completeSoundWanted;
    }

    boolean isReminderSoundWanted() {
        return reminderSoundWanted;
    }

    void setReminderSoundWanted(boolean reminderSoundWanted) {
        this.reminderSoundWanted = reminderSoundWanted;
    }


    boolean isTimerCountsUp() {
        return timerCountsUp;
    }

    void setTimerCountsUp(boolean timerCountsUp) {
        this.timerCountsUp = timerCountsUp;
    }

    boolean isCounting() {
        return isCounting;
    }

    void setCounting(boolean counting) {
        isCounting = counting;
    }

    public void setDarkModeWanted(boolean darkmodeWanted) {
        this.darkModeWanted=darkmodeWanted;
    }
}
