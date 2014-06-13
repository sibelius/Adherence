package edu.arizona.adherence.sensormanager.data;

import com.ubhave.sensormanager.config.SensorConfig;
import com.ubhave.sensormanager.data.SensorData;
import com.ubhave.sensormanager.sensors.SensorUtils;

/**
 * ZephyrData represent the sensor data Zephyr
 * Created by sibelius on 6/5/14.
 */
public class ZephyrData extends SensorData {
    private int mBatteryChargeInd;
    private int mHeartRate; // Beats per minute

    private int mHeartBeatNum;
    private int[] mHeartBeatTS;

    private double mDistance;
    private double mInstantSpeed;
    private int mStrides;

    public ZephyrData(long senseStartTimestamp, SensorConfig sensorConfig) {
        super (senseStartTimestamp, sensorConfig);

        mBatteryChargeInd = -1;
        mHeartRate = -1;
        mHeartBeatNum = -1;
        mHeartBeatTS = new int[1];
        mDistance = -1.0;
        mInstantSpeed = 1.0;
        mStrides = -1;
    }

    public void setBatteryChargeInd(int batteryChargeInd) {
        mBatteryChargeInd = batteryChargeInd;
    }

    public int getBatteryChargeInd() {
        return mBatteryChargeInd;
    }

    public void setHeartRate(int heartRate) {
        mHeartRate = heartRate;
    }

    public int getHeartRate() {
        return mHeartRate;
    }

    public void setHeartBeatNum(int heartBeatNum) {
        mHeartBeatNum = heartBeatNum;
    }

    public int getHeartBeatNum() {
        return mHeartBeatNum;
    }

    public void setHeartBeatTS(int[] heartBeatTS) {
        mHeartBeatTS = heartBeatTS;
    }

    public int[] getHeartBeatTS() {
        return mHeartBeatTS;
    }

    public void setDistance(double distance) {
        mDistance = distance;
    }

    public double getDistance() {
        return mDistance;
    }

    public void setInstantSpeed(double instantSpeed) {
        mInstantSpeed = instantSpeed;
    }

    public double getInstantSpeed() {
        return mInstantSpeed;
    }

    public void setStrides(int strides) {
        mStrides = strides;
    }

    public int getStrides() {
        return mStrides;
    }

    @Override
    public int getSensorType() {
        return SensorUtils.SENSOR_TYPE_ZEPHYR;
    }
}
