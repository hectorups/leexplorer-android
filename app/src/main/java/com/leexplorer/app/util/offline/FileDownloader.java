package com.leexplorer.app.util.offline;

import android.util.Log;
import com.squareup.okhttp.OkHttpClient;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by hectormonserrate on 13/05/14.
 */
public class FileDownloader {
  public static final String TAG = "FileDownloader";
  OkHttpClient client;

  public interface Callbacks {
    void publishContent(int status);
  }

  public FileDownloader(OkHttpClient client) {
    this.client = client;
  }

  public void downloadToFile(String filePath, URL url, Callbacks callbacks) throws IOException {
    Log.d(TAG, "DOWNLOADING " + url.toString() + " to " + filePath);

    FileOutputStream fos = null;
    InputStream in = null;

    try {

      fos = new FileOutputStream(filePath);
      HttpURLConnection connection = client.open(url);

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
