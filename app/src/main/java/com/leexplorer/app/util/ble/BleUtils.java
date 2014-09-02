package com.leexplorer.app.util.ble;

public class BleUtils {

  static public double calculateAccuracy(int txPower, double rssi) {
    if (rssi == 0) {
      return -1.0;
    }

    double ratio = rssi * 1.0 / txPower;
    if (ratio < 1.0) {
      return Math.pow(ratio, 10);
    } else {
      double accuracy = (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
      return accuracy;
    }
  }

}
