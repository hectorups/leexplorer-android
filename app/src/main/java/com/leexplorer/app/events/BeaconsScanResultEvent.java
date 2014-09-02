package com.leexplorer.app.events;

import com.leexplorer.app.models.FilteredIBeacon;
import java.util.List;

public class BeaconsScanResultEvent {
  List<FilteredIBeacon> beacons;

  public BeaconsScanResultEvent(List<FilteredIBeacon> beacons) {
    this.beacons = beacons;
  }

  public List<FilteredIBeacon> getBeacons() {
    return beacons;
  }

}
