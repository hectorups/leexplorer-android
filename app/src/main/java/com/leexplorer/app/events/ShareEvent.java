package com.leexplorer.app.events;

import android.net.Uri;

public class ShareEvent {
  private String title;
  private String description;
  private String url;
  private Uri bmpUri;
  private String type;

  public ShareEvent(String title, String description, String imageUrl,
      String type, Uri bmpUri) {
    this.title = title;
    this.description = description;
    this.url = imageUrl;
    this.type = type;
    this.bmpUri = bmpUri;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public String getUrl() {
    return url;
  }

  public String getType() {
    return type;
  }

  public Uri getBmpUri() {
    return bmpUri;
  }
}
