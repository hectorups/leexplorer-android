package com.leexplorer.app.core;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.activeandroid.ActiveAndroid;
import com.crashlytics.android.Crashlytics;
import com.leexplorer.app.R;
import dagger.ObjectGraph;
import java.util.Arrays;
import java.util.List;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class LeexplorerApplication extends Application {

  private ObjectGraph graph;

  public boolean isOnline() {
    ConnectivityManager cm =
        (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo netInfo = cm.getActiveNetworkInfo();
    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
      return true;
    }
    return false;
  }

  @Override
  public void onCreate() {
    super.onCreate();

    CalligraphyConfig.initDefault("fonts/Museo500-Regular.otf", R.attr.fontPath);

    graph = ObjectGraph.create(getModules().toArray());
    Crashlytics.start(this);

    ActiveAndroid.initialize(this);
  }

  @Override
  public void onTerminate() {
    ActiveAndroid.dispose();
    super.onTerminate();
  }

  protected List<Object> getModules() {
    return Arrays.<Object>asList(new LeexplorerModule(this));
  }

  public void inject(Object object) {
    graph.inject(object);
  }
}
