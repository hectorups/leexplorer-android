package com.leexplorer.app.util;

import android.content.Context;
import com.leexplorer.app.R;

public class RippleClick {
  Context context;

  public RippleClick(Context context) {
    this.context = context;
  }

  public void run(Runnable runnable) {
    final android.os.Handler handler = new android.os.Handler();
    handler.postDelayed(runnable, context.getResources().getInteger(R.integer.ripple_duration));
  }
}
