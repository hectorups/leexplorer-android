package com.leexplorer.app.services;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import java.util.Arrays;

/**
 * Created by hectormonserrate on 22/02/14.
 */
@TargetApi(18)
public class BeaconScanService extends IntentService {
    private static final int INTERVAL_FOREGROUND = 10000; // 2 * 60 * 1000;
    private static final int INTERVAL_BACKGROUND = 5 * 60 * 1000;
    private static final int SCAN_PERIOD = 2 * 1000;

    private final String TAG = "com.leexplorer.app.services.beaconscanservice";

    private static BluetoothManager bluetoothManager;

    private static BluetoothAdapter bluetoothAdapter;
    private Handler leHandler = new Handler();

    private static boolean scanning = false;

    public BeaconScanService() {
        super("beaconscan-service");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Intent received");

        setBluetoothAdapter();
        bluetoothAdapter.startLeScan(leScanCallback);

        leHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                scanning = false;
                bluetoothAdapter.stopLeScan(leScanCallback);
                endSearch();
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
                            + serviceFromScanRecord(scanRecord) + " - "
                            + rssi
                    );


                }
    };

    public String serviceFromScanRecord(byte[] scanRecord) {

        final int serviceOffset = 9;
        final int serviceLimit = 16;
        try{
            byte[] service = Arrays.copyOfRange(scanRecord, serviceOffset, serviceOffset + serviceLimit);
            return bytesToHex(service);
        } catch (Exception e){
            return null;
        }
    }


    public String bytesToHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b: bytes) {
            builder.append(String.format("%02x ", b));
        }
        return builder.toString();
    }

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

}
