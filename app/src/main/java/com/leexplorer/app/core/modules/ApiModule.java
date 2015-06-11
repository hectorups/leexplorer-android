package com.leexplorer.app.core.modules;

import android.text.TextUtils;
import com.leexplorer.app.api.Client;
import com.leexplorer.app.api.LeexplorerErrorHandler;
import com.leexplorer.app.api.LeexplorerOkClient;
import com.leexplorer.app.api.LeexplorerRequestInterceptor;
import com.leexplorer.app.core.AppConstants;
import com.leexplorer.app.core.EventReporter;
import com.leexplorer.app.core.LeexplorerApplication;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;
import com.squareup.otto.Bus;
import dagger.Module;
import dagger.Provides;
import java.io.File;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;
import javax.inject.Singleton;
import retrofit.ErrorHandler;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

@Module public final class ApiModule {

  private final LeexplorerApplication application;

  public ApiModule(LeexplorerApplication application) {
    this.application = application;
  }

  @Provides @Singleton Cache provideCache() {
    Cache responseCache =
        new Cache(new File(application.getCacheDir(), "okhttp"), AppConstants.NETWORK_CACHE);
    return responseCache;
  }

  @Provides @Singleton RequestInterceptor providesRequestInterceptor() {
    return new LeexplorerRequestInterceptor();
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

  @Provides @Singleton ErrorHandler provideLeexplorerErrorHandler(Bus bus,
      EventReporter eventReporter) {
    return new LeexplorerErrorHandler(bus, eventReporter);
  }

  @Provides @Singleton Client provideLeexplorerClient(LeexplorerApplication application) {
    return new Client(application);
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
}
