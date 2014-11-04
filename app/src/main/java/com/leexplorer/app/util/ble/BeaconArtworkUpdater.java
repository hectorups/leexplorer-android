package com.leexplorer.app.util.ble;

import com.leexplorer.app.models.Artwork;
import com.leexplorer.app.models.FilteredIBeacon;
import java.util.List;

public class BeaconArtworkUpdater {
  public static final String TAG = "BeaconArtworkUpdater";

  public static List<Artwork> updateDistances(List<Artwork> artworks,
      List<FilteredIBeacon> beacons) {

    // Reset Distance
    for (Artwork artwork : artworks) {
      artwork.resetDistance();
    }

    // Assign Distances according to beacons
    for (FilteredIBeacon beacon : beacons) {
      Double distance = beacon.getDistance();
      if(distance == null) {
        continue;
      }

      for (Artwork artwork : artworks) {
        if (artwork.getMajorminor().equals(beacon.getMajorminor())) {
            artwork.setDistance(distance);
        }
      }
    }

    return artworks;
  }

  public static class ArtworkNullException extends Exception{}
}
