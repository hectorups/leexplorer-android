package com.leexplorer.app.models;

import android.util.Log;

public class IBeacon {
  /**
   * Less than half a meter away
   */
  public static final int PROXIMITY_IMMEDIATE = 1;
  /**
   * More than half a meter away, but less than four meters away
   */
  public static final int PROXIMITY_NEAR = 2;
  /**
   * More than four meters away
   */
  public static final int PROXIMITY_FAR = 3;
  /**
   * No distance estimate was possible due to a bad RSSI value or measured TX power
   */
  public static final int PROXIMITY_UNKNOWN = 0;

  final private static char[] hexArray =
      { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
  private static final String TAG = "IBeacon";

  /**
   * A 16 byte UUID that typically represents the company owning a number of iBeacons
   * Example: E2C56DB5-DFFB-48D2-B060-D0F5A71096E0
   */
  protected String proximityUuid;
  /**
   * A 16 bit integer typically used to represent a group of iBeacons
   */
  protected int major;
  /**
   * A 16 bit integer that identifies a specific iBeacon within a group
   */
  protected int minor;
  /**
   * An integer with four possible values representing a general idea of how far the iBeacon is
   * away
   *
   * @see #PROXIMITY_IMMEDIATE
   * @see #PROXIMITY_NEAR
   * @see #PROXIMITY_FAR
   * @see #PROXIMITY_UNKNOWN
   */
  protected Integer proximity;
  /**
   * A double that is an estimate of how far the iBeacon is away in meters.  This name is
   * confusing,
   * but is copied from
   * the iOS7 SDK terminology.   Note that this number fluctuates quite a bit with RSSI, so despite
   * the name, it is not
   * super accurate.   It is recommended to instead use the proximity field, or your own
   * bucketization of this value.
   */
  protected Double accuracy;
  /**
   * The measured signal strength of the Bluetooth packet that led do this iBeacon detection.
   */
  protected int rssi;
  /**
   * The calibrated measured Tx power of the iBeacon in RSSI
   * This value is baked into an iBeacon when it is manufactured, and
   * it is transmitted with each packet to aid in the distance estimate
   */
  protected int txPower;

  /**
   * If multiple RSSI samples were available, this is the running average
   */
  protected Double runningAverageRssi = null;

  /**
   * @return accuracy
   * @see #accuracy
   */
  public double getAccuracy() {
    if (accuracy == null) {
      accuracy = calculateAccuracy(txPower, runningAverageRssi != null ? runningAverageRssi : rssi);
    }
    return accuracy;
  }

  /**
   * @return major
   * @see #major
   */
  public int getMajor() {
    return major;
  }

  /**
   * @return minor
   * @see #minor
   */
  public int getMinor() {
    return minor;
  }

  /**
   * @return proximity
   * @see #proximity
   */
  public int getProximity() {
    if (proximity == null) {
      proximity = calculateProximity(getAccuracy());
    }
    return proximity;
  }

  /**
   * @return rssi
   * @see #rssi
   */
  public int getRssi() {
    return rssi;
  }

  /**
   * @return txPowwer
   * @see #txPower
   */
  public int getTxPower() {
    return txPower;
  }

  /**
   * @return proximityUuid
   * @see #proximityUuid
   */
  public String getProximityUuid() {
    return proximityUuid;
  }

  @Override
  public int hashCode() {
    return minor;
  }

  /**
   * Two detected iBeacons are considered equal if they share the same three identifiers,
   * regardless
   * of their distance or RSSI.
   */
  @Override
  public boolean equals(Object that) {
    if (!(that instanceof IBeacon)) {
      return false;
    }
    IBeacon thatIBeacon = (IBeacon) that;
    return thatIBeacon.getMajor() == this.getMajor()
        && thatIBeacon.getMinor() == this.getMinor()
        && thatIBeacon.getProximityUuid() == thatIBeacon.getProximityUuid();
  }

  /**
   * Construct an iBeacon from a Bluetooth LE packet collected by Android's Bluetooth APIs
   *
   * @param scanData The actual packet bytes
   * @param rssi The measured signal strength of the packet
   * @return An instance of an <code>IBeacon</code>
   */
  public static IBeacon fromScanData(byte[] scanData, int rssi) {

    IBeacon iBeacon = new IBeacon();

    if (((int) scanData[5] & 0xff) == 0x4c &&
        ((int) scanData[6] & 0xff) == 0x00 &&
        ((int) scanData[7] & 0xff) == 0x02 &&
        ((int) scanData[8] & 0xff) == 0x15) {
      // yes!  This is an iBeacon
      iBeacon.major = processMajorminorBytes(scanData[25], scanData[26]);
      iBeacon.minor = processMajorminorBytes(scanData[27], scanData[28]);
      iBeacon.txPower = (int) scanData[29]; // this one is signed
      iBeacon.rssi = rssi;
    } else if (((int) scanData[5] & 0xff) == 0x2d &&
        ((int) scanData[6] & 0xff) == 0x24 &&
        ((int) scanData[7] & 0xff) == 0xbf &&
        ((int) scanData[8] & 0xff) == 0x16) {
      // this is an Estimote beacon
      iBeacon.major = 0;
      iBeacon.minor = 0;
      iBeacon.proximityUuid = "00000000-0000-0000-0000-000000000000";
      iBeacon.txPower = -55;
      return iBeacon;
    } else {
      // This is not an iBeacon
      return null;
    }

    byte[] proximityUuidBytes = new byte[16];
    System.arraycopy(scanData, 9, proximityUuidBytes, 0, 16);
    String hexString = bytesToHex(proximityUuidBytes);
    StringBuilder sb = new StringBuilder();
    sb.append(hexString.substring(0, 8))
        .append('-')
        .append(hexString.substring(8, 12))
        .append('-')
        .append(hexString.substring(12, 16))
        .append('-')
        .append(hexString.substring(16, 20))
        .append('-')
        .append(hexString.substring(20, 32));

    iBeacon.proximityUuid = sb.toString();

    return iBeacon;
  }

  protected IBeacon(IBeacon otherIBeacon) {
    this.major = otherIBeacon.major;
    this.minor = otherIBeacon.minor;
    this.accuracy = otherIBeacon.accuracy;
    this.proximity = otherIBeacon.proximity;
    this.rssi = otherIBeacon.rssi;
    this.proximityUuid = otherIBeacon.proximityUuid;
    this.txPower = otherIBeacon.txPower;
  }

  protected IBeacon() {

  }

  protected static double calculateAccuracy(int txPower, double rssi) {
    if (rssi == 0) {
      return -1.0; // if we cannot determine accuracy, return -1.
    }

    Log.d(TAG, "calculating accuracy based on rssi of " + rssi);

    double ratio = rssi * 1.0 / txPower;
    if (ratio < 1.0) {
      return Math.pow(ratio, 10);
    } else {
      double accuracy = (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
      Log.d(TAG, " avg rssi: " + rssi + " accuracy: " + accuracy);
      return accuracy;
    }
  }

  protected static int calculateProximity(double accuracy) {
    if (accuracy < 0) {
      return PROXIMITY_UNKNOWN;
      // is this correct?  does proximity only show unknown when accuracy is negative?  I have seen cases where it returns unknown when
      // accuracy is -1;
    }
    if (accuracy < 0.5) {
      return IBeacon.PROXIMITY_IMMEDIATE;
    }
    // forums say 3.0 is the near/far threshold, but it looks to be based on experience that this is 4.0
    if (accuracy <= 4.0) {
      return IBeacon.PROXIMITY_NEAR;
    }
    // if it is > 4.0 meters, call it far
    return IBeacon.PROXIMITY_FAR;
  }

  private static String bytesToHex(byte[] bytes) {
    char[] hexChars = new char[bytes.length * 2];
    int v;
    for (int j = 0; j < bytes.length; j++) {
      v = bytes[j] & 0xFF;
      hexChars[j * 2] = hexArray[v >>> 4];
      hexChars[j * 2 + 1] = hexArray[v & 0x0F];
    }
    return new String(hexChars);
  }

  private static int processMajorminorBytes(byte byte1, byte byte2){
    return (byte1 & 0xff) * 0x100 + (byte2 & 0xff);
  }
}
