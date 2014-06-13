package com.ubhave.sensormanager.process.pull;

import android.content.Context;

import com.ubhave.sensormanager.config.SensorConfig;
import com.ubhave.sensormanager.data.pullsensor.GravityData;
import com.ubhave.sensormanager.data.pullsensor.LinearAccelerationData;
import com.ubhave.sensormanager.process.AbstractProcessor;

import java.util.ArrayList;

public class LinearAccelerationProcessor extends AbstractProcessor
{
	public LinearAccelerationProcessor(final Context c, boolean rw, boolean sp)
	{
		super(c, rw, sp);
	}

	public LinearAccelerationData process(long pullSenseStartTimestamp, ArrayList<float[]> sensorReadings,
			ArrayList<Long> sensorReadingTimestamps, SensorConfig sensorConfig)
	{
        LinearAccelerationData linearAccelerationData = new LinearAccelerationData(pullSenseStartTimestamp, sensorConfig);
		if (setRawData)
		{
            linearAccelerationData.setSensorReadings(sensorReadings);
            linearAccelerationData.setSensorReadingTimestamps(sensorReadingTimestamps);
		}
        return linearAccelerationData;
	}
}
