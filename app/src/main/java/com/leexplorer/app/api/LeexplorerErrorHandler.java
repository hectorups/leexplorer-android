package com.leexplorer.app.api;

import android.util.Log;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.leexplorer.app.api.models.ApiError;
import com.leexplorer.app.core.AppConstants;
import com.leexplorer.app.core.EventReporter;
import com.leexplorer.app.events.BuildKilledEvent;
import com.leexplorer.app.events.LogoutRequestedEvent;
import com.leexplorer.app.events.NetworkErrorEvent;
import com.squareup.otto.Bus;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.MimeUtil;
import retrofit.mime.TypedInput;

public class LeexplorerErrorHandler implements ErrorHandler {
  private static final String TAG = "DottieErrorHandler";
  private final Bus bus;
  private EventReporter eventReporter;

  public LeexplorerErrorHandler(Bus bus, EventReporter eventReporter) {
    this.bus = bus;
    this.eventReporter = eventReporter;
  }

  @Override public Throwable handleError(RetrofitError cause) {

    Response response = cause.getResponse();

    if (cause.isNetworkError() && cause.getCause() instanceof SocketTimeoutException) {
      bus.post(new NetworkErrorEvent());
    } else {
      eventReporter.logException(cause);
    }

    try {
      ApiError error = getError(response.getBody());
      //unauthorized
      if (response.getStatus() == 401) {
        bus.post(new LogoutRequestedEvent());
      } else if (response.getStatus() == 403 && error != null && error.getMessage()
          .contains(AppConstants.BUILD_KILLED_MESSAGE)) {
        bus.post(new BuildKilledEvent());
      } else {
        Log.d(TAG, "Error!!! cause: " + cause);
      }
    } catch (NullPointerException e) {
      Log.d(TAG, "cause is null");
    }
    return cause;
  }

  private ApiError getError(TypedInput body) {
    if (body == null) {
      return null;
    }

    String charset = "UTF-8";

    if (body.mimeType() != null) {
      charset = MimeUtil.parseCharset(body.mimeType());
    }

    InputStreamReader reader = null;

    try {
      reader = new InputStreamReader(body.in(), charset);
      return new GsonBuilder().create().fromJson(reader, ApiError.class);
    } catch (IOException e) {
      Log.e(TAG, "Failed to parse HTTP Error from API", e);
    } catch (JsonParseException e) {
      eventReporter.logException(e);
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException ignored) {
          Log.e(TAG, "Filed to close stream reader");
        }
      }
    }

    return null;
  }
}