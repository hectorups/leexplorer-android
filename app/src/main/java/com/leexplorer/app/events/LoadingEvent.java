package com.leexplorer.app.events;

public class LoadingEvent {
  boolean loading;

  public LoadingEvent(boolean loading) {
    this.loading = loading;
  }

  public boolean isLoading() {
    return loading;
  }

}
