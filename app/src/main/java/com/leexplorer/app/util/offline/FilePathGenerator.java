package com.leexplorer.app.util.offline;

import android.os.Environment;
import com.leexplorer.app.util.AppConstants;
import java.io.File;

/**
 * Created by hectormonserrate on 14/05/14.
 */
public class FilePathGenerator {

  public static File appDirectory() {
    return new File(Environment.getExternalStorageDirectory() + "/" + AppConstants.APP_FOLDER);
  }

  public static void checkAppDirectory() {
    File testDirectory = appDirectory();
    if (!testDirectory.exists()) {
      testDirectory.mkdir();
    }
  }

  public static File galleryDirectory(String galleryId){
    return new File( appDirectory().toString() + "/" + galleryId);
  }

  public static void checkGalleryDirectory(String galleryId) {
    File galleryDirectory = galleryDirectory(galleryId);
    if( !galleryDirectory.exists() ){
      galleryDirectory.mkdir();
    }
  }

  public static String getFileName(String galleryId, String url) {
    checkGalleryDirectory(galleryId);
    File theFile = new File(url);
    String fileName = theFile.getName().replaceAll("[^a-zA-Z0-9.-]", "_");
    return galleryDirectory(galleryId) + "/" + fileName;
  }
}
