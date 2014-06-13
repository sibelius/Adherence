package edu.arizona.adherence;

import android.content.Context;
import android.util.Log;
import android.view.textservice.SentenceSuggestionsInfo;

import com.ubhave.dataformatter.DataFormatter;
import com.ubhave.datahandler.config.DataStorageConfig;
import com.ubhave.datahandler.loggertypes.AbstractDataLogger;
import com.ubhave.sensormanager.ESException;
import com.ubhave.sensormanager.ESSensorManager;
import com.ubhave.sensormanager.SensorDataListener;
import com.ubhave.sensormanager.config.sensors.pull.CameraConfig;
import com.ubhave.sensormanager.config.sensors.pull.LocationConfig;
import com.ubhave.sensormanager.config.sensors.pull.MicrophoneConfig;
import com.ubhave.sensormanager.config.sensors.pull.MotionSensorConfig;
import com.ubhave.sensormanager.config.sensors.pull.PullSensorConfig;
import com.ubhave.sensormanager.data.SensorData;
import com.ubhave.sensormanager.sensors.SensorInterface;
import com.ubhave.sensormanager.sensors.SensorUtils;
import com.ubhave.sensormanager.sensors.pull.PullSensor;

import java.util.ArrayList;

/**
 * Created by sibelius on 6/1/14.
 */
public class SensingListener implements SensorDataListener {

    private ESSensorManager sensorManager;
    private AbstractDataLogger dataLogger;
    private Context context;
    private ArrayList<Integer> mSubscriptions;
/*
    private int[] MONITORED_SENSORS = new int[] {
            SensorUtils.SENSOR_TYPE_ACCELEROMETER,
            SensorUtils.SENSOR_TYPE_LOCATION,
            SensorUtils.SENSOR_TYPE_MICROPHONE,
            SensorUtils.SENSOR_TYPE_BATTERY,
            SensorUtils.SENSOR_TYPE_PHONE_STATE,
            SensorUtils.SENSOR_TYPE_PROXIMITY,
            SensorUtils.SENSOR_TYPE_SCREEN,
            SensorUtils.SENSOR_TYPE_SMS,
            SensorUtils.SENSOR_TYPE_CONNECTION_STATE,
            SensorUtils.SENSOR_TYPE_APPLICATION,
            SensorUtils.SENSOR_TYPE_SMS_CONTENT_READER,
            SensorUtils.SENSOR_TYPE_CALL_CONTENT_READER,
            SensorUtils.SENSOR_TYPE_ZEPHYR,
            SensorUtils.SENSOR_TYPE_LIGHT,
            SensorUtils.SENSOR_TYPE_GYROSCOPE,
            SensorUtils.SENSOR_TYPE_GRAVITY};
            //SensorUtils.SENSOR_TYPE_CAMERA};*/

    private int[] MONITORED_SENSORS = new int[] {
            SensorUtils.SENSOR_TYPE_ACCELEROMETER,
            SensorUtils.SENSOR_TYPE_GYROSCOPE,
            SensorUtils.SENSOR_TYPE_GRAVITY,
            SensorUtils.SENSOR_TYPE_LINEAR_ACCELERATION,

            SensorUtils.SENSOR_TYPE_ZEPHYR,

            SensorUtils.SENSOR_TYPE_SCREEN,
            SensorUtils.SENSOR_TYPE_BATTERY,
            SensorUtils.SENSOR_TYPE_LIGHT,
            SensorUtils.SENSOR_TYPE_LOCATION,
            SensorUtils.SENSOR_TYPE_MICROPHONE
    };

    public SensingListener(final Context context, final AbstractDataLogger logger) {
        this.context = context;
        this.dataLogger = logger;
        try {
            sensorManager = ESSensorManager.getSensorManager(context);
            setSensorConfig();
        } catch(ESException e) {
            sensorManager = null;
            e.printStackTrace();
        }
    }

