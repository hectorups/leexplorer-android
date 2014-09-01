package com.leexplorer.app.events;

public class LoadMapEvent {
  private String address;

  public LoadMapEvent(String address) {
    this.address = address;
  }

  public String getAddress() {
    return address;
  }
}
