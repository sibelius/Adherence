package com.ubhave.sensormanager.process.pull;

import android.content.Context;

import com.ubhave.sensormanager.config.SensorConfig;
import com.ubhave.sensormanager.data.pullsensor.GravityData;
import com.ubhave.sensormanager.data.pullsensor.GyroscopeData;
import com.ubhave.sensormanager.process.AbstractProcessor;

import java.util.ArrayList;

public class GravityProcessor extends AbstractProcessor
{
	public GravityProcessor(final Context c, boolean rw, boolean sp)
	{
		super(c, rw, sp);
	}

	public GravityData process(long pullSenseStartTimestamp, ArrayList<float[]> sensorReadings,
			ArrayList<Long> sensorReadingTimestamps, SensorConfig sensorConfig)
	{
        GravityData gravityData = new GravityData(pullSenseStartTimestamp, sensorConfig);
		if (setRawData)
		{
            gravityData.setSensorReadings(sensorReadings);
            gravityData.setSensorReadingTimestamps(sensorReadingTimestamps);
		}
        return gravityData;
	}
}
