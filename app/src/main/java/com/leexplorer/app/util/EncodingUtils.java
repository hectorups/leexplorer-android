package com.leexplorer.app.util;

import java.math.BigInteger;
import java.util.Locale;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public final class EncodingUtils {

  public static String base16Encode(byte[] bytes) {
    return String.format(Locale.US, "%040x ", new BigInteger(1, bytes));
  }

  public static byte[] hmacSha1(byte[] message, String key) {
    try {
      Mac mac = Mac.getInstance("HmacSHA1");
      mac.init(new SecretKeySpec(key.getBytes(), "HmacSHA1"));
      return mac.doFinal(message);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}
