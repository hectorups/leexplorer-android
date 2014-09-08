package com.leexplorer.app.api;

import com.leexplorer.app.BuildConfig;
import retrofit.RequestInterceptor;

public class LeexplorerRequestInterceptor implements RequestInterceptor {

  @Override public void intercept(RequestFacade requestFacade) {
    requestFacade.addHeader("Client-Build", String.valueOf(BuildConfig.VERSION_CODE));
  }

}
