package com.leexplorer.app.services;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.leexplorer.app.util.Beacon;
import com.leexplorer.app.util.BeaconsManager;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by hectormonserrate on 22/02/14.
 */
@TargetApi(18)
public class BeaconScanService extends IntentService {
    private static final int INTERVAL_FOREGROUND = 10000; // 2 * 60 * 1000;
    private static final int INTERVAL_BACKGROUND = 5 * 60 * 1000;
    private static final int SCAN_PERIOD = 2500;

    public static final String ACTION = "com.leexplorer.services.beaconscanservice";
    public static final String BEACONS = "beacons";

    private final String TAG = "com.leexplorer.app.services.beaconscanservice";

    private static BluetoothManager bluetoothManager;

    private static BluetoothAdapter bluetoothAdapter;
    private Handler leHandler = new Handler();

    private HashMap<String,Beacon> beacons;

    private static boolean scanning = false;

    public BeaconScanService() {
        super("beaconscan-service");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Intent received");

        setBluetoothAdapter();
        bluetoothAdapter.startLeScan(leScanCallback);
        scanning = true;

        beacons = new HashMap<>();

        leHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                scanning = false;
                bluetoothAdapter.stopLeScan(leScanCallback);
                endSearch();
                broadcastBeacons();
            }
        }, SCAN_PERIOD);

    }

    private void setBluetoothAdapter(){
        if( bluetoothAdapter == null ){
            bluetoothManager = (BluetoothManager) getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
            bluetoothAdapter = bluetoothManager.getAdapter();
        }
    }

    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback(){
                @Override
                public void onLeScan(final BluetoothDevice device,
                                     int rssi,
                                     byte[] scanRecord) {
                    Log.d(TAG, "Bluetooth found: "
                            + device.getName() + " - "
                            + device.getAddress() + " - "
                            + rssi
                    );

                    if( beacons.get(device.getAddress()) == null ){
                        Beacon beacon = new Beacon(device.getAddress(), scanRecord, rssi);
                        beacons.put(device.getAddress(), beacon);
                    } else {
                        beacons.get(device.getAddress()).addRssi(rssi);
                    }
                }
    };


    private void endSearch(){
        Log.d(TAG, "search finished");
        bluetoothAdapter.stopLeScan(leScanCallback);
    }

    public static void setScannerAlarm(Context context) {
        Intent i = new Intent(context, BeaconScanService.class);
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), INTERVAL_FOREGROUND, pi);
    }

    private void broadcastBeacons(){
        Intent in = new Intent(ACTION);
        in.putExtra("resultCode", Activity.RESULT_OK);

        BeaconsManager beaconManager = BeaconsManager.getInstance();
        beaconManager.updateBeacons(new ArrayList<>(beacons.values()));
        in.putExtra(BEACONS, beaconManager.getAll());

        LocalBroadcastManager.getInstance(this).sendBroadcast(in);
    }

}
