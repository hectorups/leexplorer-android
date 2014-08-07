package com.leexplorer.app.util.offline;

import android.content.Context;
import com.leexplorer.app.core.LeexplorerApplication;
import com.leexplorer.app.models.Artwork;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.pollexor.Thumbor;
import java.io.File;

/**
 * Created by hectormonserrate on 13/05/14.
 *
 * Decides whether a resource needs to be taken offline or online
 */
public class ImageSourcePicker {
  private Context context;
  private Thumbor thumbor;
  private Picasso picasso;

  public ImageSourcePicker(LeexplorerApplication context, Picasso picasso, Thumbor thumbor) {
    this.context = context;
    this.picasso = picasso;
    this.thumbor = thumbor;
  }

  public RequestCreator getRequestCreator(Artwork aw, int bucketResourceId) {
    return getRequestCreator(aw.getGalleryId(), aw.getImageUrl(), bucketResourceId);
  }

  public RequestCreator getRequestCreator(String galleryId, String url, int bucketResourceId) {
    File file = new File(FilePathGenerator.getFileName(galleryId, url));
    if (!file.exists()) {
      int thumborBucket = (int) context.getResources().getDimension(bucketResourceId);

      String thumborUrl = thumbor.buildImage(url).resize(thumborBucket, 0).toUrl();

      return picasso.load(thumborUrl);
    }

    return picasso.load(file);
  }
}
