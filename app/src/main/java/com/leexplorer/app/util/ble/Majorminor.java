package com.leexplorer.app.util.ble;

import java.nio.ByteBuffer;

public class Majorminor {
  public static byte[] arrayOfBytesFromMajorminor(int major, int minor) {
    byte[] majorBytes = ByteBuffer.allocate(4).putInt(major).array();
    byte[] minorBytes = ByteBuffer.allocate(4).putInt(minor).array();
    byte[] combined = new byte[majorBytes.length + minorBytes.length];
    System.arraycopy(majorBytes, 0, combined, 0, majorBytes.length);
    System.arraycopy(minorBytes, 0, combined, majorBytes.length, minorBytes.length);

    return combined;
  }

  public static long longFromArrayOfBytes(byte[] array) {
    long value = 0;
    for (int i = 0; i < array.length; i++) {
      value = (value << 8) + array[i] & 0xff;
    }

    return value;
  }

  public static long longFromMajorminor(int major, int minor) {
    return longFromArrayOfBytes(arrayOfBytesFromMajorminor(major, minor));
  }
}
