package com.leexplorer.app.core;

import android.app.Application;
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
import com.leexplorer.app.core.modules.ApiModule;
import com.leexplorer.app.core.modules.SystemServiceModule;
import com.leexplorer.app.fragments.ArtworkFragment;
import com.leexplorer.app.fragments.ArtworkListFragment;
import com.leexplorer.app.fragments.ConfirmDialogFragment;
import com.leexplorer.app.fragments.FacilitiesDialogFragment;
import com.leexplorer.app.fragments.GalleryFragment;
import com.leexplorer.app.fragments.GalleryListFragment;
import com.leexplorer.app.fragments.GalleryMapFragment;
import com.leexplorer.app.services.AutoPlayService;
import com.leexplorer.app.services.BeaconScanService;
import com.leexplorer.app.services.GalleryDownloaderService;
import com.leexplorer.app.services.MediaPlayerService;
import dagger.Component;
import javax.inject.Singleton;

@Singleton @Component(modules = { ApiModule.class, SystemServiceModule.class })
public interface ApplicationComponent {

  void inject(GalleryFragment clazz);

  void inject(GalleryListFragment clazz);

  void inject(ArtworkFragment clazz);

  void inject(ArtworkListFragment clazz);

  void inject(FacilitiesDialogFragment clazz);

  void inject(ConfirmDialogFragment clazz);

  void inject(GalleryMapFragment clazz);

  void inject(BeaconScanService clazz);

  void inject(GalleryDownloaderService clazz);

  void inject(MediaPlayerService clazz);

  void inject(Client clazz);

  void inject(ArtworkAdapter clazz);

  void inject(GalleryAdapter clazz);

  void inject(GalleryPagerAdapter clazz);

  void inject(GalleryInfoAdapter clazz);

  void inject(ArtworkActivity clazz);

  void inject(GalleryActivity clazz);

  void inject(GalleryListActivity clazz);

  void inject(ArtworkListActivity clazz);

  void inject(FullScreenImageActivity clazz);

  void inject(AutoPlayService clazz);

  final class Initializer {
    public static ApplicationComponent init(Application application) {
      return DaggerApplicationComponent.builder()
          .apiModule(new ApiModule((LeexplorerApplication) application))
          .systemServiceModule(new SystemServiceModule((LeexplorerApplication) application))
          .build();
    }
  }
}

