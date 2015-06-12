package com.leexplorer.app.core.modules;

import android.app.Application;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;

@Module public final class BeaconModule {
  private final Application application;

  public BeaconModule(Application application) {
    this.application = application;
  }

  @Provides @Singleton public BeaconManager providesBeaconManager() {
    BeaconManager beaconManager = BeaconManager.getInstanceForApplication(application);
    beaconManager.getBeaconParsers().add(new BeaconParser().
        setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
    return beaconManager;
  }
}
