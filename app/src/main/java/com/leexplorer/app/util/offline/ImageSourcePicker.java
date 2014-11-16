package com.leexplorer.app.util.offline;

import android.content.Context;
import android.os.Build;
import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.leexplorer.app.R;
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

  public enum Mode {
    Fill, Limit
  }

  public RequestCreator getRequestCreator(Artwork artwork, int bucketResourceId) {
    return getRequestCreator(artwork.getGalleryId(), artwork.getImageId(), bucketResourceId);
  }

  public RequestCreator getRequestCreator(Artwork artwork) {
    int bucket = (int) context.getResources().getDimension(R.dimen.thumbor_xlarge);

    return getRequestCreator(artwork.getGalleryId(), artwork.getImageId(), bucket,
        artwork.getImageWidth(), artwork.getImageHeight());
  }

  public RequestCreator getRequestCreator(String galleryId, String mediaId, int width, int height,
      Mode mode) {

    File file = new File(FilePathGenerator.getFileName(galleryId, mediaId));
    if (file.exists()) {
      return picasso.load(file);
    }

    return picasso.load(getUrl(mediaId, width, height, mode));
  }

  public RequestCreator getRequestCreator(String galleryId, String mediaId, int bucketResourceId) {
    int bucket = (int) context.getResources().getDimension(bucketResourceId);
    return getRequestCreator(galleryId, mediaId, bucket, bucket, Mode.Limit);
  }

  public RequestCreator getRequestCreator(String galleryId, String mediaId, int maxSize, int width,
      int height) {

    int bestWidth;
    int bestHeight;
    if (width > height) {
      bestWidth = Math.min(width, maxSize);
      bestHeight = Math.abs(height * bestWidth / width);
    } else {
      bestHeight = Math.min(height, maxSize);
      bestWidth = Math.abs(width * bestHeight / height);
    }

    return getRequestCreator(galleryId, mediaId, bestWidth, bestHeight, Mode.Fill);
  }

  public String getUrl(String mediaId, int width, int height, Mode mode) {
    return cloudinary.url()
        .transformation(getTransformation(width, height, mode))
        .generate(mediaId + "." + getBestFormat());
  }

  public Transformation getTransformation(int width, int height, Mode mode) {
    Transformation transformation = new Transformation().crop(modeString(mode));

    if (width != 0) {
      transformation.width(width);
    }

    if (height != 0) {
      transformation.height(height);
    }

    return transformation;
  }

  private String modeString(Mode mode) {
    switch (mode) {
      case Fill:
        return "fill";
      default:
        return "limit";
    }
  }

  private String getBestFormat() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
      return "jpg";
    } else {
      return "webp";
    }
  }
}
