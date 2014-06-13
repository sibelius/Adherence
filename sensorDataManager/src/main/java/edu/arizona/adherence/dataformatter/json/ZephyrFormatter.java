package edu.arizona.adherence.dataformatter.json;

import android.content.Context;

import com.ubhave.dataformatter.json.PullSensorJSONFormatter;
import com.ubhave.sensormanager.config.SensorConfig;
import com.ubhave.sensormanager.data.SensorData;
import com.ubhave.sensormanager.sensors.SensorUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.arizona.adherence.sensormanager.data.ZephyrData;

/**
 * Created by sibelius on 6/5/14.
 */
public class ZephyrFormatter extends PullSensorJSONFormatter {

    private final static String BATTERY = "battery";
    private final static String HEARTRATE = "heartrate";
    private final static String HEARTBEATNUM = "heartbeatnum";
    private final static String HEARTBEATTS = "heartbeatts";
    private final static String DISTANCE = "distance";
    private final static String INSTANTSPEED = "instantspeed";
    private final static String STRIDES = "strides";

    public ZephyrFormatter(final Context context) {
        super (context, SensorUtils.SENSOR_TYPE_ZEPHYR);
    }

    public static int[] convertIntegers(List<Integer> integers)
    {
        int[] ret = new int[integers.size()];
        Iterator<Integer> iterator = integers.iterator();
        for (int i = 0; i < ret.length; i++)
        {
            ret[i] = iterator.next().intValue();
        }
        return ret;
    }

    @Override
    public SensorData toSensorData(String jsonString) {
        JSONObject jsonData = super.parseData(jsonString);
        if (jsonData != null) {
            long senseStartTimestamp = super.parseTimeStamp(jsonData);
            SensorConfig sensorConfig = super.getGenericConfig(jsonData);
            ZephyrData data = new ZephyrData(senseStartTimestamp, sensorConfig);
/*
            int batteryChargeInd;
            int heartRate;
            int heartBeatNum;
            int[] heartBeatTS;
            double distance;
            double instantSpeed;
            int strides;
            boolean setRawData = true;
            boolean setProcessedData = false;
            */
            try {
                data.setBatteryChargeInd(jsonData.getInt(BATTERY));
                data.setHeartRate(jsonData.getInt(HEARTRATE));
                data.setHeartBeatNum(jsonData.getInt(HEARTBEATNUM));

                ArrayList<Integer> heartBeatTs = getJSONArray(jsonData, HEARTBEATTS, Integer.class);
                data.setHeartBeatTS(convertIntegers(heartBeatTs));

                data.setDistance(jsonData.getDouble(DISTANCE));
                data.setInstantSpeed(jsonData.getDouble(INSTANTSPEED));
                data.setStrides(jsonData.getInt(STRIDES));
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return data;
        } else {
            return null;
        }
    }

    @Override
    protected void addSensorSpecificData(JSONObject json, SensorData data) throws JSONException {
        ZephyrData zephyrData = (ZephyrData) data;

        json.put(BATTERY, zephyrData.getBatteryChargeInd());
        json.put(HEARTRATE, zephyrData.getHeartRate());
        json.put(HEARTBEATNUM, zephyrData.getHeartBeatNum());

        JSONArray arrayHB = new JSONArray();

        int[] heartBeatTs = zephyrData.getHeartBeatTS();

        for(int hbts : heartBeatTs) {
            arrayHB.put(hbts);
        }

        json.put(HEARTBEATTS, arrayHB);

        json.put(DISTANCE, zephyrData.getDistance());
        json.put(INSTANTSPEED, zephyrData.getInstantSpeed());
        json.put(STRIDES, zephyrData.getStrides());
    }

    @Override
    protected void addSensorSpecificConfig(JSONObject json, SensorConfig config) throws JSONException {
    }
}
