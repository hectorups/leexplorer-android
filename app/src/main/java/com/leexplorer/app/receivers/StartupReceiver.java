package com.leexplorer.app.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import com.leexplorer.app.services.BeaconScanService;
import com.leexplorer.app.services.LocationService;

public class StartupReceiver extends BroadcastReceiver {
  private static final String TAG = "com.leexplorer.services.receivers.startupreceiver";

  @Override
  public void onReceive(Context context, Intent intent) {
    Log.i(TAG, "Received broadcast intent: " + intent.getAction());

    Intent locationService = new Intent(context, LocationService.class);
    context.startService(locationService);

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
      return;
    }

    BeaconScanService.setScannerAlarm(context, BeaconScanService.Mode.BACKGROUND);
  }
}
