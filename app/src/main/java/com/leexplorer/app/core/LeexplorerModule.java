package com.leexplorer.app.core;

/**
 * Created by hectormonserrate on 10/05/14.
 */

import android.net.Uri;
import android.util.Log;
import com.leexplorer.app.activities.ArtworkActivity;
import com.leexplorer.app.activities.ArtworkListActivity;
import com.leexplorer.app.activities.GalleryActivity;
import com.leexplorer.app.activities.GalleryListActivity;
import com.leexplorer.app.adapters.ArtworkAdapter;
import com.leexplorer.app.adapters.GalleryAdapter;
import com.leexplorer.app.adapters.GalleryInfoAdapter;
import com.leexplorer.app.adapters.GalleryPagerAdapter;
import com.leexplorer.app.api.Client;
import com.leexplorer.app.fragments.ArtworkFragment;
import com.leexplorer.app.fragments.ArtworkListFragment;
import com.leexplorer.app.fragments.GalleryFragment;
import com.leexplorer.app.fragments.GalleryListFragment;
import com.leexplorer.app.fragments.GalleryMapFragment;
import com.leexplorer.app.services.BeaconScanService;
import com.leexplorer.app.services.GalleryDownloaderService;
import com.leexplorer.app.services.MediaPlayerService;
import com.leexplorer.app.util.offline.FileDownloader;
import com.leexplorer.app.util.offline.ImageSourcePicker;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;
import com.squareup.otto.Bus;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import com.squareup.pollexor.Thumbor;
import dagger.Module;
import dagger.Provides;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.inject.Singleton;

@Module(
    injects = {
        GalleryFragment.class, GalleryListFragment.class, ArtworkFragment.class,
        ArtworkListFragment.class, BeaconScanService.class, ArtworkAdapter.class,
        GalleryAdapter.class, GalleryPagerAdapter.class, GalleryInfoAdapter.class,
        GalleryMapFragment.class, GalleryDownloaderService.class, ArtworkActivity.class,
        GalleryActivity.class, GalleryListActivity.class, ArtworkListActivity.class,
        MediaPlayerService.class
    },
    library = true)
public class LeexplorerModule {

  private final LeexplorerApplication application;

  public LeexplorerModule(LeexplorerApplication application) {
    this.application = application;
  }

  @Provides @Singleton Cache provideCache() {
    try {
      Cache responseCache =
          new Cache(new File(application.getCacheDir(), "okhttp"), 15 * 1024 * 1024);
      return responseCache;
    } catch (IOException e) {
      return null;
    }
  }

  @Provides @Singleton Bus provideBus() {
    return new AndroidBus();
  }

  @Provides @Singleton LeexplorerApplication providesLeexplorerApplicationContext() {
    return application;
  }

  @Provides @Singleton OkHttpClient providesOkHttpClient(Cache cache) {
    OkHttpClient client = new OkHttpClient();
    client.setConnectTimeout(5, TimeUnit.SECONDS);
    client.setReadTimeout(30, TimeUnit.SECONDS);
    client.setCache(cache);

    return client;
  }

  @Provides @Singleton OkUrlFactory providesOkUrlfactory(OkHttpClient client) {
    return new OkUrlFactory(client);
  }

  @Provides @Singleton Client provideLeexplorerClient(OkHttpClient client) {
    return new Client(client);
  }

  @Provides @Singleton ImageSourcePicker provideImageSourcePicker(Picasso picasso,
      Thumbor thumbor) {
    return new ImageSourcePicker(application, picasso, thumbor);
  }

  @Provides @Singleton FileDownloader privideFileDownloader(OkUrlFactory factory) {
    return new FileDownloader(factory);
  }

  @Provides @Singleton Picasso providePicasso(LeexplorerApplication application,
      OkHttpClient client) {
    Picasso.Builder builder = new Picasso.Builder(application.getApplicationContext());
    OkHttpDownloader downloader = new OkHttpDownloader(client);
    builder.downloader(downloader).listener(new Picasso.Listener() {
      @Override public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
        Log.d("DottieModule", "url: " + uri.toString() + " exception: " + exception);
      }
    });
    return builder.build();
  }

  @Provides @Singleton Thumbor provideThumbor() {
    return Thumbor.create(AppConstants.SERVER_THUMBOR_URL);
  }

  @Provides @Singleton EventReporter provideEventReported(LeexplorerApplication application) {
    return new EventReporter(application);
  }
}

