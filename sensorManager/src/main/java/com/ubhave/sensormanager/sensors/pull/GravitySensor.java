/* **************************************************
 Copyright (c) 2014

This library was developed as part of the EPSRC Ubhave (Ubiquitous and
Social Computing for Positive Behaviour Change) Project. For more
information, please visit http://www.emotionsense.org

Permission to use, copy, modify, and/or distribute this software for any
purpose with or without fee is hereby granted, provided that the above
copyright notice and this permission notice appear in all copies.

THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 ************************************************** */

package com.ubhave.sensormanager.sensors.pull;

import android.content.Context;
import android.hardware.Sensor;

import com.ubhave.sensormanager.data.pullsensor.GravityData;
import com.ubhave.sensormanager.data.pullsensor.GyroscopeData;
import com.ubhave.sensormanager.process.pull.GravityProcessor;
import com.ubhave.sensormanager.process.pull.GyroscopeProcessor;
import com.ubhave.sensormanager.sensors.SensorUtils;

public class GravitySensor extends AbstractMotionSensor
{
	private static final String TAG = "GravitySensor";
	private static GravitySensor gravitySensor;
	private static Object lock = new Object();

    private GravityData gravityData;

	public static GravitySensor getGravitySensor(final Context context)
	{
		if (gravitySensor == null)
		{
			synchronized (lock)
			{
				if (gravitySensor == null)
				{
					gravitySensor = new GravitySensor(context);
				}
			}
		}
		return gravitySensor;
	}

	private GravitySensor(final Context context)
	{
		super(context, Sensor.TYPE_GRAVITY);
	}

	protected String getLogTag()
	{
		return TAG;
	}

	public int getSensorType()
	{
		return SensorUtils.SENSOR_TYPE_GRAVITY;
	}

	protected GravityData getMostRecentRawData()
	{
		return gravityData;
	}
	
	protected void processSensorData()
	{
		synchronized (sensorReadings)
		{
            GravityProcessor processor = (GravityProcessor) getProcessor();
            gravityData = processor.process(pullSenseStartTimestamp, sensorReadings, sensorReadingTimestamps, sensorConfig.clone());
		}
	}
}
