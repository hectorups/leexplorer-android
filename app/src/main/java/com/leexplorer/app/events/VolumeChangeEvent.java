package com.leexplorer.app.events;

public class VolumeChangeEvent {
  private boolean up;

  public VolumeChangeEvent(boolean up) {
    this.up = up;
  }

  public boolean isUp() {
    return up;
  }
}
