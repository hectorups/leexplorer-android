package com.leexplorer.app.core;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.activeandroid.ActiveAndroid;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class LeexplorerApplication extends Application {
  private ApplicationComponent component;

  public boolean isOnline() {
    ConnectivityManager cm =
        (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo netInfo = cm.getActiveNetworkInfo();
    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
      return true;
    }
    return false;
  }

  @Override public void onCreate() {
    super.onCreate();
    Fabric.with(this, new Crashlytics());
    ActiveAndroid.initialize(this);
    component = ApplicationComponent.Initializer.init(this);
  }

  @Override public void onTerminate() {
    ActiveAndroid.dispose();
    super.onTerminate();
  }

  public ApplicationComponent getComponent() {
    return component;
  }
}
