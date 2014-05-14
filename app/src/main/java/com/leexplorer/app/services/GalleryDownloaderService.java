package com.leexplorer.app.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import com.leexplorer.app.LeexplorerApplication;
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

  @Inject FileDownloader fileDownloader;
  @Inject Client client;

  public static void callService(Context context, Gallery gallery){
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

    client.getArtworksData(gallery.getGalleryId()).subscribe(new Observer<ArrayList<Artwork>>() {
      @Override public void onCompleted() {
      }

      @Override public void onError(Throwable throwable) {
      }

      @Override public void onNext(ArrayList<Artwork> artworks) {
        for(Artwork artwork: artworks){
          saveArtwork(artwork);
        }
      }
    });
  }

  private void saveArtwork(Artwork artwork) {
    if (artwork.getImageUrl() != null) {
      saveFile(artwork, artwork.getImageUrl());
    }

    if (artwork.getAudioUrl() != null) {
      saveFile(artwork, artwork.getAudioUrl());
    }
  }

  private void saveFile(Artwork artwork, String url) {
    try {
      fileDownloader.downloadToFile(getFileName(artwork, url), new URL(url));
    } catch (IOException e){
      e.printStackTrace();
    }
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
}
