package com.leexplorer.app.util.ble;

import android.util.Log;
import com.leexplorer.app.models.Artwork;
import com.leexplorer.app.models.FilteredIBeacon;
import java.util.List;

public class BeaconArtworkUpdater {

  public static List<Artwork> updateDistances(List<Artwork> artworks,
      List<FilteredIBeacon> beacons) throws ArtworkNullException {
    // Reset Distance
    for (Artwork artwork : artworks) {
      artwork.resetDistance();
    }

    // Assign Distances according to beacons
    for (FilteredIBeacon beacon : beacons) {
      for (Artwork artwork : artworks) {
        if (artwork.getMajorminor().equals(beacon.getMajorminor())) {
          Double dist = beacon.getDistance();
          try {
            artwork.setDistance(dist);
          } catch (NullPointerException e) {
            Log.wtf("BeaconArtworkUpdater", "Artwork null?? " + e.toString());
            throw new ArtworkNullException();
          }
          break;
        }
      }
    }

    return artworks;
  }

  public static class ArtworkNullException extends Exception{}
}
