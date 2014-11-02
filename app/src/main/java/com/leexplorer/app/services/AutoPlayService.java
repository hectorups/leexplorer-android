package com.leexplorer.app.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.leexplorer.app.R;
import com.leexplorer.app.activities.ArtworkListActivity;
import com.leexplorer.app.core.AppConstants;
import com.leexplorer.app.events.AudioCompleteEvent;
import com.leexplorer.app.events.AudioProgressEvent;
import com.leexplorer.app.events.AudioStartedEvent;
import com.leexplorer.app.events.BeaconsScanResultEvent;
import com.leexplorer.app.events.autoplay.AutoPlayAudioStarted;
import com.leexplorer.app.models.Artwork;
import com.leexplorer.app.models.AutoPlay;
import com.leexplorer.app.models.FilteredIBeacon;
import com.leexplorer.app.models.Gallery;
import com.squareup.otto.Subscribe;
import java.util.Collections;
import java.util.List;

public class AutoPlayService extends BaseService {
  public static final String TAG = "com.leexplorer.services.AutoPlayService";
  private static final int NOTIFICATION_ID = 12;
  public static final String EXTRA_ACTION = "com.leexplorer.autoplayservice.action";
  public static final int ACTION_START = 1;
  public static final int ACTION_STOP = 2;
  public static final String EXTRA_GALLERY = "gallery";
  public static final String EXTRA_ARTWORKS = "artworks";

  private AutoPlay autoPlay;

  private Looper serviceLooper;
  private ServiceHandler serviceHandler;

  protected final class ServiceHandler extends Handler {
    public ServiceHandler(Looper looper) {
      super(looper);
    }

    @Override
    public void handleMessage(Message msg) {
      onHandleIntent((Intent) msg.obj);
    }
  }

  @Override
  public void onCreate() {
    super.onCreate();

    HandlerThread thread = new HandlerThread(TAG);
    thread.start();

    serviceLooper = thread.getLooper();
    serviceHandler = new ServiceHandler(serviceLooper);
  }

  @Override public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    super.onStartCommand(intent, flags, startId);
    Message message = serviceHandler.obtainMessage();
    message.arg1 = startId;
    message.obj = intent;
    serviceHandler.sendMessage(message);
    return START_STICKY;
  }

  protected void onHandleIntent(Intent intent) {
    Log.d(TAG, "Intent received");
    if (intent == null) {
      return;
    }

    switch (intent.getIntExtra(EXTRA_ACTION, 0)) {
      case ACTION_START:
        Gallery gallery = intent.getParcelableExtra(EXTRA_GALLERY);
        List<Artwork> artworks = intent.getParcelableArrayListExtra(EXTRA_ARTWORKS);
        start(gallery, artworks);
        break;
      case ACTION_STOP:
        stop();
        break;
      default:
        return;
    }
  }

  public void start(Gallery gallery, List<Artwork> artworks) {
    Log.d(TAG, "START AUTOPLAY IN SERVICE, autoplaying? " + (artworks == null ? "no" : "yes"));
    autoPlay = new AutoPlay(gallery, artworks);
    prepareNotification();
  }

  public void stop() {
    Log.d(TAG, "Stop autoplaying");
    clear();
  }

  private void clear() {
    autoPlay = null;
    stopForeground(true);
  }

  private void prepareNotification() {
    Resources resources = getResources();
    Intent intent = new Intent(this, ArtworkListActivity.class);
    intent.putExtra(ArtworkListActivity.EXTRA_GALLERY, autoPlay.getGallery());
    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

    PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, intent,
        PendingIntent.FLAG_UPDATE_CURRENT);

    Notification notification =
        new NotificationCompat.Builder(getApplicationContext()).setSmallIcon(
            R.drawable.ic_stat_artwork)
            .setContentTitle(resources.getString(R.string.autoplay_notification_title,
                autoPlay.getGallery().getName()))
            .setContentText(resources.getString(R.string.autoplay_notification_text))
            .setContentIntent(pi)
            .build();

    notification.flags |= Notification.FLAG_ONGOING_EVENT;
    startForeground(NOTIFICATION_ID, notification);
  }

  @Subscribe public void onBeaconsScanResult(BeaconsScanResultEvent event) {
    List<FilteredIBeacon> newBeacons = event.getBeacons();
    Log.d(TAG, "Beacons detected: " + newBeacons.size());

    if (autoPlay == null || autoPlay.getCurrentlyPlaying() != null) {
      return;
    }

    List<Artwork> artworks = autoPlay.getArtworksPlayList();
    // BeaconArtworkUpdater.updateDistances(artworks, newBeacons);
    Collections.sort(artworks, new Artwork.ArtworkComparable());

    for (Artwork artwork : artworks) {
      if (artwork.getDistance() < AppConstants.MIN_METRES_FOR_AUTOPLAY
          && !autoPlay.wasArtworkPlayed(artwork)
          && artwork.getAudioUrl() != null) {

        // Play!!
        Log.d(TAG, "Ready to play " + artwork.getName());
        playAudio(artwork);
        break;
      }
    }
  }

  public void playAudio(Artwork artwork) {
    bus.post(new AutoPlayAudioStarted(artwork));
    autoPlay.setAsPlayingArtwork(artwork);
    Intent i = new Intent(getApplicationContext(), MediaPlayerService.class);
    i.putExtra(MediaPlayerService.ARTWORK, artwork);
    i.putExtra(MediaPlayerService.ACTION, MediaPlayerService.ACTION_PLAY);
    getApplicationContext().startService(i);
  }

  @Subscribe public void audioProgressReceiver(AudioProgressEvent event) {
    Artwork playingArtwork = event.getArtwork();
    if (autoPlay != null && !autoPlay.getCurrentlyPlaying().equals(playingArtwork)) {
      autoPlay.setAsPlayingArtwork(playingArtwork);
    }
  }

  @Subscribe public void audioComplete(AudioCompleteEvent event) {
    Log.d(TAG, "Audio completed " + event.getArtwork().getName());
    if (autoPlay != null) {

      if (autoPlay.getCurrentlyPlaying().equals(event.getArtwork())) {
        autoPlay.resetPlayingArtwork();
      }

      if (autoPlay.isFinished()) {
        clear();
      }
    }
  }

  @Subscribe public void audioFailed(AudioStartedEvent event) {
    Log.d(TAG, "Audio failed");
    if (autoPlay != null) {
      autoPlay.resetPlayingArtwork();
    }
  }
}
