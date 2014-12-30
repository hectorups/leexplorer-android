package com.leexplorer.app.util.offline;

import android.util.Log;
import com.squareup.okhttp.OkUrlFactory;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileDownloader {
  public static final String TAG = "FileDownloader";
  OkUrlFactory urlFactory;

  public interface Callbacks {
    void publishContent(int status);
  }

  public FileDownloader(OkUrlFactory client) {
    this.urlFactory = client;
  }

  public void downloadToFile(String filePath, URL url, Callbacks callbacks) throws IOException {
    Log.d(TAG, "DOWNLOADING " + url.toString() + " to " + filePath);

    FileOutputStream fos = null;
    InputStream in = null;

    try {

      fos = new FileOutputStream(filePath);
      HttpURLConnection connection = urlFactory.open(url);

      int totalBytes = connection.getContentLength();
      in = connection.getInputStream();
      byte data[] = new byte[1024];

      int count;
      int totalDownloaded = 0;
      while ((count = in.read(data)) != -1) {
        fos.write(data, 0, count);
        totalDownloaded += count;
        if (callbacks != null) {
          callbacks.publishContent(totalDownloaded * 100 / totalBytes);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (fos != null) {
        fos.close();
      }

      if (in != null) {
        in.close();
      }
    }
  }
}
