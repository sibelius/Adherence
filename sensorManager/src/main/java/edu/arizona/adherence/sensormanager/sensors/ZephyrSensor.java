package edu.arizona.adherence.sensormanager.sensors;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.ubhave.sensormanager.ESException;
import com.ubhave.sensormanager.config.GlobalConfig;
import com.ubhave.sensormanager.sensors.SensorUtils;
import com.ubhave.sensormanager.sensors.push.AbstractPushSensor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import edu.arizona.adherence.sensormanager.data.ZephyrData;
import edu.arizona.adherence.sensormanager.process.ZephyrProcessor;
import zephyr.android.HxMBT.BTClient;

/**
 * Created by sibelius on 6/5/14.
 */
public class ZephyrSensor extends AbstractPushSensor {

    private static final String TAG = "ZephyrSensor";
    private static final String PERMISSION_BLUETOOTH = "android.permission.BLUETOOTH";
    private static final String PERMISSION_BT_ADMIN = "android.permission.BLUETOOTH_ADMIN";

    public final static String SENSOR_NAME_ZEPHYR = "Zephyr";

    private static ZephyrSensor zephyrSensor;
    private static Object lock = new Object();

    private BluetoothAdapter bluetooth = null;
    private ZephyrData zephyrData;
    private int mBatteryChargeInd;
    private int mHeartRate; // Beats per minute
    private int mHeartBeatNum;
    private int[] mHeartBeatTS;
    private double mDistance;
    private double mInstantSpeed;
    private int mStrides;

    //private String BhMacID = "00:07:80:9D:8A:E8";
    private String BhMacID = "";
    private BTClient mBT;
    NewConnectedListener mNewConnectedListener;

    public static final java.lang.String EXTRA_DEVICE = "android.bluetooth.device.extra.DEVICE";
    public static final java.lang.String EXTRA_PAIRING_VARIANT = "android.bluetooth.device.extra.PAIRING_VARIANT";

    public static ZephyrSensor getZephyrSensor(final Context context) throws ESException {
        Log.d("ZEPHYR", "Constructor");
        if (zephyrSensor == null) {
            synchronized (lock) {
                if (zephyrSensor == null) {
                    if (allPermissionsGranted(context, new String[]{PERMISSION_BLUETOOTH, PERMISSION_BT_ADMIN})) {
                        zephyrSensor = new ZephyrSensor(context);
                    } else {
                        throw new ESException(ESException.PERMISSION_DENIED, SENSOR_NAME_ZEPHYR);
                    }
                }
            }
        }
        return zephyrSensor;
    }

    private ZephyrSensor(Context context) {
        super(context);

        bluetooth = BluetoothAdapter.getDefaultAdapter();
        if (bluetooth == null) {
            if (GlobalConfig.shouldLog()) {
                Log.d(TAG, "Device does not support Bluetooth");
            }
            return;
        }

        mNewConnectedListener = new NewConnectedListener(mHandler, mHandler);
    }

    @Override
    protected void onBroadcastReceived(Context context, Intent intent) {
        String action = intent.getAction();
        // When discovery finds a device
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            // Get the BluetoothDevice object from the Intent
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            // Add the name and address to an array adapter to show in a ListView
            if (device.getName().startsWith("HXM")) {
                BluetoothDevice btDevice = device;
                BhMacID = btDevice.getAddress();

                mBT = new BTClient(bluetooth, BhMacID);
                mBT.addConnectedEventListener(mNewConnectedListener);

                if (mBT.IsConnected()) {
                    mBT.start();
                    Log.d("ZEPHYR", "Connected_ACTION_FOUND");
                }
            }
        } else if (BluetoothDevice.ACTION_PAIRING_REQUEST.equals(action)) {
            Bundle b = intent.getExtras();
            Log.d("ZEPHYR_BTIntent", b.get(EXTRA_DEVICE).toString());
            Log.d("ZEPHYR_BTIntent", b.get(EXTRA_PAIRING_VARIANT).toString());
            try {
                BluetoothDevice device = bluetooth.getRemoteDevice(b.get(BluetoothDevice.EXTRA_DEVICE).toString());
                Method m = BluetoothDevice.class.getMethod("convertPinToBytes", new Class[]{String.class});
                byte[] pin = (byte[]) m.invoke(device, "1234");
                m = device.getClass().getMethod("setPin", new Class[]{pin.getClass()});
                Object result = m.invoke(device, pin);
                Log.d("ZEPHYR_BTTest", result.toString());
            } catch (SecurityException e1) {
                e1.printStackTrace();
            } catch (NoSuchMethodException e1) {
                e1.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
            Log.d("ZEPHYR", "Disconnected");
        }
    }

