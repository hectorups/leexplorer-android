package com.leexplorer.app.util;

public class TextUtil {
  public static String capitalizeFirstLetter(String original) {
    if (original.length() == 0) {
      return original;
    }

    return original.substring(0, 1).toUpperCase() + original.substring(1);
  }
}
