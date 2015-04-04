package com.leexplorer.app.core;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import com.cloudinary.Cloudinary;
import com.leexplorer.app.activities.ArtworkActivity;
import com.leexplorer.app.activities.ArtworkListActivity;
import com.leexplorer.app.activities.FullScreenImageActivity;
import com.leexplorer.app.activities.GalleryActivity;
import com.leexplorer.app.activities.GalleryListActivity;
import com.leexplorer.app.adapters.ArtworkAdapter;
import com.leexplorer.app.adapters.GalleryAdapter;
import com.leexplorer.app.adapters.GalleryInfoAdapter;
import com.leexplorer.app.adapters.GalleryPagerAdapter;
import com.leexplorer.app.api.Client;
import com.leexplorer.app.api.LeexplorerErrorHandler;
import com.leexplorer.app.api.LeexplorerOkClient;
import com.leexplorer.app.api.LeexplorerRequestInterceptor;
import com.leexplorer.app.fragments.ArtworkFragment;
import com.leexplorer.app.fragments.ArtworkListFragment;
import com.leexplorer.app.fragments.ConfirmDialogFragment;
import com.leexplorer.app.fragments.FacilitiesDialogFragment;
import com.leexplorer.app.fragments.GalleryFragment;
import com.leexplorer.app.fragments.GalleryListFragment;
import com.leexplorer.app.fragments.GalleryMapFragment;
import com.leexplorer.app.models.FilteredIBeacon;
import com.leexplorer.app.services.AutoPlayService;
import com.leexplorer.app.services.BeaconScanService;
import com.leexplorer.app.services.GalleryDownloaderService;
import com.leexplorer.app.services.MediaPlayerService;
import com.leexplorer.app.util.RippleClick;
import com.leexplorer.app.util.ShareManager;
import com.leexplorer.app.util.ble.BluetoothCrashResolver;
import com.leexplorer.app.util.offline.AudioSourcePicker;
import com.leexplorer.app.util.offline.FileDownloader;
import com.leexplorer.app.util.offline.ImageSourcePicker;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;
import com.squareup.otto.Bus;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import dagger.Module;
import dagger.Provides;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.inject.Singleton;
import retrofit.ErrorHandler;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

@Module(
    injects = {
        GalleryFragment.class, GalleryListFragment.class, ArtworkFragment.class,
        ArtworkListFragment.class, BeaconScanService.class, ArtworkAdapter.class,
        GalleryAdapter.class, GalleryPagerAdapter.class, GalleryInfoAdapter.class,
        GalleryMapFragment.class, GalleryDownloaderService.class, ArtworkActivity.class,
        GalleryActivity.class, GalleryListActivity.class, ArtworkListActivity.class,
        MediaPlayerService.class, ConfirmDialogFragment.class, Client.class,
        FullScreenImageActivity.class, FacilitiesDialogFragment.class, AutoPlayService.class
    },
    library = true)
public class LeexplorerModule {

  private final LeexplorerApplication application;

  public LeexplorerModule(LeexplorerApplication application) {
    this.application = application;
  }

  @Provides @Singleton Cache provideCache() {
    Cache responseCache =
        new Cache(new File(application.getCacheDir(), "okhttp"), AppConstants.NETWORK_CACHE);
    return responseCache;
  }

  @Provides @Singleton Bus provideBus() {
    return new AndroidBus();
  }

  @Provides @Singleton RequestInterceptor providesRequestInterceptor() {
    return new LeexplorerRequestInterceptor();
  }

  @Provides @Singleton LeexplorerApplication providesLeexplorerApplicationContext() {
    return application;
  }

  @Provides @Singleton OkHttpClient providesOkHttpClient(Cache cache) {
    OkHttpClient client = new OkHttpClient();
    client.setConnectTimeout(AppConstants.CONNECT_TIMEOUT, TimeUnit.SECONDS);
    client.setReadTimeout(AppConstants.READ_TIMEOUT, TimeUnit.SECONDS);
    client.setCache(cache);

    if (!TextUtils.isEmpty(AppConstants.PROXY)) {
      InetSocketAddress socketAddress =
          InetSocketAddress.createUnresolved(AppConstants.PROXY, AppConstants.PROXY_PORT);
      client.setProxy(new Proxy(Proxy.Type.HTTP, socketAddress));
    }

    return client;
  }

  @Provides @Singleton OkUrlFactory providesOkUrlfactory(OkHttpClient client) {
    return new OkUrlFactory(client);
  }

  @Provides @Singleton ErrorHandler provideDottieErrorHandler(Bus bus,
      EventReporter eventReporter) {
    return new LeexplorerErrorHandler(bus, eventReporter);
  }

  @Provides @Singleton Client provideLeexplorerClient(LeexplorerApplication application) {
    return new Client(application);
  }

  @Provides @Singleton ImageSourcePicker provideImageSourcePicker(Picasso picasso,
      Cloudinary cloudinary) {
    return new ImageSourcePicker(application, picasso, cloudinary);
  }

  @Provides @Singleton AudioSourcePicker provideAudioSourcePicker(Cloudinary cloudinary) {
    return new AudioSourcePicker(cloudinary);
  }

  @Provides @Singleton FileDownloader privideFileDownloader(OkUrlFactory factory) {
    return new FileDownloader(factory);
  }

  @Provides @Singleton Picasso providePicasso(LeexplorerApplication application,
      OkHttpClient client, final EventReporter eventReporter) {
    Picasso.Builder builder = new Picasso.Builder(application.getApplicationContext());
    OkHttpDownloader downloader = new OkHttpDownloader(client);
    builder.downloader(downloader).listener(new Picasso.Listener() {
      @Override public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
        Log.e("DottieModule", "url: " + uri.toString() + " exception: " + exception);
        eventReporter.logException(exception);
      }
    });

    return builder.build();
  }

  @Provides @Singleton EventReporter provideEventReported(LeexplorerApplication application) {
    return new EventReporter(application);
  }

  @Provides @Singleton HashMap<String, FilteredIBeacon> provideBeaconsFound() {
    return new HashMap<>();
  }

  @Provides @Singleton BluetoothCrashResolver provideBluetoothCrashResolver(
      LeexplorerApplication application) {
    return new BluetoothCrashResolver(application);
  }

  @Provides @Singleton Cloudinary provideCloudinary() {
    Map config = new HashMap();
    config.put("cloud_name", AppConstants.CLOUDINARY_CLOUD_NAME);
    return new Cloudinary(config);
  }

  @Provides @Singleton OkClient provideOkClient(OkHttpClient httpClient) {
    return new LeexplorerOkClient(httpClient, AppConstants.HMAC_KEY);
  }

  @Provides @Singleton RestAdapter provideRestAdapter(OkClient client, ErrorHandler errorHandler,
      RequestInterceptor requestInterceptor) {
    return new RestAdapter.Builder().setClient(client)
        .setEndpoint(AppConstants.getEndpoint())
        .setErrorHandler(errorHandler)
        .setRequestInterceptor(requestInterceptor)
        .build();
  }

  @Provides @Singleton ShareManager privideShareManager(LeexplorerApplication application) {
    return new ShareManager(application);
  }
}

