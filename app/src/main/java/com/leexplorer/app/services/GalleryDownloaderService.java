package com.leexplorer.app.services;

import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.leexplorer.app.LeexplorerApplication;
import com.leexplorer.app.R;
import com.leexplorer.app.activities.GalleryActivity;
import com.leexplorer.app.api.Client;
import com.leexplorer.app.models.Artwork;
import com.leexplorer.app.models.Gallery;
import com.leexplorer.app.util.offline.FileDownloader;
import com.leexplorer.app.util.offline.FilePathGenerator;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import rx.Observer;

/**
 * Created by hectormonserrate on 13/05/14.
 */
public class GalleryDownloaderService extends IntentService {

  private static final String TAG = "GalleryDownloderService";
  private static final String EXTRA_GALLERY = "gallery";
  private static final int NOTIFICATION_ID = 15;

  public static final String ACTION = "com.leexplorer.gallerydownloaderservice.action";
  public static final String CURRENT_PERCENTAGE =
      "com.leexplorer.mediaplayerservice.current_percentage";
  public static final String GALLERY = "com.leexplorer.mediaplayerservice.gallery";

  @Inject FileDownloader fileDownloader;
  @Inject Client client;

  private Gallery gallery;
  private DownloadProgress downloadProgress;

  public static void callService(Context context, Gallery gallery) {
    Intent intent = new Intent(context, GalleryDownloaderService.class);
    intent.putExtra(EXTRA_GALLERY, gallery);
    context.startService(intent);
  }

  public GalleryDownloaderService() {
    super("gallery-downloader-service");
  }

  @Override public void onCreate() {
    super.onCreate();
    ((LeexplorerApplication) getApplicationContext()).inject(this);
  }

  @Override protected void onHandleIntent(Intent intent) {
    Log.d(TAG, "Intent received");

    gallery = intent.getParcelableExtra(EXTRA_GALLERY);

    FilePathGenerator.checkAppDirectory();

    prepareNotification();

    client.getArtworksData(gallery.getGalleryId()).subscribe(new Observer<ArrayList<Artwork>>() {
      @Override public void onCompleted() {
      }

      @Override public void onError(Throwable throwable) {
        throwable.printStackTrace();
      }

      @Override public void onNext(ArrayList<Artwork> artworks) {
        try {
          int total = totalFiles(artworks);
          downloadProgress = new DownloadProgress(total);

          for (int i = 0; i < total; i++) {
            saveArtwork(artworks.get(i));
          }
        } catch (IOException e) {
          e.printStackTrace();
        } finally {
          stopForeground(true);
          broadcastProgress(100);
        }
      }
    });
  }

  private void saveArtwork(Artwork artwork) throws IOException {
    if (artwork.getImageUrl() != null) {
      saveFile(artwork, artwork.getImageUrl());
    }

    if (artwork.getAudioUrl() != null) {
      saveFile(artwork, artwork.getAudioUrl());
    }
  }

  private int totalFiles(List<Artwork> artworks) {
    int total = 0;

    for (Artwork artwork : artworks) {
      if (artwork.getAudioUrl() != null) {
        total++;
      }

      if (artwork.getImageUrl() != null) {
        total++;
      }
    }

    return total;
  }

  private void saveFile(Artwork artwork, String url) throws IOException {
    downloadProgress.setCurrentFile(downloadProgress.getCurrentFile() + 1);
    fileDownloader.downloadToFile(FilePathGenerator.getFileName(artwork.getGalleryId(), url),
        new URL(url), downloadProgress);
  }

  private void prepareNotification() {
    Resources r = getResources();
    Intent intent = new Intent(this, GalleryActivity.class);
    intent.putExtra(GalleryActivity.GALLERY_KEY, gallery);

    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

    PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, intent,
        PendingIntent.FLAG_UPDATE_CURRENT);

    Notification notification =
        new NotificationCompat.Builder(getApplicationContext()).setSmallIcon(
            R.drawable.ic_stat_download)
            .setContentTitle(r.getString(R.string.download_notification_title))
            .setContentText(r.getString(R.string.download_notification_text, gallery.getName()))
            .setContentIntent(pi)
            .build();

    notification.flags |= Notification.FLAG_ONGOING_EVENT;

    startForeground(NOTIFICATION_ID, notification);
  }

  private void broadcastProgress(int currentPercentage) {
    Intent in = new Intent(ACTION);
    in.putExtra("resultCode", Activity.RESULT_OK);
    in.putExtra(GALLERY, gallery);
    in.putExtra(CURRENT_PERCENTAGE, currentPercentage);

    LocalBroadcastManager.getInstance(this).sendBroadcast(in);
  }

  private class DownloadProgress implements FileDownloader.Callbacks {
    private int currentFile;
    private int totalFiles;

    public DownloadProgress(int totalFiles) {
      currentFile = 0;
      this.totalFiles = totalFiles;
    }

    public int getCurrentFile() {
      return currentFile;
    }

    public void setCurrentFile(int currentFile) {
      this.currentFile = currentFile;
    }

    public void publishContent(int total) {
      int percentage = 0;
      if (currentFile > 0) {
        percentage = Math.abs(currentFile * 100 / totalFiles);
      }
      percentage += parcialPercentage(total);
      Log.d(TAG, "Broadcast: " + String.valueOf(percentage));
      broadcastProgress(percentage);
    }

    private int parcialPercentage(int value) {
      int totalParcialPercentage = Math.abs(100 / totalFiles);
      return Math.abs(value * totalParcialPercentage / 100);
    }
  }
}
