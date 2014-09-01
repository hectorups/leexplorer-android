package com.leexplorer.app.util.ble;

import java.util.Arrays;

public class BleUuid {

  public static String serviceFromScanRecord(byte[] scanRecord) {

    final int serviceOffset = 9;
    final int serviceLimit = 16;

    try {

      byte[] service = Arrays.copyOfRange(scanRecord, serviceOffset, serviceOffset + serviceLimit);

      return bytesToHex(service);
    } catch (Exception e) {
      return null;
    }
  }

  private static String bytesToHex(byte[] bytes) {
    StringBuilder builder = new StringBuilder();
    for (byte b : bytes) {
      builder.append(String.format("%02x", b));
    }

    return builder.toString();
  }
}
