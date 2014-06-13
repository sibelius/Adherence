package edu.arizona.adherence;

import android.content.Context;

import com.ubhave.datahandler.loggertypes.AbstractStoreOnlyLogger;

/**
 * Created by sibelius on 6/1/14.
 */
public class SensingStoreOnlyLogger extends AbstractStoreOnlyLogger {

    private String mDeviceId = "TaDevice";

    public SensingStoreOnlyLogger(Context context) {
        super(context);
    }

    @Override
    protected String getLocalStorageDirectoryName()
    {
        return "physical_experiment";
    }

    @Override
    protected String getUniqueUserId()
    {
		/*
		 * Note: Should be unique to this user, not a static string
		 */
        return "feitico";
    }

    public void setDeviceId(String deviceId) {
        mDeviceId = deviceId;
    }

    @Override
    protected String getDeviceId()
    {
		/*
		 * Note: Should be unique to this device, not a static string
		 */
        return mDeviceId;
    }

    @Override
    protected boolean shouldPrintLogMessages() {
		/*
		 * Turn on/off Log.d messages
		 */
        return true;
    }
}
