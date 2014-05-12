package com.leexplorer.app.activities;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Window;
import com.leexplorer.app.R;
import com.leexplorer.app.fragments.GalleryFragment;
import com.leexplorer.app.models.Gallery;
import com.leexplorer.app.services.BeaconScanService;

import static com.leexplorer.app.util.AppConstants.APP_NAME;

/**
 * Created by hectormonserrate on 20/02/14.
 */
public class BaseActivity extends ActionBarActivity {
  public static final String TAG = "com.leexplorer.activities.baseactivity";
  // This Receiver is ON when the activity is displaying. When on it catches the notification
  // before NotificationReceiver does and cancels it.
  private BroadcastReceiver onShowNotification = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      Log.i(TAG, "canceling notification");
      setResultCode(Activity.RESULT_CANCELED);
    }
  };
  private int processesLoading = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
  }

  public void onLoading(boolean loading) {
    processesLoading += loading ? 1 : -1;
    if (processesLoading < 1) {
      processesLoading = 0;
      setProgressBarIndeterminateVisibility(false);
    } else {
      setProgressBarIndeterminateVisibility(true);
    }
  }

  @Override
  public void onResume() {
    super.onResume();

    BeaconScanService.setScannerAlarm(this, true);

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

    BeaconScanService.setScannerAlarm(this, false);
  }

  public void loadMap(String address) {
    Intent intent = new Intent(Intent.ACTION_VIEW);
    Uri uri = Uri.parse("geo:0,0+?q=" + address);
    intent.setData(uri);
    try {
      startActivity(intent);
    } catch (ActivityNotFoundException e) {
      Log.d(APP_NAME, "Error opening maps" + e.getMessage());
    }
  }

  public void loadArtworks(Gallery gallery) {

    FragmentManager fm = getSupportFragmentManager();
    GalleryFragment fragment = (GalleryFragment) fm.findFragmentById(R.id.flGalleryDetailView);

    if (fragment == null) {
      return;
    }
    Intent i = new Intent(this, ArtworkListActivity.class);
    i.putExtra(ArtworkListActivity.EXTRA_GALLERY, gallery);
    startActivity(i);
  }

  public boolean isTabletMode() {
    if (findViewById(R.id.flGalleryDetailView) != null) {
      return true;
    }
    return false;
  }
}
