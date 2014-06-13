package edu.arizona.adherence.sensormanager.process;

import android.content.Context;

import com.ubhave.sensormanager.config.SensorConfig;
import com.ubhave.sensormanager.process.AbstractProcessor;

import edu.arizona.adherence.sensormanager.data.ZephyrData;

/**
 * Created by sibelius on 6/5/14.
 */
public class ZephyrProcessor extends AbstractProcessor {
    public ZephyrProcessor(final Context c, boolean rw, boolean sp) {
        super(c, rw, sp);
    }

    public ZephyrData process(
            long pullSenseStartTimestamp,
            int batteryChargeInd,
            int heartRate,
            int heartBeatNum,
            int[] heartBeatTS,
            double distance,
            double instantSpeed,
            int strides,
            SensorConfig sensorConfig) {
        ZephyrData zephyrData = new ZephyrData(pullSenseStartTimestamp, sensorConfig);
        if (setRawData) {
            zephyrData.setBatteryChargeInd(batteryChargeInd);
            zephyrData.setHeartRate(heartRate);
            zephyrData.setHeartBeatNum(heartBeatNum);
            zephyrData.setHeartBeatTS(heartBeatTS);
            zephyrData.setDistance(distance);
            zephyrData.setInstantSpeed(instantSpeed);
            zephyrData.setStrides(strides);
        }
        return zephyrData;
    }
}
