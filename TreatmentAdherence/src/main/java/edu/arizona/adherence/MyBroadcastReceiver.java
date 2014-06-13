package edu.arizona.adherence;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Start Sensing Service when Android system booted
 *
 * Created by sibelius on 6/2/14.
 */
public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent startServiceIntent = new Intent(context, SensingService.class);
        context.startService(startServiceIntent);
    }
}
