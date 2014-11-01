package com.leexplorer.app.util.ble.firs;

import com.leexplorer.app.models.IBeacon;
import com.leexplorer.app.util.ble.BleUtils;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AverageFir implements BleFir {
  private static int EXPIRING_TIME = 2 * 60 * 1000;
  private List<Double> distances;
  private Double distance;
  private Date lastUpdatedAt;

  public AverageFir() {
    reset();
    lastUpdatedAt = new Date();
  }

  @Override public Double getDistance() {
    return distance;
  }

  public void addAdvertisement(IBeacon iBeacon) {
    resetIfOld();
    lastUpdatedAt = new Date();

    double currentDistance = BleUtils.calculateAccuracy(iBeacon.getTxPower(), iBeacon.getRssi());
    distances.add(currentDistance);

    double acumulatedDistance = 0;
    for (double v : distances) {
      acumulatedDistance += v;
    }

    distance = acumulatedDistance / distances.size();
  }

  private void resetIfOld() {
    Date expiringDate = new Date(System.currentTimeMillis() - EXPIRING_TIME);
    if (lastUpdatedAt.before(expiringDate)) {
      reset();
    }
  }

  private void reset() {
    distances = new ArrayList<>();
    distance = null;
  }
}
