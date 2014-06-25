package edu.arizona.adherence;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.ubhave.datahandler.loggertypes.AbstractDataLogger;
import com.ubhave.sensormanager.ESException;
import com.ubhave.sensormanager.ESSensorManager;

/**
 *  This service collect data from sensors and save locally and remotely
 */
public class SensingService extends Service {
    private static final int ONGOING_NOTIFICATION_ID = 192;
    private IBinder mBinder;
    private boolean mAllowRebind;
    private ESSensorManager sensorManager;
    private SensingListener sensor;

    public SensingService() {
    }

    @Override
    public void onCreate() {
        // The service is being created
        try {
            System.out.println("ESSensor Service");
            sensorManager = ESSensorManager.getSensorManager(this);
        } catch (ESException e) {
            e.printStackTrace();
        }

        String userId = getSharedPreferences(ProfileActivity.PREFS_NAME, 0).getString(ProfileActivity.NAME, "unknown");

        //AbstractDataLogger dataLogger = new SensingAsyncTransferLogger(this, userId, getDeviceId());
        AbstractDataLogger dataLogger = new SensingAsyncTransferLogger(this);

        sensor = new SensingListener(this, dataLogger);
    }

    private String getDeviceId() {
        TelephonyManager mngr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        return mngr.getDeviceId();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // The service is starting, due to a call to startService()
        sensor.startSensing();
        Toast toast = Toast.makeText(this, "Sensing Service Started", Toast.LENGTH_LONG);
        toast.show();

        // Put service in foreground
        Notification notification = new Notification(
                R.drawable.treatment,
                getText(R.string.service_notification),
                System.currentTimeMillis());
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notification.setLatestEventInfo(this, getText(R.string.notification_title),
                getText(R.string.notification_message), pendingIntent);
        startForeground(ONGOING_NOTIFICATION_ID, notification);

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // A client is binding to the service with bindService()
        sensor.startSensing();
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // All clients have unbound with unbindService()
        sensor.stopSensing();
        return mAllowRebind;
    }

    @Override
    public void onRebind(Intent intent) {
        // A client is binding to the service with bindService(),
        // after onUnbind() has already been called
    }

    @Override
    public void onDestroy() {
        // The service is no longer used and is being destroyed
        sensor.stopSensing();
        stopForeground(true);
        Toast toast = Toast.makeText(this, "Sensing Service Stopped", Toast.LENGTH_LONG);
        toast.show();
    }
}
