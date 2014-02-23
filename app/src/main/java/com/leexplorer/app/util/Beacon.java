package com.leexplorer.app.util;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by hectormonserrate on 22/02/14.
 */
public class Beacon implements Parcelable{
    private String mac;
    private ArrayList<Integer> rssis;
    private String uuid;


    public Beacon(String mac, String uuid, int rssi){
        rssis = new ArrayList<>();
        addRssi(rssi);
        this.uuid = uuid;
        this.mac = mac;
    }

    public void addRssi(int rssi){
        rssis.add(rssi);
    }

    public int getRssi(){
        int total = 0;
        for(int i: rssis){
            total += i;
        }
        return (total / rssis.size());
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /*
     *  Parcelable Overrides
     */

    protected Beacon(Parcel in) {
        uuid = in.readString();
        mac = in.readString();

        rssis = new ArrayList<>();
        rssis.add(in.readInt());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uuid);
        dest.writeString(mac);
        dest.writeInt(getRssi());
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Beacon> CREATOR = new Parcelable.Creator<Beacon>() {
        @Override
        public Beacon createFromParcel(Parcel in) {
            return new Beacon(in);
        }

        @Override
        public Beacon[] newArray(int size) {
            return new Beacon[size];
        }
    };
}
