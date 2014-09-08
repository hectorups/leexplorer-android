package com.leexplorer.app.api;

import com.leexplorer.app.BuildConfig;
import com.leexplorer.app.core.AppConstants;
import retrofit.RequestInterceptor;

public class LeexplorerRequestInterceptor implements RequestInterceptor {

  @Override public void intercept(RequestFacade requestFacade) {
    requestFacade.addHeader(AppConstants.CLIENT_BUILD_HEADER_KEY, getClientBuildHeaderValue());
  }

  private String getClientBuildHeaderValue() {
    StringBuilder value = new StringBuilder();
    value.append(AppConstants.CLIENT_NAME)
        .append('/')
        .append(BuildConfig.VERSION_NAME)
        .append('/')
        .append(BuildConfig.VERSION_CODE);

    return value.toString();
  }
}
