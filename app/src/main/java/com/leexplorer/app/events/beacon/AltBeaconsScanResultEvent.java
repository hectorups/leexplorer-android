package com.leexplorer.app.events.beacon;

import java.util.Collection;
import org.altbeacon.beacon.Beacon;

public class AltBeaconsScanResultEvent {
  private Collection<Beacon> beacons;

  public AltBeaconsScanResultEvent(Collection<Beacon> beacons) {
    this.beacons = beacons;
  }

  public Collection<Beacon> getBeacons() {
    return beacons;
  }
}
