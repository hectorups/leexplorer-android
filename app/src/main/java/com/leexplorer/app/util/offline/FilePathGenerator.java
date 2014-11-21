package com.leexplorer.app.util.offline;

import android.os.Environment;
import android.util.Log;
import com.leexplorer.app.core.AppConstants;
import java.io.File;

public class FilePathGenerator {
  public static final String TAG = "FilePathGenerator";

  public static File appDirectory() {
    return new File(Environment.getExternalStorageDirectory() + "/" + AppConstants.APP_FOLDER);
  }

  public static void checkAppDirectory() {
    File testDirectory = appDirectory();
    if (!testDirectory.exists() && !testDirectory.mkdir()) {
      Log.e(TAG, "Directory couldnt be created");
    }
  }

  public static File galleryDirectory(String galleryId) {
    return new File(appDirectory().toString() + "/" + galleryId);
  }

  public static void checkGalleryDirectory(String galleryId) {
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

  private static String getFileName(String galleryId, String url, String prefix) {
    checkGalleryDirectory(galleryId);
    File theFile = new File(url);
    String fileName = theFile.getName().replaceAll("[^a-zA-Z0-9.-]", "_");
    return galleryDirectory(galleryId) + "/" + prefix + fileName;
  }

  public static String getFileName(String galleryId, String url, Version version) {
    return getFileName(galleryId, url, version == Version.SMALL ? "s_" : "");
  }

  public static String getFileName(String galleryId, String url) {
    return getFileName(galleryId, url, Version.NORMAL);
  }

  public enum Version {
    NORMAL, SMALL
  }
}
