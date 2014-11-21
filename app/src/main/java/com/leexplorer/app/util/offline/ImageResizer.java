package com.leexplorer.app.util.offline;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class ImageResizer {
  public static final String TAG = "ImageResizer";

  public static void resizeImage(String filePath, String compressedFilePath, int maxSize) {
    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
    Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

    Size bestSize = getBestSize(maxSize, bitmap.getWidth(), bitmap.getHeight());

    Bitmap resizedBitmap =
        Bitmap.createScaledBitmap(bitmap, bestSize.getWidth(), bestSize.getHeight(), true);

    FileOutputStream out;
    try {
      out = new FileOutputStream(compressedFilePath);
      resizedBitmap.compress(getBestFormat(), 100, out);
    } catch (FileNotFoundException e) {
      Log.e(TAG, e.toString());
    }
  }

  public static Size getBestSize(int maxSize, int width, int height) {
    int bestWidth;
    int bestHeight;
    if (width > height) {
      bestWidth = Math.min(width, maxSize);
      bestHeight = Math.abs(height * bestWidth / width);
    } else {
      bestHeight = Math.min(height, maxSize);
      bestWidth = Math.abs(width * bestHeight / height);
    }

    return new Size(bestWidth, bestHeight);
  }

  static class Size {
    private int width;
    private int height;

    private Size(int width, int height) {
      this.width = width;
      this.height = height;
    }

    public int getWidth() {
      return width;
    }

    public int getHeight() {
      return height;
    }
  }

  private static Bitmap.CompressFormat getBestFormat() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
      return Bitmap.CompressFormat.JPEG;
    } else {
      return Bitmap.CompressFormat.WEBP;
    }
  }
}
