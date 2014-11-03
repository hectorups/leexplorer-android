package com.leexplorer.app.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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
import com.leexplorer.app.events.autoplay.AutoPlayAudioFinishedEvent;
import com.leexplorer.app.events.autoplay.AutoPlayAudioStartedEvent;
import com.leexplorer.app.events.autoplay.AutoPlayStatusEvent;
import com.leexplorer.app.models.Artwork;
import com.leexplorer.app.models.AutoPlay;
import com.leexplorer.app.models.FilteredIBeacon;
import com.leexplorer.app.models.Gallery;
import com.leexplorer.app.util.offline.ImageSourcePicker;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

public class AutoPlayService extends BaseService {
  public static final String TAG = "com.leexplorer.services.AutoPlayService";
  private static final int NOTIFICATION_ID = 12;
  public static final String EXTRA_ACTION = "com.leexplorer.services.autoplayservice.action";
  public static final String CANCEL_BROADCAST =
      "com.leexplorer.services.AutoPlayService.CANCEL_AUTOPLAY";
  public static final int ACTION_START = 1;
  public static final int ACTION_STOP = 2;
  public static final int ACTION_CHECK_STATUS = 3;
  public static final String EXTRA_GALLERY = "gallery";
  public static final String EXTRA_ARTWORKS = "artworks";

  public enum Status {
    RUNNING, PLAYING, OFF
  }

  @Inject ImageSourcePicker imageSourcePicker;
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

    Gallery gallery;
    switch (intent.getIntExtra(EXTRA_ACTION, 0)) {
      case ACTION_START:
        gallery = intent.getParcelableExtra(EXTRA_GALLERY);
        List<Artwork> artworks = intent.getParcelableArrayListExtra(EXTRA_ARTWORKS);
        start(gallery, artworks);
        break;
      case ACTION_STOP:
        gallery = intent.getParcelableExtra(EXTRA_GALLERY);
        if (autoPlay != null && autoPlay.getGallery().equals(gallery)) {
          stop();
        }
        break;
      case ACTION_CHECK_STATUS:
        Status status;
        Gallery currentGallery = null;
        if (autoPlay == null) {
          status = Status.OFF;
        } else if (autoPlay.getCurrentlyPlaying() != null) {
          status = Status.PLAYING;
          currentGallery = autoPlay.getGallery();
        } else {
          status = Status.RUNNING;
          currentGallery = autoPlay.getGallery();
        }

        bus.post(new AutoPlayStatusEvent(currentGallery, status));
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
    if (autoPlay != null) {
      bus.post(new AutoPlayAudioFinishedEvent(autoPlay.getGallery()));
    }
    autoPlay = null;
    stopForeground(true);
  }

  private void prepareNotification() {
    final int imageWidth = getResources().getDimensionPixelSize(R.dimen.notification_size);
    final int imageHeight = getResources().getDimensionPixelSize(R.dimen.notification_size);

    Handler mainHandler = new Handler(getMainLooper());
    Runnable getImageRunnable = new Runnable() {
      @Override public void run() {
        imageSourcePicker.getRequestCreator(autoPlay.getGallery().getGalleryId(),
            autoPlay.getGallery().getMainImage(), R.dimen.thumbor_tiny)
            .resize(imageWidth, imageHeight)
            .centerCrop()
            .into(new Target() {
              @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                showNotification(bitmap);
              }

              @Override public void onBitmapFailed(Drawable errorDrawable) {
                if (errorDrawable != null) {
                  eventReporter.logException(errorDrawable.toString());
                } else {
                  eventReporter.logException("Unknown errorDrawable");
                }

                showNotification(null);
              }

              @Override public void onPrepareLoad(Drawable placeHolderDrawable) {
              }
            });
      }
    };

    mainHandler.post(getImageRunnable);
  }

  private PendingIntent galleryPendingIntent() {
    Intent intent = new Intent(this, ArtworkListActivity.class);
    intent.putExtra(ArtworkListActivity.EXTRA_GALLERY, autoPlay.getGallery());
    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
    return PendingIntent.getActivity(getApplicationContext(), 0, intent,
        PendingIntent.FLAG_UPDATE_CURRENT);
  }

  private PendingIntent cancelPendingIntent() {
    Intent intent = new Intent(getApplicationContext(), AutoPlayService.class);
    intent.putExtra(EXTRA_ACTION, ACTION_STOP);
    intent.putExtra(EXTRA_GALLERY, autoPlay.getGallery());
    return PendingIntent.getService(getApplicationContext(), 0, intent,
        PendingIntent.FLAG_UPDATE_CURRENT);
  }

  private void showNotification(Bitmap bitmap) {
    if (autoPlay == null) {
      return;
    }

    Resources resources = getResources();
    String text =
        resources.getString(R.string.autoplay_notification_text, autoPlay.getGallery().getName());

    NotificationCompat.BigTextStyle bigStyle = new NotificationCompat.BigTextStyle();
    bigStyle.bigText(text);

    NotificationCompat.Builder builder =
        new NotificationCompat.Builder(getApplicationContext()).setSmallIcon(
            R.drawable.ic_stat_artwork)
            .setContentTitle(resources.getString(R.string.autoplay_notification_title,
                autoPlay.getGallery().getName()))
            .setContentText(text)
            .addAction(R.drawable.ic_stop_autoplay,
                resources.getString(R.string.autoplay_notification_stop), cancelPendingIntent())
            .setStyle(bigStyle)
            .setContentIntent(galleryPendingIntent());

    if (bitmap != null) {
      builder.setLargeIcon(bitmap);
    }

    Notification notification = builder.build();

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
    bus.post(new AutoPlayAudioStartedEvent(artwork));
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
