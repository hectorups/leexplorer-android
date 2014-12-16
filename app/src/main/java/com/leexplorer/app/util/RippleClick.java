package com.leexplorer.app.util;

import android.content.Context;
import com.leexplorer.app.R;

public class RippleClick {
  private final static double DURATION_COEF = 1.6;

  public static void run(Context context, Runnable runnable) {
    final android.os.Handler handler = new android.os.Handler();
    int duration = (int) Math.abs(
        context.getResources().getInteger(R.integer.ripple_duration) * DURATION_COEF);
    handler.postDelayed(runnable, duration);
  }
}
