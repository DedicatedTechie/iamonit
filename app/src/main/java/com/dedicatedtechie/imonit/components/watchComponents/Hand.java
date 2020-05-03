package com.dedicatedtechie.imonit.components.watchComponents;

import android.content.Context;
import android.util.AttributeSet;

import com.dedicatedtechie.imonit.R;

public class Hand extends androidx.appcompat.widget.AppCompatImageView {

    public Hand(Context context) {
        super(context);
    }

    public Hand(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Hand(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void rotate(long millisUntilFinished) {
        final int granularity = getResources().getInteger(R.integer.granularity);
        float currentSteps = (float) Math.ceil((float)millisUntilFinished / 1000f / (float) granularity);
        int timerMax = getResources().getInteger(R.integer.timerMaxInMils);
       final float maxSteps = (float) Math.ceil((float)timerMax / 1000f / (float) granularity);
        float percent = currentSteps / maxSteps;
        float angle = (360 * percent - 90);
        //the "-90" is because the system expects 0-degrees to point to the right of the screen, not up.

        //turn the needle to match the time.
        this.animate().setDuration(0).rotation(angle).start();
    }
}
