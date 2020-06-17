package com.dedicatedtechie.imonit;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.dedicatedtechie.imonit.components.Components;
import com.dedicatedtechie.imonit.components.ViewList;
import com.dedicatedtechie.imonit.components.controls.Controls;
import com.dedicatedtechie.imonit.components.timers.TaskTimer;
import com.dedicatedtechie.imonit.settings.NotificationSettings;
import com.dedicatedtechie.imonit.settings.SettingsObject;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static com.dedicatedtechie.imonit.components.ViewList.ControlComponentNumber.COUNT_UP_DOWN_SWITCH_ARRAY;
import static com.dedicatedtechie.imonit.components.ViewList.ControlComponentNumber.INCREMENT_BUTTON_ARRAY;
import static com.dedicatedtechie.imonit.components.ViewList.ControlComponentNumber.SEEK_BAR_ARRAY;
import static com.dedicatedtechie.imonit.components.ViewList.ControlComponentNumber.START_STOP_BUTTON_ARRAY;
import static com.dedicatedtechie.imonit.components.ViewList.ControlComponentNumber.SYSTEM;
import static com.dedicatedtechie.imonit.components.ViewList.ControlComponentNumber.TIMER_TICK_AS_CONTROL;
import static com.dedicatedtechie.imonit.components.ViewList.ControlComponentNumber.WATCH_TAP_AS_CONTROL;
import static com.dedicatedtechie.imonit.components.ViewList.IncrementButtonNumber.INC_LAYOUT;
import static com.dedicatedtechie.imonit.components.timers.TimerFunctions.TIMER_UP;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "DeTe";

    private Components components;
    private SettingsObject settingsObject;


    public Components getComponents() {
        return components;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            onResume();
            Log.i(TAG, "MainActivity.onCreate@53: resuming successfully");
        } catch (Exception e) {
            Log.i(TAG, "MainActivity.onCreate@55: can't resume");
            e.printStackTrace();
            // this comment was added to test GitHub uploading
        }
        //setContentView(R.layout.activity_main_dark);
        //SharedPreferences are used to store settings during orientation changes or activity changes.


        //the settingsObject is where settings, options, and statuses are kept.
        try {
            if (settingsObject != null) {
                settingsObject.retrieveSharedPreferences();
                Log.i(TAG, "MainActivity.onCreate@59: settings object exists");
            } else {
                Log.i(TAG, "MainActivity.onCreate@61: settingsObject was null");
                settingsObject = new SettingsObject(MainActivity.this);
                //Log.i(TAG, "MainActivity.onCreate@52: retrieving SP.");
            }
        } catch (Exception e) {
            Log.i(TAG, "MainActivity.onCreate@66: failed");
            e.printStackTrace();
        }
        settingsObject.retrieveSharedPreferences();
        //Log.i(TAG, "MainActivity.onCreate@52: retrieved SP");

        if (settingsObject.isDarkModeWanted()) {
            setContentView(R.layout.activity_main_dark);
        } else {
            setContentView(R.layout.activity_main_light);
        }

        //the Components object controls all the interfaces and visualizations so they can be updated.
        components = new Components(this, settingsObject);

        //the ViewList object stores all of the views.  It is initialized here
        ViewList viewlist = makeViewList();
        //Log.i(TAG, "MainActivity.onCreate@51: viewList has been created");

        components.sendViewsToComponents(this, viewlist);

        //Set the titleBar.
        setTitle(this.getString(R.string.ImOnIt) + getString(R.string.abbreviation_for_Level) + (settingsObject.getLevel()));

        TaskTimer _taskTimer = components.getTaskTimer(this, settingsObject);

        //Make sure the switch points the right way
        SwitchCompat countUpDownSwitch = components.getControls().getCountUpDownSwitch();
        if (settingsObject.timerCounts(TIMER_UP)) {
            countUpDownSwitch.setChecked(true);
        } else {//counting down
            countUpDownSwitch.setChecked(false);
        }


        //if we had been going before, keep counting
        if (settingsObject.isCounting()) {
            _taskTimer.startTimer(settingsObject.getLevel());
        }

        components.initialize(_taskTimer);
    }

    public boolean makeNotification(int type, Notification notification) {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationSettings notificationSettings = components.getNotificationSettings();
        int notificationId = (int) System.currentTimeMillis();
        if (notificationManager != null) {
            notificationManager.notify(notificationId, notification);
            return true;
        } else return false;
    }

    private ViewList makeViewList() {
        //the ViewList object stores all of the views.  It is initialized here
        View[][] _controlViews = findControlViews();
        //Log.i(TAG, "MainActivity.makeViewList@90: ");
        return new ViewList(_controlViews, findVisualViews(), findWatchComponentViews());
    }

    private View[] findWatchComponentViews() {
        View[] _viewArray = new View[3];
        _viewArray[ViewList.WatchComponentNumber.WATCH] = this.findViewById(R.id.watchImageView);
        _viewArray[ViewList.WatchComponentNumber.HAND] = this.findViewById(R.id.hand);
        _viewArray[ViewList.WatchComponentNumber.FACE] = this.findViewById(R.id.face);
        return _viewArray;
    }

    private View[] findVisualViews() {
        View[] _viewArray = new View[4];
        _viewArray[ViewList.VisualComponentNumber.DIGITAL_READOUT] = this.findViewById(R.id.readoutTextView);
        _viewArray[ViewList.VisualComponentNumber.PROGRESS_BAR] = this.findViewById(R.id.progressBar);
        return _viewArray;
    }

    private View[][] findControlViews() {
        View[] _incrementButtonViewArray = findIncrementViews();
        //Log.i(TAG, MainActivity.class.getSimpleName() + "> findControlViews() @82: " + _incrementButtonViewArray[6]);
        View[] _startStopButtonViewArray = new View[]{findViewById(R.id.startStopButton)};
        View[] _upDownSwitchViewArray = new View[]{findViewById(R.id.countUpOrDownSwitch)};
        View[] _seekBarViewArray = new View[]{findViewById(R.id.timerSeekBar)};

        View[][] controlViews = new View[7][];
        controlViews[SYSTEM] = null;
        controlViews[INCREMENT_BUTTON_ARRAY] = _incrementButtonViewArray;
        controlViews[START_STOP_BUTTON_ARRAY] = _startStopButtonViewArray;
        controlViews[COUNT_UP_DOWN_SWITCH_ARRAY] = _upDownSwitchViewArray;
        controlViews[SEEK_BAR_ARRAY] = _seekBarViewArray;
        controlViews[WATCH_TAP_AS_CONTROL] = null;
        controlViews[TIMER_TICK_AS_CONTROL] = null;

        return controlViews;
    }

    private View[] findIncrementViews() {
        View[] _viewArray = new View[7];
        _viewArray[ViewList.IncrementButtonNumber.MIN_UP] = this.findViewById(R.id.minUpButton);
        _viewArray[ViewList.IncrementButtonNumber.MIN_DOWN] = this.findViewById(R.id.minDownButton);
        _viewArray[ViewList.IncrementButtonNumber.SEC_UP] = this.findViewById(R.id.secUpButton);
        _viewArray[ViewList.IncrementButtonNumber.SEC_DOWN] = this.findViewById(R.id.secDownButton);
        _viewArray[ViewList.IncrementButtonNumber.MIN_TEXT] = this.findViewById(R.id.minButtonTextView);
        _viewArray[ViewList.IncrementButtonNumber.SEC_TEXT] = this.findViewById(R.id.secButtonTextView);
        _viewArray[INC_LAYOUT] = this.findViewById(R.id.increment_buttons);
        //Log.i(TAG, "findIncrementViews@97: found buttonLayout");
        return _viewArray;
    }

    ///called when user clicks the start/stop button. If it's stopped, start it, otherwise, stop it.
    public void clickStartStop(@SuppressWarnings("unused") View view) {
        Controls _controls = components.getControls();
        _controls.startStopClicked(this, view);
    }

    ///the user just clicked the "countUp vs countDown" switch.
    public void countUpDownSwitchClicked(View directionSwitch) {
        //Log.i(TAG, "MainActivity.countUpDownSwitchClicked@157: SWITCH!!!");
        Controls _controls = components.getControls();
        _controls.countUpDownSwitchClicked(this, directionSwitch);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            settingsObject.retrieveSharedPreferences();
            Log.i(TAG, "MainActivity.onRestoreInstanceState@199: settingsObject exists");
        } catch (Exception e) {
            Log.i(TAG, "MainActivity.onResume@201: failed");
            e.printStackTrace();
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        try {
            settingsObject.retrieveSharedPreferences();
            Log.i(TAG, "MainActivity.onRestoreInstanceState@211: settingsObject exists");
        } catch (Exception e) {
            Log.i(TAG, "MainActivity.onRestoreInstanceState@213: failed");
            e.printStackTrace();
        }
    }

    public ConstraintLayout getMainLayout(){
        try {
            return findViewById(R.id.mainLayout);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        //Log.i(TAG, "MainActivity.onSaveInstanceState@183: storing SP");
        saveEverything();
        super.onSaveInstanceState(outState, outPersistentState);
    }

    /**
     * Create a SETTINGS button in the titleBar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * called when user clicks on a menu item.
     *
     * @param item The item in the options menu that the user Clicked on
     * @return true/false -- has event been handled.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        boolean isCounting = settingsObject.isCounting(); //get the status
        TaskTimer timer = components.getTaskTimer(this, settingsObject);
        Intent menuIntent;

        //Log.i(TAG, "method onOptionsItemSelected @159: a menu item was Clicked");

        if (item.getItemId() == R.id.settingsFragment) {//settings item selected. (the only menu item)
            menuIntent = new Intent(MainActivity.this, SettingsActivity.class);
        } else {//(item.getItemId() == R.id.instructions)
            menuIntent = new Intent(MainActivity.this, Instructions.class);
        }
        pauseAfterMenuItemSelected(isCounting, timer);
        startActivity(menuIntent);

        return super.onOptionsItemSelected(item);
    }


    private void pauseAfterMenuItemSelected(boolean isCounting, TaskTimer timer) {
        if (isCounting) {
            Toast.makeText(MainActivity.this, "Timer is paused on settings menu", Toast.LENGTH_SHORT).show();
            timer.stopTimer(); //stop the timer
            settingsObject.updateSharedPreferences();//save our status and go to the settings screen
        }
    }

    @Override
    protected void onPause() {
        saveEverything();
        super.onPause();
    }

    private void saveEverything() {



       /* //stop the timer, but keep track of whether or not it was counting.
        boolean timerStatus = settingsObject.isCounting();
        long taskLength = settingsObject.getTaskLengthInMillis();
        long currentTime = settingsObject.getCurrentTimeInMillis();
*/

/*        TaskTimer _taskTimer = components.getTaskTimer(this, settingsObject);
        if (_taskTimer != null)
            _taskTimer.stopTimer();
        components.resetTaskTimerToZero(SYSTEM);*/

      /*  settingsObject.setTimerTo(taskLength);
        settingsObject.setCurrentTimeInMillis(currentTime);
        settingsObject.setIsCounting(timerStatus);*/

        //save that status
        settingsObject.updateSharedPreferences();
    }

    public void dismissAlarm(View dismissButton) {
        FloatingActionButton startStopFab = components.getControls().getStartStopButton();
     components.getNotificationSettings().dismissAlarm(dismissButton,startStopFab);
    }
}
