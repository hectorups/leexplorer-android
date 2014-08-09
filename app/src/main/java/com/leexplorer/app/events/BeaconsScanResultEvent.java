package com.leexplorer.app.events;

import com.leexplorer.app.models.Beacon;
import java.util.List;

public class BeaconsScanResultEvent {
  List<Beacon> beacons;

  public BeaconsScanResultEvent(List<Beacon> beacons) {
    this.beacons = beacons;
  }

  public List<Beacon> getBeacons() {
    return beacons;
  }

}
