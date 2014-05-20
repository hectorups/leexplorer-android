package com.leexplorer.app.events;

/**
 * Created by hectormonserrate on 19/05/14.
 */
public class LoadingEvent {
  boolean loading;

  public LoadingEvent(boolean loading) {
    this.loading = loading;
  }

  public boolean isLoading() {
    return loading;
  }

  public void setLoading(boolean loading) {
    this.loading = loading;
  }
}
