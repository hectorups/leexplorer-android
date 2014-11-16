package com.leexplorer.app.util.offline;

import android.net.Uri;
import com.cloudinary.Cloudinary;
import java.io.File;

public class AudioSourcePicker {
  private Cloudinary cloudinary;

  public AudioSourcePicker(Cloudinary cloudinary) {
    this.cloudinary = cloudinary;
  }

  public Uri getUri(String galleryId, String audioId) {
    File file = new File(FilePathGenerator.getFileName(galleryId, audioId));
    if (file.exists()) {
      return Uri.parse(file.toString());
    }

    String url = cloudinary.url().resourceType("raw").type("upload").generate(audioId);

    return Uri.parse(url);
  }
}
