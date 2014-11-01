package com.leexplorer.app.models;

import android.os.Parcel;
import android.os.Parcelable;
import com.leexplorer.app.util.ble.Majorminor;
import com.leexplorer.app.util.ble.firs.BleFir;
import com.leexplorer.app.util.ble.firs.StandardFir;

public class FilteredIBeacon implements Parcelable {

  private int major;
  private int minor;
  private String uuid;
  private int txPower;
  private Double distance;
  private BleFir bleFir;

  public FilteredIBeacon(IBeacon iBeacon) {
    this.uuid = iBeacon.getProximityUuid();
    this.txPower = iBeacon.getTxPower();
    this.major = iBeacon.major;
    this.minor = iBeacon.minor;
    bleFir = new StandardFir();
    addAdvertisement(iBeacon);
  }

  public void addAdvertisement(IBeacon iBeacon) {
    bleFir.addAdvertisement(iBeacon);
    distance = bleFir.getDistance();
  }

  public Double getDistance() {
    return distance;
  }

  public String getMajorminor() {
    return String.valueOf(Majorminor.longFromMajorminor(major, minor));
  }

  public int getMajor() {
    return major;
  }

  public int getMinor() {
    return minor;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @SuppressWarnings("unused")
  public static final Parcelable.Creator<FilteredIBeacon> CREATOR =
      new Parcelable.Creator<FilteredIBeacon>() {
        @Override
        public FilteredIBeacon createFromParcel(Parcel in) {
          return new FilteredIBeacon(in);
        }

        @Override
        public FilteredIBeacon[] newArray(int size) {
          return new FilteredIBeacon[size];
        }
      };

  protected FilteredIBeacon(Parcel in) {
    uuid = in.readString();
    major = in.readInt();
    minor = in.readInt();
    distance = in.readDouble();
    txPower = in.readInt();
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(uuid);
    dest.writeInt(major);
    dest.writeInt(minor);
    dest.writeDouble(distance);
    dest.writeInt(txPower);
  }
}
