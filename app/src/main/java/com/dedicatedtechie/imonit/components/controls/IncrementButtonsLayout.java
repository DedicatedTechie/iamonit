package com.dedicatedtechie.imonit.components.controls;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dedicatedtechie.imonit.R;
import com.dedicatedtechie.imonit.components.ViewList;

import static com.dedicatedtechie.imonit.components.Components.TIMER_MAX;
import static com.dedicatedtechie.imonit.components.ViewList.IncrementButtonNumber.MIN_DOWN;
import static com.dedicatedtechie.imonit.components.ViewList.IncrementButtonNumber.MIN_UP;
import static com.dedicatedtechie.imonit.components.ViewList.IncrementButtonNumber.SEC_DOWN;
import static com.dedicatedtechie.imonit.components.ViewList.IncrementButtonNumber.SEC_UP;

public class IncrementButtonsLayout extends RelativeLayout {

    private final ImageView[] fourIncrementButtons = new ImageView[4];
    private View[] incrementViewArray = new View[7];
    private Controls controls;
    private long _taskLength;

    /**
     * @param activity     The activity that contains the fourIncrementButtons
     * @param _controls    the Controls object in the controls package
     * @param _buttonArray MIN_UP, MIN_DOWN, SEC_UP, SEC_DOWN, MIN_TEXT, SEC_TEXT, THIS_LAYOUT
     */
    public IncrementButtonsLayout(Activity activity, Controls _controls, View[] _buttonArray) {
        super(activity);
        this.incrementViewArray = _buttonArray;
        this.controls = _controls;
    }

    public IncrementButtonsLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IncrementButtonsLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    private void setIncrementViewArray(View[] incrementViewArray) {
        this.incrementViewArray = incrementViewArray;
        fourIncrementButtons[MIN_UP] = (ImageView) incrementViewArray[MIN_UP];
        fourIncrementButtons[MIN_DOWN] = (ImageView) incrementViewArray[MIN_DOWN];
        fourIncrementButtons[SEC_UP] = (ImageView) incrementViewArray[SEC_UP];
        fourIncrementButtons[SEC_DOWN] = (ImageView) incrementViewArray[SEC_DOWN];
    }


    public void update(long _taskLengthInMillis) {
        //it's the buttons, so task length is equal to time remaining.
        this._taskLength = _taskLengthInMillis;
    }

    public void initIncrementButtons(Controls _controls, View[] _incrementViewArray, long _taskLength) {
        this.controls = _controls;
        setIncrementViewArray(_incrementViewArray); //sets incrementViewArray AND fourIncrementButtons
        this._taskLength = _taskLength; //update the current taskLength so the fourIncrementButtons can return the NEW taskLength
    }


    private void showIncrementButtons() {
        ImageView watch = controls.getComponents().getVisualComponents().getWatchComponents().getTappingArea();
        if (isViewOverlapping(this, watch)) {
            Toast.makeText(getContext(), "no room for buttons", Toast.LENGTH_SHORT).show();
            hideIncrementButtons();
        } else {
            for (View _button : incrementViewArray) {
                _button.setVisibility(View.VISIBLE);
            }
        }
    }

    private boolean isViewOverlapping(View firstView, View secondView) {
        int[] firstPosition = new int[2];
        int[] secondPosition = new int[2];

        firstView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        firstView.getLocationOnScreen(firstPosition);
        secondView.getLocationOnScreen(secondPosition);

        int r = firstView.getMeasuredWidth() + firstPosition[0];
        int l = secondPosition[0];
        return r >= l && (r != 0 && l != 0);
    }


    private void hideIncrementButtons() {
        for (View _view : incrementViewArray) {
            _view.setVisibility(View.INVISIBLE);
        }
    }

    private void setupButtonListeners() {

        RepeatListener _repeatListener = new RepeatListener(500, 50, _view -> {

            int buttonNumber = getButtonNumberByView((ImageView) _view);
            _taskLength = incrementAppropriately(buttonNumber);
            //Log.i(TAG, "method setupButtonListeners @124: taskLength:" + _taskLength);
            controls.notifyComponentsOfChange(_taskLength, ViewList.ControlComponentNumber.INCREMENT_BUTTON_ARRAY, true);
        });
        attachListenerToButtons(_repeatListener);
    }

    private void attachListenerToButtons(RepeatListener repeatListener) {
        for (View _button : fourIncrementButtons) {
            _button.setOnTouchListener(repeatListener);
        }
    }

    private int getButtonNumberByView(ImageView view) {
        int unknownViewsId = view.getId();
        for (int buttonNumber = 0; buttonNumber < fourIncrementButtons.length; buttonNumber++) {
            if (fourIncrementButtons[buttonNumber].getId() == unknownViewsId) {
                return buttonNumber;
            }
        }
        //Log.i(TAG, "method getButtonNumberByView @171: buttonView not found");
        return 9999;
    }

    private long incrementAppropriately(int buttonNumber) {
        final int HELD_BUTTON_SPEED_FACTOR = 1;
        final int granularity = getResources().getInteger(R.integer.granularity);
        long newSetTime;
        switch (buttonNumber) {
            case MIN_UP:
                newSetTime = incrementTo((int) (_taskLength + 60 * 1000 * HELD_BUTTON_SPEED_FACTOR));
                break;
            case ViewList.IncrementButtonNumber.MIN_DOWN:
                newSetTime = incrementTo((int) (_taskLength - 60 * 1000 * HELD_BUTTON_SPEED_FACTOR));
                break;
            case ViewList.IncrementButtonNumber.SEC_UP:
                newSetTime = incrementTo((int) (_taskLength + granularity * 1000 * HELD_BUTTON_SPEED_FACTOR));
                break;
            case ViewList.IncrementButtonNumber.SEC_DOWN:
                newSetTime = incrementTo((int) (_taskLength - granularity * 1000 * HELD_BUTTON_SPEED_FACTOR));
                break;
            default:
                try {
                    throw new IndexOutOfBoundsException();
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
                newSetTime = 0;
        }
        return clip(newSetTime);
    }

    private long clip(long input) {
        if (input > TIMER_MAX) {
            return TIMER_MAX;
        } else if (input < 0) {
            return 0;
        } else {
            return input;
        }
    }

    //called by other methods such as minUp secDown, etc.
    //handles the changes caused by pressing the increment fourIncrementButtons
    private long incrementTo(long _newTime) {
        //Log.i(TAG, "method incrementTo @ 168: " + _newTime + " = _newTime");
        return _newTime;
    }

    public void stopListening() {
        attachListenerToButtons(null);
        hideIncrementButtons();
    }

    public void startListening() {
        setupButtonListeners();
        showIncrementButtons();
    }
}
