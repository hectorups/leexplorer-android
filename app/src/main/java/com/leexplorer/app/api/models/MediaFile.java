package com.leexplorer.app.api.models;

import com.google.gson.annotations.SerializedName;

public class MediaFile {
  @SerializedName("public_id")
  public String publicId;

  public Integer bytes;
}
