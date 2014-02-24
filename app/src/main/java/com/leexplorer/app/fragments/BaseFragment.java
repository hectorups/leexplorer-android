package com.leexplorer.app.fragments;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.leexplorer.app.services.BeaconScanService;

/**
 * Created by hectormonserrate on 23/02/14.
 */
public class BaseFragment extends Fragment {
    public static final String TAG = "VisibleFragment";

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(BeaconScanService.ACTION_SHOW_NOTIFICATION);
        getActivity().registerReceiver(onShowNotification, filter, BeaconScanService.PERM_PRIVATE, null);

        // Cancel existing notification if any
        if (Context.NOTIFICATION_SERVICE != null) {
            String ns = Context.NOTIFICATION_SERVICE;
            NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(ns);
            notificationManager.cancel(0);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(onShowNotification);
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
