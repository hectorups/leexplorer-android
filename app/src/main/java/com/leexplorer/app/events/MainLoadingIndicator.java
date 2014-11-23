package com.leexplorer.app.events;

public class MainLoadingIndicator {
  private boolean loading;

  public MainLoadingIndicator(boolean loading) {
    this.loading = loading;
  }

  public boolean isLoading() {
    return loading;
  }
}
