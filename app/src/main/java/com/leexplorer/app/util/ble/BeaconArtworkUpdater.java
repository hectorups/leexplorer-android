package com.leexplorer.app.util.ble;

import com.leexplorer.app.models.Artwork;
import java.util.List;

/**
 * Created by hectormonserrate on 23/02/14.
 */
public class BeaconArtworkUpdater {

  public static List<Artwork> updateDistances(List<Artwork> artworks,
      List<Beacon> beacons) {
    // Reset Distance
    for (Artwork aw : artworks) {
      aw.setDistance(0);
    }

    // Assign Distances according to beacons
    for (Beacon b : beacons) {
      for (Artwork aw : artworks) {
        if (aw.getMac().equals(b.getMac())) {
          aw.setDistance(b.getRssi());
          break;
        }
      }
    }

    return artworks;
  }
}
