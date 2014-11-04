package com.leexplorer.app.util.ble.firs;

import com.leexplorer.app.models.IBeacon;
import com.leexplorer.app.util.ble.BleUtils;
import java.util.Date;

public class StandardFir implements BleFir {
  private static final int EXPIRING_TIME = 2 * 60 * 1000;
  private static final int GONE_TIME = 10 * 1000;
  private static final double coefficients[] = new double[] {
      0.02, 0.04, 0.06, 0.08, 0.10, 0.12, 0.16 , 0.12, 0.10, 0.08, 0.06, 0.04, 0.02
  };

  private int length;
  private double[] delayLine;
  private int count = 0;
  private double currentDistance;
  private Integer txPower;
  private Date lastUpdatedAt;

  public StandardFir() {
    lastUpdatedAt = new Date();
    length = coefficients.length;
    delayLine = new double[length];
    reset();
  }

  public void addAdvertisement(IBeacon iBeacon) {
    resetIfOld();
    lastUpdatedAt = new Date();

    double tempDistance = BleUtils.calculateAccuracy(getTxPower(iBeacon), iBeacon.getRssi());

    currentDistance = getOutputSample(tempDistance);

    // Debug
    //if( iBeacon.getMinor() == 26 ) {
    //  DecimalFormat df = new DecimalFormat("#.00");
    //  Log.d("FIR", "curdist: "
    //      + df.format(currentDistance)
    //      + ", TempDist: "
    //      + df.format(tempDistance)
    //      + ", RSSI: "
    //      + iBeacon.getRssi());
    //}
  }

  public double getOutputSample(double inputSample) {
    delayLine[count] = inputSample;
    double result = 0.0;
    int index = count;
    for (int i = 0; i < length; i++) {
      double coefficient = coefficients[i];
      double value = delayLine[index--];
      result += coefficient * (value != 0 ? value : inputSample);
      if (index < 0) {
        index = length - 1;
      }
    }
    if (++count >= length) {
      count = 0;
    }

    return result;
  }

  @Override public Double getDistance() {
    resetIfOld();
    if(isGone()) {
      return null;
    }
    return currentDistance == 0. ? null : currentDistance;
  }

  private int getTxPower(IBeacon iBeacon) {
    if (this.txPower == null) {
      this.txPower = iBeacon.getTxPower();
    }

    return this.txPower;
  }

  private void resetIfOld() {
    Date expiringDate = new Date(System.currentTimeMillis() - EXPIRING_TIME);
    if (lastUpdatedAt.before(expiringDate)) {
      reset();
    }
  }

  private boolean isGone() {
    Date expiringDate = new Date(System.currentTimeMillis() - GONE_TIME);
    return lastUpdatedAt.before(expiringDate);
  }

  private void reset() {
    for (int i = 0; i < delayLine.length; i++) {
      delayLine[i] = 0.;
    }
    count = 0;
  }
}
