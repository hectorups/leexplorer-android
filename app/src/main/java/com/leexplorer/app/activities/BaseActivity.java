package com.leexplorer.app.activities;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Window;

import com.leexplorer.app.services.BeaconScanService;

/**
 * Created by hectormonserrate on 20/02/14.
 */
public class BaseActivity extends ActionBarActivity {
    private int processesLoading = 0;
    public static final String TAG = "com.leexplorer.activities.baseactivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
    }

    public void onLoading(boolean loading){
        processesLoading += loading ? 1 : -1;
        if( processesLoading < 1 ){
            processesLoading = 0;
            setProgressBarIndeterminateVisibility(false);
        } else {
            setProgressBarIndeterminateVisibility(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(BeaconScanService.ACTION_SHOW_NOTIFICATION);
        registerReceiver(onShowNotification, filter, BeaconScanService.PERM_PRIVATE, null);

        // Cancel existing notification if any
        if (Context.NOTIFICATION_SERVICE != null) {
            String ns = Context.NOTIFICATION_SERVICE;
            NotificationManager notificationManager = (NotificationManager) getSystemService(ns);
            notificationManager.cancel(0);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(onShowNotification);
    }


    // This Receiver is ON when the activity is displaying. When on it catches the notification
    // before NotificationReceiver does and cancels it.
    private BroadcastReceiver onShowNotification = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "canceling notification");
            setResultCode(Activity.RESULT_CANCELED);
        }
    };
}
