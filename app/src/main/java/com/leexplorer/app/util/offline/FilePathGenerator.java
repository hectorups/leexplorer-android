package com.leexplorer.app.util.offline;

import android.os.Build;
import android.os.Environment;
import android.util.Log;
import com.leexplorer.app.core.AppConstants;
import java.io.File;

public class FilePathGenerator {
  public static final String TAG = "FilePathGenerator";

  public static File appDirectory() {
    return new File(Environment.getExternalStorageDirectory() + "/" + AppConstants.APP_FOLDER);
  }

  public static void createAppDicrectoryIfNecessary() {
    File testDirectory = appDirectory();
    if (!testDirectory.exists() && !testDirectory.mkdir()) {
      Log.e(TAG, "Directory couldnt be created");
    }
  }

  public static File galleryDirectory(String galleryId) {
    return new File(appDirectory().toString() + "/" + galleryId);
  }

  public static void createGalleryDicrectoryIfNecessary(String galleryId) {
    File galleryDirectory = galleryDirectory(galleryId);
    if (!galleryDirectory.exists() && !galleryDirectory.mkdir()) {
      Log.e(TAG, "Directory couldnt be created");
    }
  }

  public static boolean isGalleryDownloaded(String galleryId) {
    File galleryDirectory = galleryDirectory(galleryId);
    if (galleryDirectory.exists() && galleryDirectory.listFiles().length > 0) {
      return true;
    }

    return false;
  }

  private static String getFileName(String galleryId, String mediaId, String prefix) {
    createGalleryDicrectoryIfNecessary(galleryId);
    return galleryDirectory(galleryId) + "/" + prefix + mediaId + "." + getBestFormat();
  }

  public static String getFileName(String galleryId, String mediaId, Version version) {
    return getFileName(galleryId, mediaId, version == Version.SMALL ? "s_" : "");
  }

  public static String getFileName(String galleryId, String mediaId) {
    return getFileName(galleryId, mediaId, Version.NORMAL);
  }

  public static String getBestFormat() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
      return "jpg";
    } else {
      return "webp";
    }
  }

  public enum Version {
    NORMAL, SMALL
  }
}
