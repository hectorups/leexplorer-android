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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import com.leexplorer.app.R;
import com.leexplorer.app.core.EventReporter;
import com.leexplorer.app.core.LeexplorerApplication;
import com.leexplorer.app.events.BuildKilledEvent;
import com.leexplorer.app.events.ConfirmDialogResultEvent;
import com.leexplorer.app.events.LoadMapEvent;
import com.leexplorer.app.events.LoadingEvent;
import com.leexplorer.app.events.MainLoadingIndicator;
import com.leexplorer.app.events.NetworkErrorEvent;
import com.leexplorer.app.events.ShareEvent;
import com.leexplorer.app.events.VolumeChangeEvent;
import com.leexplorer.app.events.artworks.LoadArtworksEvent;
import com.leexplorer.app.events.autoplay.AutoPlayReadyToPlayEvent;
import com.leexplorer.app.events.autoplay.AutoPlayStatusEvent;
import com.leexplorer.app.fragments.ConfirmDialogFragment;
import com.leexplorer.app.fragments.GalleryFragment;
import com.leexplorer.app.services.AutoPlayService;
import com.leexplorer.app.services.BeaconScanService;
import com.leexplorer.app.util.ShareManager;
import com.leexplorer.app.util.Tint;
import com.leexplorer.app.views.CroutonCustomView;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import javax.inject.Inject;

public abstract class BaseActivity extends ActionBarActivity {
  public static final String TAG = "com.leexplorer.activities.baseactivity";
  public static final String CONFIRM_TAG = TAG + "_confirm";
  // This Receiver is ON when the activity is displaying. When on it catches the notification
  // before NotificationReceiver does and cancels it.

  @Inject Bus bus;
  @Inject EventReporter eventReporter;
  @Inject ShareManager shareManager;
  private SmoothProgressBar progressBar;

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
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
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
      progressBar.setVisibility(View.GONE);
      progressBar.progressiveStop();
    } else {
      progressBar.setVisibility(View.VISIBLE);
      progressBar.progressiveStart();
    }
  }

  public void setupActionBar() {
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    progressBar = (SmoothProgressBar) findViewById(R.id.pbToolbar);
    progressBar.bringToFront();
    if (toolbar != null) {
      setSupportActionBar(toolbar);
      getSupportActionBar().setHomeButtonEnabled(true);
      if (showHomeButton()) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(
            Tint.getTintedDrawable(getResources(), R.drawable.ic_action_home, R.color.le_blue));
      }
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    bus.register(eventhandler);

    AutoPlayService.checkAutoplayStatus(this);

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

    BeaconScanService.setScannerAlarm(this, BeaconScanService.Mode.BACKGROUND);
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
      CroutonCustomView.makeError(BaseActivity.this, R.string.error_app_too_old).show();
    }

    @Subscribe public void onNetworkError(NetworkErrorEvent event) {
      int messageId = R.string.error_no_connection;
      if (((LeexplorerApplication) getApplication()).isOnline()) {
        return;
      }
      CroutonCustomView.cancelAllCroutons();
      CroutonCustomView.makeError(BaseActivity.this, messageId).show();
    }

    @Subscribe public void onCheckAutoplayStatusEvent(AutoPlayStatusEvent event) {
      if (event.getStatus() == AutoPlayService.Status.OFF) {
        BeaconScanService.setScannerAlarm(BaseActivity.this, BeaconScanService.Mode.FOREGROUND);
      } else {
        BeaconScanService.setScannerAlarm(BaseActivity.this, BeaconScanService.Mode.AUTOPLAY);
      }
    }

    @Subscribe public void onMainLoadingIndicator(MainLoadingIndicator event) {
      FrameLayout view = (FrameLayout) findViewById(R.id.mainLoadingIndicator);
      if (view == null) {
        return;
      }

      if (event.isLoading()) {
        view.setVisibility(View.VISIBLE);
      } else {
        view.setVisibility(View.GONE);
      }
    }

    @Subscribe public void onShareContent(ShareEvent event) {
      eventReporter.itemShared(event.getType(), event.getTitle());
      Intent intent = shareManager.shareIntent(event);
      startActivity(intent);
    }

    @Subscribe public void onConfirmResult(ConfirmDialogResultEvent event) {
      if (event.getCaller().contentEquals(CONFIRM_TAG)) {
        CroutonCustomView crouton;
        Intent i = new Intent(BaseActivity.this, AutoPlayService.class);
        if (event.getResult()) {
          crouton = CroutonCustomView.make(BaseActivity.this, R.string.autoplay_artwork_will_play);
          crouton.setDuration(4000);
          i.putExtra(AutoPlayService.EXTRA_ACTION, AutoPlayService.ACTION_CONFIRM);
        } else {
          crouton = CroutonCustomView.make(BaseActivity.this, R.string.autoplay_audio_skipped);
          i.putExtra(AutoPlayService.EXTRA_ACTION, AutoPlayService.ACTION_SKIP);
        }

        crouton.setResourceImageId(R.drawable.ic_autoplay_white);
        crouton.show();
        BaseActivity.this.startService(i);
      }
    }

    @Subscribe public void onAutoplayReadyToPlay(AutoPlayReadyToPlayEvent event) {
      ConfirmDialogFragment confirmDialogFragment = ConfirmDialogFragment.newInstance(CONFIRM_TAG,
          getResources().getString(R.string.autoplay_confirm_title),
          getResources().getString(R.string.autoplay_confirm_text, event.getArtwork().getName()),
          getResources().getString(R.string.autoplay_confirm_ok),
          getResources().getString(R.string.autoplay_confirm_skip));

      confirmDialogFragment.setCancelable(false);
      confirmDialogFragment.show(getSupportFragmentManager(), ConfirmDialogFragment.TAG);
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

  public boolean showHomeButton() {
    return false;
  }
}
