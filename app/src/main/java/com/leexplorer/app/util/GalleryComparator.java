package com.leexplorer.app.util;

import com.leexplorer.app.models.Gallery;

import java.util.Comparator;

/**
 * Created by deepakdhiman on 3/1/14.
 */
public class GalleryComparator implements Comparator<Gallery> {

    @Override
    public int compare(Gallery gallery, Gallery gallery2) {
        if(gallery.getDistanceFromCurrentLocation()==gallery2.getDistanceFromCurrentLocation()){
            return 0;
        }
        else if(gallery.getDistanceFromCurrentLocation()>gallery2.getDistanceFromCurrentLocation()){
            return 1;
        }
        else{
            return -1;
        }

    }
}
