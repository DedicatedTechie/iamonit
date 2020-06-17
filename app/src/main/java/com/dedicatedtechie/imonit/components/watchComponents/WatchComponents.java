package com.dedicatedtechie.imonit.components.watchComponents;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.dedicatedtechie.imonit.components.ViewList;
import com.dedicatedtechie.imonit.components.controls.Controls;

public class WatchComponents {

    private final Hand hand;
    private final ImageView watch;
    private final ImageView tappingArea;
    private boolean listening;

    public WatchComponents(ImageView _watchView, Hand _hand, ImageView _tappingArea) {
        this.watch = _watchView;
        this.hand = _hand;
        this.tappingArea = _tappingArea;
    }

    public Hand getHand() {
        return hand;
    }



    public ImageView getWatch() {
        return watch;
    }

    public ImageView getTappingArea() {
        return tappingArea;
    }


    public void update(long timeInMillis) {
        hand.rotate(timeInMillis);
    }


    @SuppressLint("ClickableViewAccessibility")
    public void startListening() {
        //Log.i(TAG, "WatchComponents.startListening@103: start");
        listening = true;
    }

    public void initialize(Controls _controls) {
        createListener(_controls);
    }

    private void createListener(Controls _controls) {
        tappingArea.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            //don't do anything until the watchFace has been drawn
            @SuppressWarnings("Convert2Lambda")
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onGlobalLayout() {
                if (tappingArea.getWidth() > 0) {
                    tappingArea.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                //Log.i(TAG, "method onGlobalLayout @105: face is drawn");
                //Log.i(TAG, WatchComponents.class.getSimpleName() + "> onGlobalLayout() @106: " + tappingArea.getWidth() + " = tappingArea.getWidth()");
                //Log.i(TAG, WatchComponents.class.getSimpleName() + "> onGlobalLayout() @106: " + tappingArea.getHeight() + " = tappingArea.getHeight()");
                //find center of watch for tapping at centerPt
                final Point centerPt = center(tappingArea);

                //now that the imageView has been drawn, and the centerPoint calculated, listen for touches.

                View.OnTouchListener touchListener = new View.OnTouchListener() {
                    @SuppressLint("ClickableViewAccessibility")
                    @Override
                    public boolean onTouch(View view, MotionEvent event) {
                        if (listening) {
                            //Log.i(TAG, "WatchComponents.onTouchListener@127: listening");
                            //Log.i(TAG, "method startListening @100: Tap Detected.");
                            if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                                long taskLengthInMillis = new WatchMath().taskLengthFromEvent(event, centerPt);
                                //Log.i(TAG, "WatchComponents.onGlobalLayout@124: task length from tap:" + new DecimalFormat("#.###").format((float) taskLengthInMillis / 1000f / 60f) + " min");
                                //look to see if the tap has changed anything
                                long taskLengthFromSettings = _controls.getComponents().getSettingsObject().getTaskLengthInMillis();
                                //Log.i(TAG, "WatchComponents.onGlobalLayout@127: task length from settings:" + new DecimalFormat("#.###").format((float) taskLengthFromSettings / 1000f / 60f) + " min");
                                if (taskLengthInMillis == taskLengthFromSettings) {
                                    //the tap hasn't changed anything.
                                    //Log.i(TAG, "WatchComponents.onGlobalLayout@130: no change in time.  No need to update components.");
                                    return true;
                                }//else the tap changed something
                                //Log.i(TAG, "WatchComponents.onGlobalLayout@133: this tap event (" + event.getAction() + ") changed the time to " + new DecimalFormat("#.###").format((float) taskLengthFromSettings / 1000f / 60f) + " min");
                                _controls.notifyComponentsOfChange(taskLengthInMillis, ViewList.ControlComponentNumber.WATCH_TAP_AS_CONTROL, true);
                                return true; //tap handled.
                            } else { // touch event is ACTION_UP
                                //Log.i(TAG, "WatchComponents.onGlobalLayout@137: touch_UP action.  do nothing.");
                                //don't do anything on touch action_up
                                //Log.i(TAG, "WatchComponents.onGlobalLayout@139: task length from settings:" + new DecimalFormat("#.###").format((float) _controls.getComponents().getSettingsObject().getCurrentTimeInMillis() / 1000f / 60f) + " min");

                                return false; //tap not handled
                            }
                        } else {//we're not listening.
                            //Log.i(TAG, "WatchComponents.onTouchLIstener@151: NOT listening");
                            //tap handled by ignoring it.
                            return true;
                        }
                    }
                };
                tappingArea.setOnTouchListener(touchListener);
            }
        });
    }

    /**
     * @param view the view that you want to find the center of
     * @return a Point that is located at the center of the View
     */
    private Point center(final View view) {
        Point ctr = new Point();

        float w = view.getWidth();
        float h = view.getHeight();

        int width = (int) (w / 2);
        int height = (int) (h / 2);

        ctr.set(width, height);
        return ctr;
    }


    public void stopListening() {
        listening = false;
    }
}
