package com.leexplorer.app.api.models;

import com.google.gson.annotations.SerializedName;

public class Artwork {

  public Integer major;

  public Integer minor;

  public String description;

  public ImageFile image;

  @SerializedName("gallery_id")
  public String galleryId;

  @SerializedName("id")
  public String artworkId;

  public String name;

  @SerializedName("published_at")
  public String publishedAt;

  public String author;

  @SerializedName("likes_count")
  public int likesCount;

  public MediaFile audio;
}
