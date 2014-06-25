package edu.arizona.adherence;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.ubhave.datahandler.loggertypes.AbstractDataLogger;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends Activity {

    private static final String TAG_LABEL = "Label";
    private static final String TAG_PROFILE = "Profile";

    private static final String TAG_ACTIVITY = "activity";
    private static final String TAG_EVENT = "event";
    private static final String TAG_ACTIVITY_START = "start";
    private static final String TAG_ACTIVITY_STOP = "stop";
    private static final String TAG_TIMESTAMP = "timestamp";
    private static final String TAG_LOCAL_TIME = "localTime";

    private static final String TAG_WALKING = "Walking";
    private static final String TAG_RUNNING = "Running";
    private static final String TAG_UPSTAIRS = "Walking Upstairs";
    private static final String TAG_DOWNSTAIRS = "Walking Downstairs";
    private static final String TAG_SITTING = "Sitting";
    private static final String TAG_STANDING = "Standing";
    private static final String TAG_LAYING = "Laying";
    private static final String TAG_BIKING = "Biking";

    private SharedPreferences mSettings;
    private Button activityButton;
    private Spinner mActivities;
    private TextView mCountDown;
    private TextView mRemaining;

    private AbstractDataLogger dataLogger;

    // Is performing any physical activity
    private boolean isPerforming = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSettings = getSharedPreferences(ProfileActivity.PREFS_NAME, 0);
        activityButton = (Button) findViewById(R.id.activity_button);
        activityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startClick();
            }
        });

        mCountDown = (TextView) findViewById(R.id.countdown);
        mRemaining = (TextView) findViewById(R.id.txtRemaining);

        mActivities = (Spinner) findViewById(R.id.spnActivity);

        CustomAdapter adapter = new CustomAdapter(this, R.layout.spinner_rows, getActivityData());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mActivities.setAdapter(adapter);

        mActivities.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ImageText it = (ImageText) mActivities.getSelectedItem();
                int remaining = mSettings.getInt(getResources().getString(it.getDescription()),5);
                mRemaining.setText(String.valueOf(remaining));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        mActivities.setSelection(0);

        dataLogger = new SensingAsyncTransferLogger(this);

        boolean newuser = getIntent().getBooleanExtra(ProfileActivity.PROFILE_NEWUSER, true);
        if (newuser)
            saveProfile();
    }

    private void startClick() {

        activityButton.setEnabled(false);
        mActivities.setEnabled(false);
        isPerforming = true;
        manageService(true);

        new CountDownTimer(64 * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                if (seconds > 60) {
                    if (seconds == 61) {
                        mCountDown.setText("Go!");
                        logEvent(TAG_ACTIVITY_START);
                    } else
                        mCountDown.setText(String.valueOf(seconds - 61));
                } else {
                    if (seconds == 12)
                        logEvent(TAG_ACTIVITY_START);
                    mCountDown.setText(seconds + " seconds");
                }
            }

            public void onFinish() {
                mCountDown.setText("Done!");
                logEvent(TAG_ACTIVITY_STOP);

                activityButton.setEnabled(true);
                mActivities.setEnabled(true);

                ImageText it = (ImageText) mActivities.getSelectedItem();
                String activity = getResources().getString(it.getDescription());

                int remaining = mSettings.getInt(activity, 5);
                if (remaining != 0) {
                    SharedPreferences.Editor editor = mSettings.edit();
                    editor.putInt(activity, remaining - 1);
                    editor.commit();

                    mRemaining.setText(String.valueOf(remaining-1));
                }
                isPerforming = false;
            }
        }.start();
    }

    private void logEvent(String event) {
        JSONObject json = new JSONObject();
        try {
            int activityId = ((ImageText) mActivities.getSelectedItem()).getDescription();
            json.put(TAG_ACTIVITY, getResources().getString(activityId));
            json.put(TAG_EVENT, event);
            json.put(TAG_TIMESTAMP, System.currentTimeMillis());
            json.put(TAG_LOCAL_TIME, localTime());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        dataLogger.logExtra(TAG_LABEL, json);
    }

    private ArrayList<ImageText> getActivityData() {
        ArrayList<ImageText> values = new ArrayList<ImageText>();

        values.add(new ImageText(R.string.walking, R.drawable.walking));
        values.add(new ImageText(R.string.running, R.drawable.running));
        values.add(new ImageText(R.string.upstairs, R.drawable.upstairs));
        values.add(new ImageText(R.string.downstairs, R.drawable.downstairs));
        values.add(new ImageText(R.string.sitting, R.drawable.sitting));
        values.add(new ImageText(R.string.standing, R.drawable.standing));
        values.add(new ImageText(R.string.laying, R.drawable.laying));
        values.add(new ImageText(R.string.biking, R.drawable.biking));

        return values;
    }

    private void saveProfile() {
        String name = mSettings.getString(ProfileActivity.NAME, "unknown" + System.currentTimeMillis());
        int age = mSettings.getInt(ProfileActivity.AGE, 0);
        String gender = mSettings.getString(ProfileActivity.GENDER, "male");
        float height = mSettings.getFloat(ProfileActivity.HEIGHT, 0.0f);
        float weight = mSettings.getFloat(ProfileActivity.WEIGHT, 0.0f);

        JSONObject json = new JSONObject();
        try {
            json.put(ProfileActivity.NAME, name);
            json.put(ProfileActivity.AGE, age);
            json.put(ProfileActivity.GENDER, gender);
            json.put(ProfileActivity.HEIGHT, height);
            json.put(ProfileActivity.WEIGHT, weight);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        dataLogger.logExtra(TAG_PROFILE, json);

        // Activities statistics
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putInt(TAG_WALKING, 5);
        editor.putInt(TAG_RUNNING, 5);
        editor.putInt(TAG_UPSTAIRS, 5);
        editor.putInt(TAG_DOWNSTAIRS, 5);
        editor.putInt(TAG_SITTING, 5);
        editor.putInt(TAG_STANDING, 5);
        editor.putInt(TAG_LAYING, 5);
        editor.putInt(TAG_BIKING, 5);
        editor.apply();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        if (!isPerforming) {
            // Stop the service to collect the data
            manageService(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Start the service to collect the data
        //manageService(true);
    }

    private void manageService(boolean start) {
        Intent serviceIntent = new Intent(this, SensingService.class);
        if (start)
            startService(serviceIntent);
        else
            stopService(serviceIntent);
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        manageService(false);
    }

    @SuppressLint("SimpleDateFormat")
    private String localTime()
    {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss zZ");
        return dateFormat.format(calendar.getTime());
    }
}
