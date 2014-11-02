package com.leexplorer.app.util.ble;

import com.leexplorer.app.models.Artwork;
import com.leexplorer.app.models.FilteredIBeacon;
import java.util.List;

public class BeaconArtworkUpdater {

  public static List<Artwork> updateDistances(List<Artwork> artworks,
      List<FilteredIBeacon> beacons) {
    // Reset Distance
    for (Artwork artwork : artworks) {
      artwork.resetDistance();
    }

    // Assign Distances according to beacons
    for (FilteredIBeacon beacon : beacons) {
      for (Artwork artwork : artworks) {
        if (artwork.getMajorminor().equals(beacon.getMajorminor())) {
          Double dist = beacon.getDistance();
          artwork.setDistance(dist);
          break;
        }
      }
    }

    return artworks;
  }
}
