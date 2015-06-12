package com.leexplorer.app.core.modules;

import android.net.Uri;
import android.util.Log;
import com.cloudinary.Cloudinary;
import com.leexplorer.app.core.AndroidBus;
import com.leexplorer.app.core.AppConstants;
import com.leexplorer.app.core.EventReporter;
import com.leexplorer.app.core.LeexplorerApplication;
import com.leexplorer.app.util.ShareManager;
import com.leexplorer.app.util.offline.AudioSourcePicker;
import com.leexplorer.app.util.offline.FileDownloader;
import com.leexplorer.app.util.offline.ImageSourcePicker;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;
import com.squareup.otto.Bus;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import dagger.Module;
import dagger.Provides;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Singleton;

@Module public final class SystemServiceModule {

  private final LeexplorerApplication application;

  public SystemServiceModule(LeexplorerApplication application) {
    this.application = application;
  }

  @Provides @Singleton Bus provideBus() {
    return new AndroidBus();
  }

  @Provides @Singleton LeexplorerApplication providesLeexplorerApplicationContext() {
    return application;
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

  @Provides @Singleton Cloudinary provideCloudinary() {
    Map config = new HashMap();
    config.put("cloud_name", AppConstants.CLOUDINARY_CLOUD_NAME);
    return new Cloudinary(config);
  }

  @Provides @Singleton ShareManager provideShareManager(LeexplorerApplication application) {
    return new ShareManager(application);
  }
}
