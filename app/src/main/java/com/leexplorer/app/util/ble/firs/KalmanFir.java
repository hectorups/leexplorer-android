package com.leexplorer.app.util.ble.firs;

import com.leexplorer.app.models.IBeacon;
import com.leexplorer.app.util.ble.BleUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KalmanFir implements BleFir {
  private ArrayList<Double> distanceBuffer;
  private List<Double> medianBuffer;
  private Double distance;
  private double lastError;
  private double kalmanEstimation;
  private static final int BUFFER_SIZE = 6;
  private Integer txPower;

  public KalmanFir(){
    distanceBuffer = new ArrayList<>();
    medianBuffer = new ArrayList<>();
  }


  public void addAdvertisement(IBeacon iBeacon) {
    double varianceSum = 0;
    double expectedDistance = 0;
    double tempDistance = BleUtils.calculateAccuracy(getTxPower(iBeacon), iBeacon.getRssi());

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

  @Override public Double getDistance() {
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

  private int getTxPower(IBeacon iBeacon) {
    if (this.txPower == null) {
      this.txPower = iBeacon.getTxPower();
    }

    return this.txPower;
  }
}
