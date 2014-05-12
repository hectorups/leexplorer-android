package com.leexplorer.app;

/**
 * Created by hectormonserrate on 10/05/14.
 */

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
import com.leexplorer.app.util.AppConstants;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import com.squareup.pollexor.Thumbor;
import dagger.Module;
import dagger.Provides;
import java.util.concurrent.TimeUnit;
import javax.inject.Singleton;

@Module(
    injects = {
        GalleryFragment.class, GalleryListFragment.class, ArtworkFragment.class,
        ArtworkListFragment.class, BeaconScanService.class, ArtworkAdapter.class,
        GalleryAdapter.class, GalleryPagerAdapter.class, GalleryInfoAdapter.class,
        GalleryMapFragment.class
    },
    library = true)
public class LeexplorerModule {

  private final LeexplorerApplication application;

  public LeexplorerModule(LeexplorerApplication application) {
    this.application = application;
  }

  @Provides @Singleton LeexplorerApplication providesLeexplorerApplicationContext() {
    return application;
  }

  @Provides @Singleton OkHttpClient providesOkHttpClient() {
    OkHttpClient client = new OkHttpClient();
    client.setConnectTimeout(5, TimeUnit.SECONDS);
    client.setReadTimeout(30, TimeUnit.SECONDS);

    return client;
  }

  @Provides @Singleton Client provideLeexplorerClient(OkHttpClient client) {
    return new Client(client);
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

