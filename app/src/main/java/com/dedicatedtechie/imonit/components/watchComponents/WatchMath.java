package com.dedicatedtechie.imonit.components.watchComponents;

import android.graphics.Point;
import android.view.MotionEvent;

class WatchMath {
    /**
     * Calculates the angle from centerPt to targetPt in degrees.
     * The return should range from [0,360), rotating CLOCKWISE,
     * 0 and 360 degrees represents NORTH,
     * 90 degrees represents EAST, etc...
     *
     * @param centerPt Point we are rotating around.
     * @param targetPt Point we want to calculate the angle to.
     * @return angle in degrees.  This is the angle from centerPt to targetPt.
     */
    private static double getAngle(Point centerPt, Point targetPt) {
        // calculate the angle theta from the deltaY and deltaX values
        // (Math.atan2() returns radians values from [-PI,PI])
        // 0 currently points EAST.
        // NOTE: By preserving Y and X param order to Math.atan2(),  we are expecting
        // a CLOCKWISE angle direction.
        double theta = Math.atan2(centerPt.y - targetPt.y, targetPt.x - centerPt.x);
        //Log.i(TAG, "method getAngle @50: tap at " + targetPt.x + ", " + targetPt.y);
        // convert from radians to degrees
        // this will give you an angle from [0->270],[-180,0]
        double angle = Math.toDegrees(theta);

        //turn results around to clockwise
        angle *= -1;

        //bring 0 to the top
        angle += 90;

        // convert to positive range [0-360)
        // since we want to prevent negative angles, adjust them now.
        // we can assume that atan2 will not return a negative value
        // greater than one partial rotation
        if (angle < 0) {
            angle += 360;
        }

        return angle;
    }

    ///Converts angle of 360 degrees into a percent of TIMER_MAX
    private long taskLengthFromAngle(long angle) {
        //convert to float for division
        float floatAngle = (float) angle;
        float floatGranularity = (float) com.dedicatedtechie.imonit.components.Components.GRANULARITY;

        //change the degree-angle into "percentage of the way around the circle"
        float percent = floatAngle / 360;
        //Log.i(TAG, "WatchComponents.taskLengthFromAngle (line 160): " + new DecimalFormat("#").format(percent * 100) + "%");
        //change "percent of maximum" into time in millis
        float millis = percent * (long) com.dedicatedtechie.imonit.components.Components.TIMER_MAX;

        //convert millis into chunks of "15-second intervals" (or whatever granularity is set to)
        int chunks = Math.round((millis / 1000) / floatGranularity);

        //now convert back to millis, so we can submit the task length in evenly rounded int(long) millis
        return Math.round(chunks * com.dedicatedtechie.imonit.components.Components.GRANULARITY * 1000);
    }

    private Point pointFromTouchEvent(MotionEvent event) {
        //tap was at touchX, touchY
        long touchX = (long) event.getX();
        long touchY = (long) event.getY();
        return new Point((int) touchX, (int) touchY);
    }

    long taskLengthFromEvent(MotionEvent event, Point centerPt) {

        Point targetPt = new WatchMath().pointFromTouchEvent(event);

        //get the angle from the center to the tap
        long angle = (long) getAngle(centerPt, targetPt);

        //Convert that angle into TIME
        return new WatchMath().taskLengthFromAngle(angle);
    }
}
