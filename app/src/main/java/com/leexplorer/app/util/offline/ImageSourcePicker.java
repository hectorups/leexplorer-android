package com.leexplorer.app.util.offline;

import android.content.Context;
import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.leexplorer.app.core.LeexplorerApplication;
import com.leexplorer.app.models.Artwork;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import java.io.File;

public class ImageSourcePicker {
  private Context context;
  private Cloudinary cloudinary;
  private Picasso picasso;

  public ImageSourcePicker(LeexplorerApplication context, Picasso picasso, Cloudinary cloudinary) {
    this.context = context;
    this.picasso = picasso;
    this.cloudinary = cloudinary;
  }

  public RequestCreator getRequestCreator(Artwork aw, int bucketResourceId) {
    return getRequestCreator(aw.getGalleryId(), aw.getImageId(), bucketResourceId);
  }

  public RequestCreator getRequestCreator(String galleryId, String mediaId, int bucketResourceId) {
    File file = new File(FilePathGenerator.getFileName(galleryId, mediaId));
    if (!file.exists()) {
      int thumborBucket = (int) context.getResources().getDimension(bucketResourceId);

      String thumborUrl = cloudinary.url()
          .transformation(
              new Transformation().width(thumborBucket).height(thumborBucket).crop("fill"))
          .generate(mediaId);

      return picasso.load(thumborUrl);
    }

    return picasso.load(file);
  }
}