    @Override
    protected IntentFilter[] getIntentFilters() {
        IntentFilter[] filters = new IntentFilter[3];
        filters[0] = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filters[1] = new IntentFilter(BluetoothDevice.ACTION_PAIRING_REQUEST);
        filters[2] = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);

        return filters;
    }

    @Override
    protected boolean startSensing() {
        Log.d("ZEPHYR", "startSensing");
        if (!bluetooth.isEnabled()) {
            bluetooth.enable();
            while (!bluetooth.isEnabled()) {
                try {
                    Thread.sleep(100);
                } catch (Exception exp) {
                    exp.printStackTrace();
                }
            }
        }

        Set<BluetoothDevice> pairedDevices = bluetooth.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().startsWith("HXM")) {
                    BluetoothDevice btDevice = device;
                    BhMacID = btDevice.getAddress();

                    try {
                        mBT = new BTClient(bluetooth, BhMacID);
                        mBT.addConnectedEventListener(mNewConnectedListener);

                        if (mBT.IsConnected()) {
                            mBT.start();
                            Log.d("ZEPHYR", "Connected");
                            return true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        // Try to discover the device
        bluetooth.startDiscovery();

        Log.d("ZEPHYR", "Discovery Mode");
        return false;
    }

    @Override
    protected void stopSensing() {
        Log.d("ZEPHYR", "stopSensing");

        if (bluetooth != null) {
            try {
                if (mBT != null) {
                    mBT.removeConnectedEventListener(mNewConnectedListener);
                    mBT.Close();
                    mBT = null;
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            bluetooth.cancelDiscovery();
            bluetooth.disable();
        }
    }

    @Override
    protected String getLogTag() {
        return TAG;
    }

    @Override
    public int getSensorType() {
        return SensorUtils.SENSOR_TYPE_ZEPHYR;
    }

    final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            Log.d("ZEPHYR", "HANDLER");
            switch (msg.what) {
                case NewConnectedListener.DATA:
                    Bundle b = msg.getData();

                    mBatteryChargeInd = b.getInt(NewConnectedListener.BATTERY);
                    mHeartRate = b.getInt(NewConnectedListener.HEARTRATE);
                    mHeartBeatNum = b.getInt(NewConnectedListener.HEARTBEATNUM);
                    mHeartBeatTS = b.getIntArray(NewConnectedListener.HEARTBEATTS);
                    mDistance = b.getDouble(NewConnectedListener.DISTANCE);
                    mInstantSpeed = b.getDouble(NewConnectedListener.INSTANTSPEED);
                    mStrides = b.getInt(NewConnectedListener.STRIDES);

                    Log.d("ZEPHYR_HR", String.valueOf(mHeartRate));

                    ZephyrProcessor processor = (ZephyrProcessor) getProcessor();
                    zephyrData = processor.process(
                            System.currentTimeMillis(),
                            mBatteryChargeInd,
                            mHeartRate,
                            mHeartBeatNum,
                            mHeartBeatTS,
                            mDistance,
                            mInstantSpeed,
                            mStrides,
                            sensorConfig.clone());
                    onDataSensed(zephyrData);
            }

        }
    };
}
