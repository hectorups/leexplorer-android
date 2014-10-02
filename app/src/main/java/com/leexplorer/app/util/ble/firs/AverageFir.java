package com.leexplorer.app.util.ble.firs;

import com.leexplorer.app.models.IBeacon;
import com.leexplorer.app.util.ble.BleUtils;
import java.util.ArrayList;
import java.util.List;

public class AverageFir implements BleFir {
  private List<Double> distances;
  private Double distance;

  public AverageFir() {
    distances = new ArrayList<>();
  }

  @Override public Double getDistance() {
    return distance;
  }

  public void addAdvertisement(IBeacon iBeacon) {
    double currentDistance = BleUtils.calculateAccuracy(iBeacon.getTxPower(), iBeacon.getRssi());
    distances.add(currentDistance);

    double acumulatedDistance = 0;
    for (double v : distances) {
      acumulatedDistance += v;
    }

    distance = acumulatedDistance / distances.size();
  }
}
