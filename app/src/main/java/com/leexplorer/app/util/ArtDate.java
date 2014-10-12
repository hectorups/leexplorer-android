package com.leexplorer.app.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ArtDate {

  public static String shortDate(Date date) {
    return formatDate(date, "yyyy");
  }

  private static String formatDate(Date date, String format) {
    String formatedString;

    if (date == null) {
      return "";
    }

    SimpleDateFormat sf = new SimpleDateFormat(format);
    formatedString = sf.format(date);

    return formatedString;
  }
}
