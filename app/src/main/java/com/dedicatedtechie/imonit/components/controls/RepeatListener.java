package com.dedicatedtechie.imonit.components.controls;

import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

/**
 * A class, that can be used as a TouchListener on any view (e.g. a Button).
 * It cyclically runs a clickListener, emulating keyboard-like behaviour. First
 * click is fired immediately, next one after the initialInterval, and subsequent
 * ones after the normalInterval.
 *
 * <p>Interval is scheduled after the onClick completes, so it has to run fast.
 * If it runs slow, it does not generate skipped onClicks. Can be rewritten to
 * achieve this.
 */
class RepeatListener implements OnTouchListener {

    private final int normalInterval;
    private final OnClickListener clickListener;
    private final Handler handler = new Handler();
    private int initialInterval;
    private View touchedView;

    private final Runnable handlerRunnable = new Runnable() {
        @Override
        public void run() {
            if (touchedView.isEnabled()) {
                handler.postDelayed(this, normalInterval);
                clickListener.onClick(touchedView);
            } else {
                // if the view was disabled by the clickListener, remove the callback
                handler.removeCallbacks(handlerRunnable);
                touchedView.setPressed(false);
                touchedView = null;
            }
        }
    };

    /**
     * @param _initialInterval The interval after first click event
     * @param _normalInterval  The interval after second and subsequent click
     *                        events
     * @param _clickListener   The OnClickListener, that will be called
     *                        periodically
     */
    public RepeatListener(int _initialInterval, int _normalInterval,
                          OnClickListener _clickListener) {
        if (_clickListener == null)
            throw new IllegalArgumentException("null runnable");
        if (_initialInterval < 0 || _normalInterval < 0)
            throw new IllegalArgumentException("negative interval");

        this.initialInterval = _initialInterval;
        this.normalInterval = _normalInterval;
        this.clickListener = _clickListener;
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                handler.removeCallbacks(handlerRunnable);
                handler.postDelayed(handlerRunnable, initialInterval);
                touchedView = view;
                touchedView.setPressed(true);
                clickListener.onClick(view);
                view.performClick();
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                handler.removeCallbacks(handlerRunnable);
                touchedView.setPressed(false);
                touchedView = null;
                return true;
        }

        return false;
    }

}