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
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import com.leexplorer.app.R;
import com.leexplorer.app.activities.ArtworkListActivity;
import com.leexplorer.app.activities.GalleryActivity;
import com.leexplorer.app.activities.GalleryListActivity;
import com.leexplorer.app.api.Client;
import com.leexplorer.app.api.models.Artwork;
import com.leexplorer.app.core.AppConstants;
import com.leexplorer.app.core.EventReporter;
import com.leexplorer.app.core.LeexplorerApplication;
import com.leexplorer.app.events.BeaconsScanResultEvent;
import com.leexplorer.app.models.FilteredIBeacon;
import com.leexplorer.app.models.Gallery;
import com.leexplorer.app.models.IBeacon;
import com.squareup.otto.Bus;
import java.util.ArrayList;
import java.util.HashMap;
import javax.inject.Inject;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BeaconScanService extends IntentService {
  public static final String ACTION_SHOW_NOTIFICATION =
      "com.leexplorer.services.beaconscanservice.SHOW_NOTIFICATION";
  public static final String SERVICE_NAME = "beaconscan-serviceFromScanRecord";
  public static final String PERM_PRIVATE = "com.leexplorer.beaconscanservice.PRIVATE";
  private static final int INTERVAL_FOREGROUND = 2 * 60 * 1000;
  private static final int INTERVAL_BACKGROUND = 4 * 60 * 1000;
  // Don't drain the battery when in bg!
  private static final int SCAN_PERIOD = 4000;
  private static final String TAG = "com.leexplorer.app.services.beaconscanservice";
  private static final String LOG_SEPARATOR = " - ";

  @Inject Client client;
  @Inject Bus bus;
  @Inject EventReporter eventReporter;

  private BluetoothManager bluetoothManager;
  private BluetoothAdapter bluetoothAdapter;
  private HashMap<String, FilteredIBeacon> beacons;

  private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
    @Override
    public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
      IBeacon iBeacon = IBeacon.fromScanData(scanRecord, rssi);

      if (!isLeBeacon(iBeacon)) {
        return;
      }

      if (iBeacon.getMajor() == 0 && iBeacon.getMinor() == 0) {
        Log.d(TAG, "Found one of our beacons but it has not majorminor configured");
        return;
      }

      Log.d(TAG, "Bluetooth found: "
          + device.getName()
          + LOG_SEPARATOR
          + iBeacon.getProximityUuid()
          + LOG_SEPARATOR
          + iBeacon.getMajor()
          + ':'
          + iBeacon.getMinor()
          + LOG_SEPARATOR
          + iBeacon.getTxPower()
          + LOG_SEPARATOR
          + rssi);

      if (beacons.get(device.getAddress()) == null) {
        FilteredIBeacon beacon = new FilteredIBeacon(iBeacon);
        beacons.put(device.getAddress(), beacon);
      } else {
        beacons.get(device.getAddress()).addAdvertisement(iBeacon);
      }
    }
  };

  public BeaconScanService() {
    super(SERVICE_NAME);
  }

  public static void setScannerAlarm(Context context, boolean foreground) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
      return;
    }

    Intent i = new Intent(context, BeaconScanService.class);
    PendingIntent pi = PendingIntent.getService(context, 0, i, 0);

    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    alarmManager.cancel(pi);

    int interval = foreground ? INTERVAL_FOREGROUND : INTERVAL_BACKGROUND;
    alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), interval, pi);
  }

  @Override public void onCreate() {
    super.onCreate();
    ((LeexplorerApplication) getApplicationContext()).inject(this);
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    Log.d(TAG, "Intent received");

    setBluetoothAdapter();

    if (!isBluetoothAdapterHealthy()) {
      Log.e(TAG, "bluetoothadapter null ?");
      return;
    }

    bluetoothAdapter.startLeScan(leScanCallback);

    beacons = new HashMap<>();

    SystemClock.sleep(SCAN_PERIOD);

    endSearch();
    broadcastBeacons();
    sendNotification();
  }

  private void setBluetoothAdapter() {
    if (bluetoothAdapter == null) {
      bluetoothManager =
          (BluetoothManager) getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
      bluetoothAdapter = bluetoothManager.getAdapter();
    }
  }

  private void endSearch() {
    if (isBluetoothAdapterHealthy()) {
      try {
        bluetoothAdapter.stopLeScan(leScanCallback);
        Log.d(TAG, "search finished");

        for (FilteredIBeacon beacon : beacons.values()) {
          Log.d(TAG, beacon.getMajorminor() + " distance: " + beacon.getDistance());
        }
      } catch (NullPointerException e) {
        eventReporter.logException(e);
      }
    }
  }

  private boolean isBluetoothAdapterHealthy() {
    return bluetoothAdapter != null && bluetoothAdapter.getState() == BluetoothAdapter.STATE_ON;
  }

  private void broadcastBeacons() {
    bus.post(new BeaconsScanResultEvent(new ArrayList<>(beacons.values())));
  }

  private void sendNotification() {
    Gallery gallery = unseenGallery();
    if (gallery == null) {
      return;
    }

    gallery.setWasSeen(true);
    gallery.save();

    eventReporter.galleryDiscovered(gallery);

    Resources resources = getResources();

    TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

    stackBuilder.addNextIntent(new Intent(this, GalleryListActivity.class));

    Intent galleryIntent = new Intent(this, GalleryActivity.class);
    galleryIntent.putExtra(GalleryActivity.GALLERY_KEY, gallery);
    stackBuilder.addNextIntent(galleryIntent);

    Intent artworkListIntent = new Intent(this, ArtworkListActivity.class);
    artworkListIntent.putExtra(ArtworkListActivity.EXTRA_GALLERY, gallery);
    artworkListIntent.putExtra(ArtworkListActivity.EXTRA_FROM_NOTIFICATION, true);
    stackBuilder.addNextIntent(artworkListIntent);

    PendingIntent pi = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);

    Notification notification = new NotificationCompat.Builder(this).setTicker(
        resources.getString(R.string.beacon_notification_title))
        .setSmallIcon(R.drawable.ic_stat_artwork)
        .setContentTitle(resources.getString(R.string.beacon_notification_title))
        .setContentText(resources.getString(R.string.beacon_notification_text, gallery.getName()))
        .setContentIntent(pi)
        .setAutoCancel(true)
        .build();

    Intent notificationIntent = new Intent(ACTION_SHOW_NOTIFICATION);
    notificationIntent.putExtra("REQUEST_CODE", 0);
    notificationIntent.putExtra("NOTIFICATION", notification);

    sendOrderedBroadcast(notificationIntent, PERM_PRIVATE, null, null, Activity.RESULT_OK, null,
        null);
  }

  private Gallery unseenGallery() {
    Gallery gallery = null;
    for (FilteredIBeacon beacon : beacons.values()) {
      gallery = galleryFromBeacon(beacon);

      if (gallery != null) {
        break;
      }
    }

    return gallery;
  }

  private Gallery galleryFromBeacon(FilteredIBeacon beacon) {
    String galleryId = null;
    com.leexplorer.app.models.Artwork artwork =
        com.leexplorer.app.models.Artwork.findByMajorminor(beacon.getMajorminor());

    if (artwork == null) {
      Artwork apiAw = null;

      try {
        apiAw = client.getService().getArtwork(beacon.getMajor() + ":" + beacon.getMinor());
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
        apiGallery = client.getService().getGallery(galleryId);
      } catch (Exception e) {
        e.printStackTrace();
      }

      if (apiGallery != null) {
        gallery = Gallery.fromApiModel(apiGallery);
        gallery.save();
      }
    } else if (gallery.isWasSeen()) {
      return null;
    }

    return gallery;
  }

  private boolean isLeBeacon(IBeacon iBeacon) {
    return AppConstants.LE_UUID.contentEquals(iBeacon.getProximityUuid());
  }
}
