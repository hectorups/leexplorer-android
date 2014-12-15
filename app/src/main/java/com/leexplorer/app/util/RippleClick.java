package com.leexplorer.app.util;

import android.content.Context;
import com.leexplorer.app.R;

public class RippleClick {
  public static void run(Context context, Runnable runnable) {
    final android.os.Handler handler = new android.os.Handler();
    int duration =
        (int) Math.abs(context.getResources().getInteger(R.integer.ripple_duration) * 1.6);
    handler.postDelayed(runnable, duration);
  }
}
