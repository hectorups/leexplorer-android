package com.leexplorer.app.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.leexplorer.app.LeexplorerApplication;
import com.leexplorer.app.R;
import com.leexplorer.app.activities.GalleryActivity;
import com.leexplorer.app.api.Client;
import com.leexplorer.app.models.Artwork;
import com.leexplorer.app.models.Gallery;
import com.leexplorer.app.util.FileDownloader;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import javax.inject.Inject;
import rx.Observer;

/**
 * Created by hectormonserrate on 13/05/14.
 */
public class GalleryDownloaderService extends IntentService {

  private static final String TAG = "GalleryDownloderService";
  private static final String EXTRA_GALLERY = "gallery";
  private static final String APP_FOLDER = "leexplorer";
  private static final int NOTIFICATION_ID = 15;

  @Inject FileDownloader fileDownloader;
  @Inject Client client;

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

    Gallery gallery = intent.getParcelableExtra(EXTRA_GALLERY);

    checkAppDirectory();

    prepareNotification(gallery);

    client.getArtworksData(gallery.getGalleryId()).subscribe(new Observer<ArrayList<Artwork>>() {
      @Override public void onCompleted() {
        stopForeground(true);
      }

      @Override public void onError(Throwable throwable) {
        stopForeground(true);
      }

      @Override public void onNext(ArrayList<Artwork> artworks) {
        try {
          for (Artwork artwork : artworks) {
            saveArtwork(artwork);
          }
        } catch (IOException e) {
          e.printStackTrace();
          onError(e);
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

  private void saveFile(Artwork artwork, String url) throws IOException {
    fileDownloader.downloadToFile(getFileName(artwork, url), new URL(url));
  }

  private File appDirectory() {
    return new File(Environment.getExternalStorageDirectory() + "/" + APP_FOLDER);
  }

  private void checkAppDirectory() {
    File testDirectory = appDirectory();
    if (!testDirectory.exists()) {
      testDirectory.mkdir();
    }
  }

  private String getFileName(Artwork artwork, String url) {
    File testDirectory = appDirectory();
    File theFile = new File(url);
    return testDirectory.toString() + "/" + artwork.getId() + "-" + theFile.getName();
  }

  private void prepareNotification(Gallery gallery) {
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
}
