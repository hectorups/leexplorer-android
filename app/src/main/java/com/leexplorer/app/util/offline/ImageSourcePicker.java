package com.leexplorer.app.util.offline;

import android.content.Context;
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

    FilePathGenerator.Version version = getVersionFromHeight(height);
    File file = new File(FilePathGenerator.getFileName(galleryId, mediaId, version));
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

    ImageResizer.Size bestSize = ImageResizer.getBestSize(maxSize, width, height);

    return getRequestCreator(galleryId, mediaId, bestSize.getWidth(), bestSize.getHeight(),
        Mode.Fill);
  }

  public String getUrl(String mediaId, int width, int height, Mode mode) {
    return cloudinary.url()
        .transformation(getTransformation(width, height, mode))
        .generate(mediaId + "." + FilePathGenerator.getBestFormat());
  }

  public String getUrl(String mediaId, int maxSize, int width, int height, Mode mode) {
    ImageResizer.Size bestSize = ImageResizer.getBestSize(maxSize, width, height);

    return getUrl(mediaId, bestSize.getWidth(), bestSize.getHeight(), mode);
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
        return "lfill";
      default:
        return "limit";
    }
  }

  private FilePathGenerator.Version getVersionFromHeight(int height) {
    int mediumHeight = (int) context.getResources().getDimension(R.dimen.thumbor_medium);

    if (height > mediumHeight) {
      return FilePathGenerator.Version.NORMAL;
    } else {
      return FilePathGenerator.Version.SMALL;
    }
  }
}
