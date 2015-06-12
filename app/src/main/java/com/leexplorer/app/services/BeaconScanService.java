package com.leexplorer.app.services;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.IBinder;
import android.os.RemoteException;
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
import com.leexplorer.app.events.beacon.AltBeaconsScanResultEvent;
import com.leexplorer.app.models.Gallery;
import com.leexplorer.app.util.ble.Majorminor;
import com.squareup.otto.Bus;
import java.util.Collection;
import javax.inject.Inject;
import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

public class BeaconScanService extends Service implements BeaconConsumer {

  public static final String ACTION_SHOW_NOTIFICATION =
      "com.leexplorer.services.beaconscanservice.SHOW_NOTIFICATION";
  public static final String PERM_PRIVATE = "com.leexplorer.beaconscanservice.PRIVATE";
  private static final String TAG = "BeaconScanService";

  @Inject Client client;
  @Inject Bus bus;
  @Inject EventReporter eventReporter;
  @Inject BeaconManager beaconManager;
  private boolean calculatingIfNewBeacons;

  public static void startService(Context context) {
    Intent startServiceIntent = new Intent(context, BeaconScanService.class);
    context.startService(startServiceIntent);
  }

  @Override public IBinder onBind(Intent intent) {
    return null;
  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {
    super.onStartCommand(intent, flags, startId);
    return START_STICKY;
  }

  @Override public void onCreate() {
    super.onCreate();
    ((LeexplorerApplication) getApplicationContext()).getComponent().inject(this);
    beaconManager.bind(this);
  }

  @Override public void onDestroy() {
    beaconManager.unbind(this);
    super.onDestroy();
  }

  @SuppressWarnings("PMD") private void sendNotification(Gallery gallery) {

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

  private Gallery galleryFromBeacon(Beacon beacon) {
    String galleryId = null;
    String beaconMajorMinor = String.valueOf(
        Majorminor.longFromMajorminor(beacon.getId2().toInt(), beacon.getId3().toInt()));
    com.leexplorer.app.models.Artwork artwork =
        com.leexplorer.app.models.Artwork.findByMajorminor(beaconMajorMinor);

    if (artwork == null) {
      Artwork apiAw = null;

      try {
        apiAw = client.getService().getArtwork(beacon.getId2() + ":" + beacon.getId2());
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

  private void onBeaconRanged(Beacon beacon) {
    Gallery gallery = galleryFromBeacon(beacon);
    if (gallery != null && !gallery.isWasSeen()) {
      sendNotification(gallery);
    }
  }

  @Override public void onBeaconServiceConnect() {
    Log.i(TAG, "Beacon Service Connected");

    beaconManager.setRangeNotifier(new RangeNotifier() {
      @Override public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
        Log.v(TAG, region.getUniqueId() + " Beacons found: " + beacons.size());
        bus.post(new AltBeaconsScanResultEvent(beacons));

        if (!calculatingIfNewBeacons) {
          calculatingIfNewBeacons = true;
          for (Beacon beacon : beacons) {
            onBeaconRanged(beacon);
          }
          calculatingIfNewBeacons = false;
        }
      }
    });

    try {
      Region region = new Region("leexplorer", Identifier.parse(AppConstants.LE_UUID), null, null);
      beaconManager.startRangingBeaconsInRegion(region);
    } catch (RemoteException e) {
      Log.e(TAG, e.toString());
    }
  }
}
