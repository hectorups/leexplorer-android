package com.leexplorer.app.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.leexplorer.app.LeexplorerApplication;
import com.leexplorer.app.R;
import com.leexplorer.app.activities.ArtworkActivity;
import com.leexplorer.app.events.AudioComplete;
import com.leexplorer.app.events.AudioProgressEvent;
import com.leexplorer.app.models.Artwork;
import com.leexplorer.app.util.offline.AudioSourcePicker;
import com.squareup.otto.Bus;
import java.util.ArrayList;
import javax.inject.Inject;

public class MediaPlayerService extends Service {

  public static final String TOTAL_DURATION = "com.leexplorer.mediaplayerservice.total_duration";
  public static final String CURRENT_DURATION =
      "com.leexplorer.mediaplayerservice.current_duration";
  public static final String ARTWORK = "com.leexplorer.mediaplayerservice.artworks";
  public static final String SEEK_TO_VALUE = "com.leexplorer.mediaplayerservice.seek_to_value";
  public static final String ACTION = "com.leexplorer.mediaplayerservice.action";
  public static final int ACTION_PLAY = 1;
  public static final int ACTION_STOP = 2;
  public static final int ACTION_PAUSE = 3;
  public static final int ACTION_SEEK_TO = 4;
  private static final String LOG = "com.leexplorer.services.mediaplayerservice";
  private static final int STATUS_INTERVAL = 500;
  private static final int NOTIFICATION_ID = 11;
  private static MediaPlayer mediaPlayer;
  private static Artwork artwork;
  private static ArrayList<Artwork> artworks;

  private Looper serviceLooper;
  private ServiceHandler serviceHandler;
  private Handler progressHandler = new Handler();
  @Inject Bus bus;

  @Override
  public void onCreate() {
    super.onCreate();

    ((LeexplorerApplication) getApplication()).inject(this);

    HandlerThread thread = new HandlerThread("MediaPlayerService:WorkerThread");
    thread.start();

    serviceLooper = thread.getLooper();
    serviceHandler = new ServiceHandler(serviceLooper);
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

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  protected void onHandleIntent(Intent intent) {
    Log.d(LOG, "Intent received");
    if (intent == null) {
      return;
    }

    switch (intent.getIntExtra(ACTION, 0)) {
      case ACTION_PLAY:
        //artworks = intent.getParcelableArrayListExtra(ARTWORKS);
        play((Artwork) intent.getParcelableExtra(ARTWORK));
        break;
      case ACTION_STOP:
        stop();
        break;
      case ACTION_PAUSE:
        pause();
        break;
      case ACTION_SEEK_TO:
        seek_to(intent.getIntExtra(SEEK_TO_VALUE, 0));
        break;
      default:
        return;
    }
  }

  private void prepareNotification() {
    Resources r = getResources();
    Intent intent = new Intent(this, ArtworkActivity.class);
    intent.putExtra(ArtworkActivity.EXTRA_ARTWORK, artwork);

    artworks = new ArrayList<>();
    artworks.add(artwork);
    intent.putExtra(ArtworkActivity.EXTRA_ARTWORKS, artworks);
    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

    PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, intent,
        PendingIntent.FLAG_UPDATE_CURRENT);

    Notification notification =
        new NotificationCompat.Builder(getApplicationContext()).setSmallIcon(
            R.drawable.ic_white_play)
            .setContentTitle(r.getString(R.string.audio_notification_title))
            .setContentText(r.getString(R.string.audio_notification_text, artwork.getName()))
            .setContentIntent(pi)
            .build();

    notification.flags |= Notification.FLAG_ONGOING_EVENT;

    startForeground(NOTIFICATION_ID, notification);
  }

  private void stop() {
    progressHandler.removeCallbacks(updateTimeTask);

    if (artwork != null) {
      bus.post(new AudioComplete(artwork));
    }

    if (mediaPlayer != null) {
      mediaPlayer.release();
      mediaPlayer = null;
      stopForeground(true);
    }
  }

  synchronized private void play(final Artwork artwork) {

    if (artwork.getAudioUrl() == null) {
      Log.e(LOG, "I got an artwork to play without an audio file !!!");
      return;
    }

    if (mediaPlayer != null && this.artwork != null && this.artwork.equals(artwork)) {
      mediaPlayer.seekTo(mediaPlayer.getCurrentPosition());
      mediaPlayer.start();
    } else {
      stop();
      this.artwork = artwork;

      Uri audioUri = AudioSourcePicker.getUri(artwork.getGalleryId(), artwork.getAudioUrl());
      mediaPlayer = MediaPlayer.create(getApplicationContext(), audioUri);
      mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mp) {
          stop();
        }
      });
      mediaPlayer.start();
    }

    updateProgress();
    prepareNotification();
  }

  synchronized private void pause() {
    if (mediaPlayer == null) {
      return;
    }

    stopForeground(true);
    mediaPlayer.pause();
  }

  synchronized private void seek_to(int position) {
    if (mediaPlayer == null) {
      return;
    } else if (position >= mediaPlayer.getDuration()) {
      return;
    }

    mediaPlayer.seekTo(position);
  }

  private void updateProgress() {
    progressHandler.postDelayed(updateTimeTask, STATUS_INTERVAL);
  }

  private void broadcastProgress(long totalDuration, long currentDuration) {
    bus.post(new AudioProgressEvent(artwork, totalDuration, currentDuration));
  }

  private final class ServiceHandler extends Handler {
    public ServiceHandler(Looper looper) {
      super(looper);
    }

    @Override
    public void handleMessage(Message msg) {
      onHandleIntent((Intent) msg.obj);
    }
  }

  private Runnable updateTimeTask = new Runnable() {
    public void run() {
      synchronized (MediaPlayerService.this) {
        if (mediaPlayer == null) {
          progressHandler.removeCallbacks(updateTimeTask);
          return;
        }

        try {
          long totalDuration = mediaPlayer.getDuration();
          long currentDuration = mediaPlayer.getCurrentPosition();
          broadcastProgress(totalDuration, currentDuration);
        } catch (Exception e) {
          e.printStackTrace();
        } finally {
          progressHandler.postDelayed(this, STATUS_INTERVAL);
        }
      }
    }
  };
}
