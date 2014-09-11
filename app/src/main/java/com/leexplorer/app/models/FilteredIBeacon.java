package com.leexplorer.app.models;

import android.os.Parcel;
import android.os.Parcelable;
import com.leexplorer.app.util.ble.BleUtils;
import com.leexplorer.app.util.ble.Majorminor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FilteredIBeacon implements Parcelable {

  private static final int BUFFER_SIZE = 6;
  private double lastError;
  private double kalmanEstimation;

  private int major;
  private int minor;
  private ArrayList<Double> distanceBuffer;
  private List<Double> medianBuffer;
  private String uuid;
  private int txPower;
  private Double distance;

  public FilteredIBeacon(IBeacon iBeacon) {
    distanceBuffer = new ArrayList<>();
    medianBuffer = new ArrayList<>();
    lastError = 1;
    kalmanEstimation = 0;

    this.uuid = iBeacon.getProximityUuid();
    this.txPower = iBeacon.getTxPower();
    this.major = iBeacon.major;
    this.minor = iBeacon.minor;
    addAdvertisement(iBeacon);
  }

  public void addAdvertisement(IBeacon iBeacon) {
    double varianceSum = 0;
    double expectedDistance = 0;
    double tempDistance = BleUtils.calculateAccuracy(txPower, iBeacon.getRssi());

    if (distanceBuffer.size() == BUFFER_SIZE) {
      distanceBuffer.remove(0);
    }

    distanceBuffer.add(tempDistance);
    if (iBeacon.getProximity() == IBeacon.PROXIMITY_IMMEDIATE) {
      expectedDistance = 0.8625;
      lastError = 1;
      kalmanEstimation = 0;
    } else if (iBeacon.getProximity() == IBeacon.PROXIMITY_NEAR) {
      expectedDistance = 1.75;
      lastError = 1;
      kalmanEstimation = 0;
    } else if (iBeacon.getProximity() == IBeacon.PROXIMITY_FAR) {
      expectedDistance = 2.625;
      lastError = 1;
      kalmanEstimation = 0;
    }

    for (int k = 0; k < distanceBuffer.size(); k++) {
      varianceSum =
          varianceSum + (distanceBuffer.get(k) - expectedDistance) * (distanceBuffer.get(k)
              - expectedDistance);
    }
    double variance = varianceSum / BUFFER_SIZE;
    double standardDeviation = Math.sqrt(variance);

    double kalmanParameter = lastError / (lastError + standardDeviation);
    kalmanEstimation = kalmanEstimation + kalmanParameter * (tempDistance - kalmanEstimation);
    lastError = (1 - kalmanParameter) * lastError;

    if (medianBuffer.size() >= BUFFER_SIZE) {
      medianBuffer.remove(0);
    }
    medianBuffer.add(kalmanEstimation);
    List<Double> medianBufferSorted = new ArrayList<>();
    medianBufferSorted.clear();
    medianBufferSorted.addAll(medianBuffer);
    Collections.sort(medianBufferSorted);

    distance = median(medianBufferSorted);
  }

  public Double getDistance() {
    return distance;
  }

  private double median(List<Double> a) {
    int middle = a.size() / 2;
    if (a.size() % 2 == 1) {
      return a.get(middle);
    } else {
      return (a.get(middle - 1) + a.get(middle)) / 2.0;
    }
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
