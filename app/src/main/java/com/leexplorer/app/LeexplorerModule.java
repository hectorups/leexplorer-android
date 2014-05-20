package com.leexplorer.app;

/**
 * Created by hectormonserrate on 10/05/14.
 */

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
import com.leexplorer.app.util.AppConstants;
import com.leexplorer.app.util.offline.FileDownloader;
import com.leexplorer.app.util.offline.ImageSourcePicker;
import com.squareup.okhttp.ConnectionPool;
import com.squareup.okhttp.HttpResponseCache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.otto.Bus;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import com.squareup.pollexor.Thumbor;
import dagger.Module;
import dagger.Provides;
import java.io.File;
import java.io.IOException;
import java.net.ResponseCache;
import java.util.concurrent.TimeUnit;
import javax.inject.Singleton;

@Module(
    injects = {
        GalleryFragment.class, GalleryListFragment.class, ArtworkFragment.class,
        ArtworkListFragment.class, BeaconScanService.class, ArtworkAdapter.class,
        GalleryAdapter.class, GalleryPagerAdapter.class, GalleryInfoAdapter.class,
        GalleryMapFragment.class, GalleryDownloaderService.class, ArtworkActivity.class,
        GalleryActivity.class, GalleryListActivity.class, ArtworkListActivity.class
    },
    library = true)
public class LeexplorerModule {

  private final LeexplorerApplication application;

  public LeexplorerModule(LeexplorerApplication application) {
    this.application = application;
  }

  @Provides @Singleton HttpResponseCache provideResponseCache() {
    try {
      HttpResponseCache httpResponseCache =
          new HttpResponseCache(new File(application.getCacheDir(), "http"),
              AppConstants.DISK_HTTP_CACHE_MAX_SIZE_BYTE);
      ResponseCache.setDefault(httpResponseCache);
      return httpResponseCache;
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

  @Provides @Singleton OkHttpClient providesOkHttpClient( HttpResponseCache responseCache) {
    OkHttpClient client = new OkHttpClient();
    client.setConnectTimeout(5, TimeUnit.SECONDS);
    client.setReadTimeout(30, TimeUnit.SECONDS);

    client.setResponseCache(responseCache);
    client.setConnectionPool(
        new ConnectionPool(AppConstants.CONNECTION_POOL_JSON, AppConstants.KEEP_ALIVE_DURATION_MS));

    return client;
  }

  @Provides @Singleton Client provideLeexplorerClient(OkHttpClient client) {
    return new Client(client);
  }

  @Provides @Singleton ImageSourcePicker provideImageSourcePicker(Picasso picasso,
      Thumbor thumbor) {
    return new ImageSourcePicker(application, picasso, thumbor);
  }

  @Provides @Singleton FileDownloader privideFileDownloader(OkHttpClient client) {
    return new FileDownloader(client);
  }

  @Provides @Singleton Picasso providePicasso(LeexplorerApplication application,
      OkHttpClient client) {
    Picasso.Builder builder = new Picasso.Builder(application.getApplicationContext());
    OkHttpDownloader downloader = new OkHttpDownloader(client);
    builder.downloader(downloader);
    return builder.build();
  }

  @Provides @Singleton Thumbor provideThumbor() {
    return Thumbor.create(AppConstants.SERVER_THUMBOR_URL);
  }
}

