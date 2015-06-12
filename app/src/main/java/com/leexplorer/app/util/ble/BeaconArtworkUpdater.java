package com.leexplorer.app.util.ble;

import com.leexplorer.app.models.Artwork;
import com.leexplorer.app.models.FilteredIBeacon;
import java.util.List;
import org.altbeacon.beacon.Beacon;

public class BeaconArtworkUpdater {
  public static final String TAG = "BeaconArtworkUpdater";

  public static List<Artwork> updateDistances(List<Artwork> artworks, List<Beacon> beacons) {

    // Reset Distance
    for (Artwork artwork : artworks) {
      artwork.resetDistance();
    }

    // Assign Distances according to beacons
    for (Beacon beacon : beacons) {
      double distance = beacon.getDistance();

      for (Artwork artwork : artworks) {
        String beaconMajorMinor = String.valueOf(
            Majorminor.longFromMajorminor(beacon.getId2().toInt(), beacon.getId3().toInt()));
        if (artwork.getMajorminor().equals(beaconMajorMinor)) {
          artwork.setDistance(distance);
        }
      }
    }

    return artworks;
  }

  public static class ArtworkNullException extends Exception {
  }
}
