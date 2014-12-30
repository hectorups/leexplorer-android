package com.leexplorer.app.util;

import com.leexplorer.app.models.Gallery;
import java.io.Serializable;
import java.util.Comparator;

public class GalleryComparator implements Comparator<Gallery>, Serializable {

  @Override
  public int compare(Gallery gallery, Gallery gallery2) {
    if (gallery.getDistanceFromCurrentLocation() == gallery2.getDistanceFromCurrentLocation()) {
      return 0;
    } else if (gallery.getDistanceFromCurrentLocation()
        > gallery2.getDistanceFromCurrentLocation()) {
      return 1;
    } else {
      return -1;
    }
  }
}
