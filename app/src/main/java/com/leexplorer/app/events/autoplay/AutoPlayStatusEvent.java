package com.leexplorer.app.events.autoplay;

import com.leexplorer.app.models.Gallery;
import com.leexplorer.app.services.AutoPlayService;

public class AutoPlayStatusEvent {
  private Gallery gallery;
  private AutoPlayService.Status status;

  public AutoPlayStatusEvent(Gallery gallery, AutoPlayService.Status status) {
    this.gallery = gallery;
    this.status = status;
  }

  public Gallery getGallery() {
    return gallery;
  }

  public AutoPlayService.Status getStatus() {
    return status;
  }
}
