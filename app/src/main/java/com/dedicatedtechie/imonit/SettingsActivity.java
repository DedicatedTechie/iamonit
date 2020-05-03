package com.dedicatedtechie.imonit;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import com.dedicatedtechie.imonit.settings.SettingsObject;

import static com.dedicatedtechie.imonit.components.timers.TimerFunctions.TIMER_UP;


public class SettingsActivity extends AppCompatActivity {

    private SettingsObject settingsObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        SettingsFragment fragment = new SettingsFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settingsFragment, fragment)
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        settingsObject = new SettingsObject(this);
        String levelSettingKey = getResources().getString(R.string.level_setting);
        //Preference levelListPreference = fragment.findPreference(levelSettingKey);
         }


    @SuppressWarnings("unused")
    public void emailDeveloper(View view) {
        String[] address = new String[1];
        address[0] = "info@dedicatedtechie.com";
        String subject = "App Feedback for I'm On It";
        String body = bodyEmailString();

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, address);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, body);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private String appVersionNumber() {
        String version;
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            version = null;
        }
        return version;
    }

    @SuppressWarnings("HardcodedLineSeparator")
    private String bodyEmailString() {
        String body;
        body = "\n\n" +
                " If you do not wish to share your app settings with the developer, delete the lines below:" +
                "\n-=-=-=-=-=-=-=-=-=-=-=-\n" +
                "Android version: " + Build.VERSION.SDK_INT + ": " + Build.VERSION.INCREMENTAL + ": " + Build.VERSION.CODENAME + "\n" +
                "App version: " + appVersionNumber() + "\n" +
                "App Settings: " + "\n" +
                "progressBarWanted: " + settingsObject.progressBarIsWanted() + "\n" +
                "vibrateOnReminder: " + settingsObject.vibratesOnReminder() + "\n" +
                "vibrateOnFinish: " + settingsObject.vibratesOnFinish() + "\n" +
                "completeSoundWanted: " + settingsObject.completeSoundIsWanted() + "\n" +
                "reminderSoundWanted: " + settingsObject.reminderSoundIsWanted() + "\n" +
                "timerCountsUp: " + settingsObject.timerCounts(TIMER_UP) + "\n" +
                "timerSetTo: " + settingsObject.getTaskLengthInMillis() + "\n" +
                "isCounting: " + settingsObject.isCounting() + "\n" +
                "currentTime: " + settingsObject.getCurrentTimeInMillis() + "\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-\n\n";
        return body;
    }


    @SuppressWarnings("unused")
    public void rateTheApp(View rateAppButton) {
        //Log.i(TAG, "SettingsActivity2.rateTheApp@249: loading Android store");
        Uri marketUri = Uri.parse("market://details?id=" + getPackageName());
        startActivity(new Intent().setAction(Intent.ACTION_VIEW).setData(marketUri));
    }


    //provides behavior for the physical "back" button on the device
    @Override
    public void onBackPressed() {
        Intent backIntent = new Intent(this, MainActivity.class);
        settingsObject.updateSharedPreferences();
        startActivity(backIntent);
    }

    //provides behavior for the "back" button in the actionbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Respond to the action bar's Up/Home button
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("unused")
    public void goToNotificationSettings(View view) {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, this.getPackageName());
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", this.getPackageName());
            intent.putExtra("app_uid", this.getApplicationInfo().uid);
        } else {
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setData(Uri.parse("package:" + this.getPackageName()));
        }
        this.startActivity(intent);
    }


    @SuppressWarnings("WeakerAccess")
    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            addNotificationsPreferenceToFragment();
        }

        private void addNotificationsPreferenceToFragment() {
            PreferenceScreen screen = this.getPreferenceScreen();

            PreferenceCategory notificationsPreferenceCategory = new PreferenceCategory(screen.getContext());
            notificationsPreferenceCategory.setKey("notificationsPrefCategory");
            notificationsPreferenceCategory.setTitle("Notifications");
            notificationsPreferenceCategory.setIcon(R.drawable.ic_notifications);


            Preference preferenceButton = new Preference(screen.getContext());
            preferenceButton.setKey("buttonForSettings");
            preferenceButton.setTitle("Notification Settings");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                preferenceButton.setSummary("Your Phone supports custom sounds by notification type.  Click here to set custom ring-tones for reminder and completion notifications (On the next screen, tap on the notification name to make changes).");
            } else {//api < 26
                preferenceButton.setSummary("Click here for additional notification settings");
            }
            preferenceButton.setIntent(getNotifySettingsIntent(getContext()));
            screen.addPreference(notificationsPreferenceCategory);
            notificationsPreferenceCategory.addPreference(preferenceButton);
        }

        private Intent getNotifySettingsIntent(Context context) {
            Intent intent = new Intent();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                intent.putExtra("app_package", context.getPackageName());
                intent.putExtra("app_uid", context.getApplicationInfo().uid);
            } else {
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
            }
            return intent;
        }
    }
}