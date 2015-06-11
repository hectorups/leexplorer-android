package com.leexplorer.app.services;

import android.app.Service;
import com.leexplorer.app.core.ApplicationComponent;
import com.leexplorer.app.core.EventReporter;
import com.leexplorer.app.core.LeexplorerApplication;
import com.squareup.otto.Bus;
import javax.inject.Inject;

public abstract class BaseService extends Service {

  @Inject protected Bus bus;
  @Inject protected EventReporter eventReporter;

  @Override public void onCreate() {
    super.onCreate();

    injectComponent(((LeexplorerApplication) getApplication()).getComponent());
    bus.register(this);
  }

  @Override public void onDestroy() {
    bus.unregister(this);
    super.onDestroy();
  }

  abstract protected void injectComponent(ApplicationComponent component);
}
