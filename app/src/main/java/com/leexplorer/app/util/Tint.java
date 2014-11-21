package com.leexplorer.app.util;

import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;

public class Tint {
  public static Drawable getTintedDrawable(Resources res, @DrawableRes int drawableResId,
      @ColorRes int colorResId) {

    Drawable drawable = res.getDrawable(drawableResId);
    int color = res.getColor(colorResId);
    drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
    return drawable;
  }
}
