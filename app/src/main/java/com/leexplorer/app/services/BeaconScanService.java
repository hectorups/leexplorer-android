package com.leexplorer.app.services;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.leexplorer.app.R;
import com.leexplorer.app.activities.ArtworkListActivity;
import com.leexplorer.app.activities.GalleryActivity;
import com.leexplorer.app.activities.GalleryListActivity;
import com.leexplorer.app.api.Client;
import com.leexplorer.app.api.models.Artwork;
import com.leexplorer.app.models.Gallery;
import com.leexplorer.app.util.Beacon;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by hectormonserrate on 22/02/14.
 */
@TargetApi(18)
public class BeaconScanService extends IntentService {
    public static final String ACTION = "com.leexplorer.services.beaconscanservice";
    public static final String BEACONS = "beacons";
    public static final String ACTION_SHOW_NOTIFICATION = "com.leexplorer.services.beaconscanservice.SHOW_NOTIFICATION";
    public static final String PERM_PRIVATE = "com.leexplorer.beaconscanservice.PRIVATE";
    private static final int INTERVAL_FOREGROUND = 2 * 60 * 1000;
    private static final int INTERVAL_BACKGROUND = 5 * 60 * 1000; // Don't drain the battery when in bg!
    private static final int SCAN_PERIOD = 4000;
    private final String TAG = "com.leexplorer.app.services.beaconscanservice";
    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device,
                             int rssi,
                             byte[] scanRecord) {
            Log.d(TAG, "Bluetooth found: "
                            + device.getName() + " - "
                            + device.getAddress() + " - "
                            + rssi
            );

            if (beacons.get(device.getAddress()) == null) {
                Beacon beacon = new Beacon(device.getAddress(), scanRecord, rssi);
                beacons.put(device.getAddress(), beacon);
            } else {
                beacons.get(device.getAddress()).addRssi(rssi);
            }
        }
    };
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private HashMap<String, Beacon> beacons;


    public BeaconScanService() {
        super("beaconscan-service");
    }

    public static void setScannerAlarm(Context context, boolean foreground) {
        Intent i = new Intent(context, BeaconScanService.class);
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);


        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pi);

        int interval = foreground ? INTERVAL_FOREGROUND : INTERVAL_BACKGROUND;
        alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), interval, pi);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Intent received");

        setBluetoothAdapter();
        bluetoothAdapter.startLeScan(leScanCallback);

        beacons = new HashMap<>();

        SystemClock.sleep(SCAN_PERIOD);

        endSearch();
        broadcastBeacons();
        sendNotification();

    }

    private void setBluetoothAdapter() {
        if (bluetoothAdapter == null) {
            bluetoothManager = (BluetoothManager) getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
            bluetoothAdapter = bluetoothManager.getAdapter();
        }
    }

    private void endSearch() {
        Log.d(TAG, "search finished");
        bluetoothAdapter.stopLeScan(leScanCallback);
    }

    private void broadcastBeacons() {
        Intent in = new Intent(ACTION);
        in.putExtra("resultCode", Activity.RESULT_OK);

        in.putExtra(BEACONS, new ArrayList<>(beacons.values()));

        LocalBroadcastManager.getInstance(this).sendBroadcast(in);
    }

    private void sendNotification() {
        Gallery g = unseenGallery();
        if (g == null) {
            return;
        }

        Resources r = getResources();

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        stackBuilder.addNextIntent(new Intent(this, GalleryListActivity.class));

        Intent galleryIntent = new Intent(this, GalleryActivity.class);
        galleryIntent.putExtra(GalleryActivity.GALLERY_KEY, g);
        stackBuilder.addNextIntent(galleryIntent);

        Intent artworkListIntent = new Intent(this, ArtworkListActivity.class);
        artworkListIntent.putExtra(ArtworkListActivity.EXTRA_GALLERY, g);
        artworkListIntent.putExtra(ArtworkListActivity.EXTRA_FROM_NOTIFICATION, true);
        stackBuilder.addNextIntent(artworkListIntent);

        PendingIntent pi = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);

        Notification notification = new NotificationCompat.Builder(this)
                .setTicker(r.getString(R.string.beacon_notification_title))
                .setSmallIcon(R.drawable.ic_stat_artwork)
                .setContentTitle(r.getString(R.string.beacon_notification_title))
                .setContentText(r.getString(R.string.beacon_notification_text, g.getName()))
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build();

        Intent notificationIntent = new Intent(ACTION_SHOW_NOTIFICATION);
        notificationIntent.putExtra("REQUEST_CODE", 0);
        notificationIntent.putExtra("NOTIFICATION", notification);

        sendOrderedBroadcast(notificationIntent, PERM_PRIVATE, null, null, Activity.RESULT_OK, null, null);
    }

    private Gallery unseenGallery() {
        Gallery g = null;
        for (Beacon beacon : beacons.values()) {
            g = galleryFromBeacon(beacon);

            if (g != null) {
                break;
            }
        }

        return g;
    }

    private Gallery galleryFromBeacon(Beacon beacon) {
        String galleryId = null;
        com.leexplorer.app.models.Artwork artwork = com.leexplorer.app.models.Artwork.findByMac(beacon.getMac());

        if (artwork == null) {
            Artwork apiAw = null;

            try {
                apiAw = Client.getService().getArtwork(beacon.getMac());
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (apiAw != null) {
                artwork = com.leexplorer.app.models.Artwork.fromJsonModel(apiAw);
                artwork.save();
            }
        }

        if (artwork != null) {
            galleryId = artwork.getGalleryId();
        }

        if (galleryId == null) {
            return null;
        }

        Gallery gallery = Gallery.findById(galleryId);

        if (gallery == null) {
            com.leexplorer.app.api.models.Gallery apiGallery = null;
            try {
                apiGallery = Client.getService().getGallery(galleryId);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (apiGallery != null) {
                gallery = Gallery.fromApiModel(apiGallery);
            }
        } else if (gallery.isWasSeen()) {
            return null;
        }

        return gallery;
    }

}
