package com.leexplorer.app.util;

import java.util.ArrayList;

/**
 * Created by hectormonserrate on 23/02/14.
 */
public class BeaconsManager {
    private static BeaconsManager instance;

    private ArrayList<Beacon> activeBeacons = new ArrayList<>();

    public static BeaconsManager getInstance(){
        if( instance == null){
            instance = new BeaconsManager();
        }

        return instance;
    }


    public void updateBeacons(ArrayList<Beacon> newList){
        activeBeacons = newList;
    }

    public ArrayList<Beacon> getAll(){
        return activeBeacons;
    }

}
