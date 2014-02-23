package com.leexplorer.app.services.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by hectormonserrate on 22/02/14.
 */
public class StartupReceiver extends BroadcastReceiver {
    private static final String TAG = "com.leexplorer.services.receivers.startupreceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Received broadcast intent: " + intent.getAction());

        // BeaconScanService.setScannerAlarm(context);
    }
}