    private void setSensorConfig() {
        if (sensorManager != null) {
            try {
                // 50 HZ
                // Sleep for only 1 second, and get more data
                long sleepMotion = 1000L;

                /*
                 * Set Motion sensing params
                 */
                sensorManager.setSensorConfig(SensorUtils.SENSOR_TYPE_ACCELEROMETER,
                        PullSensorConfig.POST_SENSE_SLEEP_LENGTH_MILLIS, sleepMotion);

                sensorManager.setSensorConfig(SensorUtils.SENSOR_TYPE_GYROSCOPE,
                        PullSensorConfig.POST_SENSE_SLEEP_LENGTH_MILLIS, sleepMotion);

                sensorManager.setSensorConfig(SensorUtils.SENSOR_TYPE_GRAVITY,
                        PullSensorConfig.POST_SENSE_SLEEP_LENGTH_MILLIS, sleepMotion);

                sensorManager.setSensorConfig(SensorUtils.SENSOR_TYPE_LINEAR_ACCELERATION,
                        PullSensorConfig.POST_SENSE_SLEEP_LENGTH_MILLIS, sleepMotion);

                // Sleep for 1 minute and gather more data
                long locationSleep = 1 * 60 * 1000L;
                /*
                 * Set location sensing params
                 */
                sensorManager.setSensorConfig(SensorUtils.SENSOR_TYPE_LOCATION,
                        LocationConfig.ACCURACY_TYPE, LocationConfig.LOCATION_ACCURACY_FINE);
                sensorManager.setSensorConfig(SensorUtils.SENSOR_TYPE_LOCATION,
                        PullSensorConfig.POST_SENSE_SLEEP_LENGTH_MILLIS, locationSleep);


                /*
                 * Set microphone sensing params
                */
                sensorManager.setSensorConfig(SensorUtils.SENSOR_TYPE_MICROPHONE, PullSensorConfig.SENSE_WINDOW_LENGTH_MILLIS, 2000L);
                sensorManager.setSensorConfig(SensorUtils.SENSOR_TYPE_MICROPHONE, PullSensorConfig.POST_SENSE_SLEEP_LENGTH_MILLIS, 5000L);

                /*
                 * Set camera 'sensing' params

                sensorManager.setSensorConfig(SensorUtils.SENSOR_TYPE_CAMERA, PullSensorConfig.POST_SENSE_SLEEP_LENGTH_MILLIS, 5000L);
                sensorManager.setSensorConfig(SensorUtils.SENSOR_TYPE_CAMERA, CameraConfig.CAMERA_TYPE, CameraConfig.CAMERA_TYPE_FRONT);
*/
                String rootDirectory = (String) dataLogger.getDataManager().getConfig(DataStorageConfig.LOCAL_STORAGE_ROOT_DIRECTORY_NAME);

                /*
                 * Store audio files to /Sounds
*/
                sensorManager.setSensorConfig(SensorUtils.SENSOR_TYPE_MICROPHONE, MicrophoneConfig.AUDIO_FILES_DIRECTORY, rootDirectory + "/Sounds");
                /*
                 * Store image files to /Images

                sensorManager.setSensorConfig(SensorUtils.SENSOR_TYPE_CAMERA, CameraConfig.IMAGE_FILES_DIRECTORY, rootDirectory + "/Images");
*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean startSensing()
    {
        int subscriptionId;
        mSubscriptions = new ArrayList<Integer>();

        try
        {
            for (int sensorType : MONITORED_SENSORS) {
                subscriptionId = sensorManager.subscribeToSensorData(sensorType, this);
                mSubscriptions.add(subscriptionId);
            }
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public boolean stopSensing()
    {
        try
        {
            for (Integer subscriptionId : mSubscriptions) {
                Log.d("Sensor", "Unsubscribing id = " + subscriptionId);
                sensorManager.unsubscribeFromSensorData(subscriptionId);
            }
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onDataSensed(SensorData data) {
        DataFormatter formatter = DataFormatter.getJSONFormatter(context, data.getSensorType());
        try {
            Log.d("Formatter", SensorUtils.getSensorName(data.getSensorType()));
        } catch (ESException e) {
            e.printStackTrace();
        }
        Log.d("Data Sensed", formatter.toString(data));
        dataLogger.logSensorData(data);
    }

    @Override
    public void onCrossingLowBatteryThreshold(boolean isBelowThreshold) {
        // Nothing for example app
    }
}
