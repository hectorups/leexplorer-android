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
import android.view.KeyEvent;
import android.view.Window;
import com.leexplorer.app.R;
import com.leexplorer.app.core.EventReporter;
import com.leexplorer.app.core.LeexplorerApplication;
import com.leexplorer.app.events.BuildKilledEvent;
import com.leexplorer.app.events.LoadArtworksEvent;
import com.leexplorer.app.events.LoadMapEvent;
import com.leexplorer.app.events.LoadingEvent;
import com.leexplorer.app.events.NetworkErrorEvent;
import com.leexplorer.app.events.VolumeChangeEvent;
import com.leexplorer.app.fragments.GalleryFragment;
import com.leexplorer.app.services.BeaconScanService;
import com.leexplorer.app.views.CroutonCustomView;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import javax.inject.Inject;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class BaseActivity extends ActionBarActivity {
  public static final String TAG = "com.leexplorer.activities.baseactivity";
  // This Receiver is ON when the activity is displaying. When on it catches the notification
  // before NotificationReceiver does and cancels it.

  @Inject Bus bus;
  @Inject EventReporter eventReporter;

  private final EventHandler eventhandler = new EventHandler();

  private BroadcastReceiver onShowNotification = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      Log.i(TAG, "canceling notification");
      setResultCode(Activity.RESULT_CANCELED);
    }
  };
  private int processesLoading = 0;

  @Override
  protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(new CalligraphyContextWrapper(newBase));
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
    ((LeexplorerApplication) getApplication()).inject(this);
  }

  @Override protected void onDestroy() {
    Crouton.cancelAllCroutons();
    eventReporter.flush();
    super.onDestroy();
  }

  public void onProgressLoading(boolean loading) {
    onLoading(loading);
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
    bus.register(eventhandler);

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
    bus.unregister(eventhandler);

    unregisterReceiver(onShowNotification);

    BeaconScanService.setScannerAlarm(this, false);
  }

  public boolean isTabletMode() {
    return getResources().getBoolean(R.bool.isTablet);
  }

  private class EventHandler {
    @Subscribe public void onLoading(LoadingEvent event) {
      onProgressLoading(event.isLoading());
    }

    @Subscribe public void onLoadMap(LoadMapEvent event) {
      Intent intent = new Intent(Intent.ACTION_VIEW);
      Uri uri = Uri.parse("geo:0,0+?q=" + event.getAddress());
      intent.setData(uri);
      try {
        startActivity(intent);
      } catch (ActivityNotFoundException e) {
        Log.d(TAG, "Error opening maps" + e.getMessage());
      }
    }

    @Subscribe public void onLoadArtworks(LoadArtworksEvent event) {
      FragmentManager fm = getSupportFragmentManager();
      GalleryFragment fragment = (GalleryFragment) fm.findFragmentById(R.id.flGalleryDetailView);

      if (fragment == null) {
        return;
      }
      Intent i = new Intent(BaseActivity.this, ArtworkListActivity.class);
      i.putExtra(ArtworkListActivity.EXTRA_GALLERY, event.getGallery());
      startActivity(i);
    }

    @Subscribe public void onBuildKilled(BuildKilledEvent event) {
      new CroutonCustomView(BaseActivity.this, R.string.error_app_too_old).show();
    }

    @Subscribe public void onNetworkError(NetworkErrorEvent event) {
      int messageId;
      if (((LeexplorerApplication) getApplication()).isOnline()) {
        messageId = R.string.error_problem_with_connection;
      } else {
        messageId = R.string.error_no_connection;
      }
      CroutonCustomView.cancelAllCroutons();
      new CroutonCustomView(BaseActivity.this, messageId, 4000).show();
    }
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
      bus.post(new VolumeChangeEvent(false));
      return true;
    } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
      bus.post(new VolumeChangeEvent(true));
      return true;
    }

    return super.onKeyDown(keyCode, event);
  }
}
