package com.dedicatedtechie.imonit.components;

import android.view.View;

public class ViewList {
    private View[] incrementViewArray;
    private final View[] visualViews;
    private final View[] watchComponentViews;
    private final View[][] controlViewsAsArrays;


    /**
     * @param controlViewsAsArrays incrementViewArray, startStopButtonViewArray, directionSwitchViewArray, seekBarViewArray
     * @param _visualViews         DIGITAL_READOUT, PROGRESS_BAR, TIME_SPEND, TIME_LEFT
     * @param _watchComponentViews HAND, NEEDLE, FACE
     */
    public ViewList(View[][] controlViewsAsArrays, View[] _visualViews, View[] _watchComponentViews) {
        this.visualViews = _visualViews;
        this.watchComponentViews = _watchComponentViews;
        this.controlViewsAsArrays = controlViewsAsArrays;
        this.incrementViewArray = this.controlViewsAsArrays[0];
        this.incrementViewArray = controlViewsAsArrays[ControlComponentNumber.INCREMENT_BUTTON_ARRAY];
    }

    private View[][] getControlViewsAsArrays() {
        return controlViewsAsArrays;
    }


    View[] getIncrementButtonViewArray() {
        return incrementViewArray;
    }


    View getWatchImageView() {
        return watchComponentViews[WatchComponentNumber.WATCH];
    }

    View getWatchHandView() {
        return watchComponentViews[WatchComponentNumber.HAND];
    }

    View getWatchFaceView() {
        return watchComponentViews[WatchComponentNumber.FACE];
    }

    View getDigitalReadout() {
        return visualViews[VisualComponentNumber.DIGITAL_READOUT];
    }

    View getProgressBar() {
        return visualViews[VisualComponentNumber.PROGRESS_BAR];
    }

    View[] getStartStopButtonViewArray() {
        return getControlViewsAsArrays()[ControlComponentNumber.START_STOP_BUTTON_ARRAY];
    }

    View[] getCountUpDownSwitchArray() {

        return getControlViewsAsArrays()[ControlComponentNumber.COUNT_UP_DOWN_SWITCH_ARRAY];
    }

    View[] getSeekBarViewArray() {
        return getControlViewsAsArrays()[ControlComponentNumber.SEEK_BAR_ARRAY];
    }

    public static class IncrementButtonNumber {
        public final static int MIN_UP = 0;
        public final static int MIN_DOWN = 1;
        public final static int SEC_UP = 2;
        public final static int SEC_DOWN = 3;
        public final static int MIN_TEXT = 4;
        public final static int SEC_TEXT = 5;
        public final static int INC_LAYOUT = 6;
    }

    public static class VisualComponentNumber {
        public final static int DIGITAL_READOUT = 0;
        public final static int PROGRESS_BAR = 1;
    }

    public static class WatchComponentNumber {
        public final static int WATCH = 0;
        public final static int HAND = 1;
        public final static int FACE = 2;
    }

    public static class ControlComponentNumber {
        public static final int SYSTEM = 0;
        public final static int INCREMENT_BUTTON_ARRAY = 1;
        public final static int START_STOP_BUTTON_ARRAY = 2;
        public final static int COUNT_UP_DOWN_SWITCH_ARRAY = 3;
        public final static int SEEK_BAR_ARRAY = 4;
        public static final int WATCH_TAP_AS_CONTROL = 5;
        public static final int TIMER_TICK_AS_CONTROL = 6;
    }
}
