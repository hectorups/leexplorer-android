package com.leexplorer.app.util;

import android.content.Context;
import com.leexplorer.app.R;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by hectormonserrate on 15/02/14.
 */
public class ArtDate {
  private static Context context;

  public static void initialize(Context c) {
    context = c;
  }

  public static String shortDate(Date date) {
    return formatDate(date, "yyyy");
  }

  private static String formatDate(Date date, String format) {
    String formatedString;

    if (date == null) {
      return context.getResources().getString(R.string.unknown_date);
    }

    SimpleDateFormat sf = new SimpleDateFormat(format);
    formatedString = sf.format(date);

    return formatedString;
  }
}
