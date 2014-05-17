package com.leexplorer.app.util.offline;

import android.net.Uri;
import java.io.File;

/**
 * Created by hectormonserrate on 16/05/14.
 */
public class AudioSourcePicker {

  public static Uri getUri(String galleryId, String url) {
    File file = new File(FilePathGenerator.getFileName(galleryId, url));
    if (file.exists()) {
      return Uri.parse(file.toString());
    }

    return Uri.parse(url);
  }
}
