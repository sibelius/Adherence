package edu.arizona.adherence;

import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;

import com.ubhave.datahandler.loggertypes.AbstractAsyncTransferLogger;

import java.util.HashMap;

/**
 * Created by sibelius on 6/1/14.
 */
public class SensingAsyncTransferLogger extends AbstractAsyncTransferLogger {

    private static final String RESPONSE_ON_SUCCESSFUL_POST = "SAVED";
    private static final String FILE_POST_URL = "http://adherencetreatment.appspot.com/upload";
    private static final String LOCAL_STORAGE_DIRECTORY = "CrowdSensing";

    private static final String TAG_DEVICE = "device";
    private static final String TAG_USER = "user";

    public SensingAsyncTransferLogger(Context context)
    {
        super(context);
    }

    @Override
    protected long getFileLifeMillis()
    {
		/*
		 *  Transfer any files that are more than 5 minutes old
		 */
        //return (1000L * 30);
        return (5 * 60 * 1000L);
    }

    @Override
    protected long getTransferAlarmLengthMillis()
    {
		/*
		 *  Try to transfer data every 5 minute
		 */
        return (30 * 60 * 1000L);
    }


    @Override
    protected String getDataPostURL() {
        return FILE_POST_URL;
    }

    @Override
    protected String getLocalStorageDirectoryName() {
        return LOCAL_STORAGE_DIRECTORY;
    }

    @Override
    protected String getUniqueUserId() {
        /*
		 * Note: Should be unique to this user, not a static string
		 */
        SharedPreferences settings = context.getSharedPreferences(ProfileActivity.PREFS_NAME, 0);
        return settings.getString(ProfileActivity.NAME, "uniqueuserid");
    }

    @Override
    protected String getDeviceId() {
        TelephonyManager mngr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        return mngr.getDeviceId();
    }

    @Override
    protected String getSuccessfulPostResponse()
    {
        return RESPONSE_ON_SUCCESSFUL_POST;
    }

    @Override
    protected HashMap<String, String> getPostParameters()
    {
		/*
		 * Parameters to be used when POST-ing data
		 */
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(TAG_DEVICE, getDeviceId());
        params.put(TAG_USER, getUniqueUserId());
        return params;
    }

    @Override
    protected boolean shouldPrintLogMessages()
    {
		/*
		 * Turn on/off Log.d messages
		 */
        return true;
    }
}
