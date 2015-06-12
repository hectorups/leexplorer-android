package com.leexplorer.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public final class PreferenceUtils {

  private PreferenceUtils() {
  }

  public static void putDouble(Context context, String key, Double value) {
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    sharedPreferences.edit().putLong(key, Double.doubleToRawLongBits(value)).apply();
  }

  public static Double getDouble(Context context, String key, Double defaultValue) {
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    if (!sharedPreferences.contains(key)) {
      return defaultValue;
    }
    return Double.longBitsToDouble(sharedPreferences.getLong(key, 0));
  }
}
