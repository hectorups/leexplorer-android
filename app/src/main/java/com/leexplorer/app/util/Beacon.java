package com.leexplorer.app.util;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by hectormonserrate on 22/02/14.
 */
public class Beacon implements Parcelable {
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
  private String mac;
  private ArrayList<Integer> rssis;
  private String uuid;

  public Beacon(String mac, byte[] scanRecord, int rssi) {
    rssis = new ArrayList<>();
    addRssi(rssi);
    this.uuid = serviceFromScanRecord(scanRecord);
    this.mac = mac;
  }

  protected Beacon(Parcel in) {
    uuid = in.readString();
    mac = in.readString();

    rssis = new ArrayList<>();
    rssis.add(in.readInt());
  }

  public void addRssi(int rssi) {
    rssis.add(rssi);
  }

  public int getRssi() {
    int total = 0;
    for (int i : rssis) {
      total += i;
    }
    return total / rssis.size();
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

  private String serviceFromScanRecord(byte[] scanRecord) {

    final int serviceOffset = 9;
    final int serviceLimit = 16;
    try {
      byte[] service = Arrays.copyOfRange(scanRecord, serviceOffset, serviceOffset + serviceLimit);
      return bytesToHex(service);
    } catch (Exception e) {
      return null;
    }
  }

  private String bytesToHex(byte[] bytes) {
    StringBuilder builder = new StringBuilder();
    for (byte b : bytes) {
      builder.append(String.format("%02x ", b));
    }
    return builder.toString();
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
}
