package com.leexplorer.app.util.transformations;

import android.graphics.Bitmap;

public class AspectRationDummyTransformation implements com.squareup.picasso.Transformation {
  private final double size;


  public AspectRationDummyTransformation(double size) {
    this.size = size;
  }

  @Override
  public Bitmap transform(final Bitmap bitmap) {
    return bitmap;
  }

  @Override
  public String key() {
    return "SizeDummyTransformation:" + size;
  }
}