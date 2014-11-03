package com.leexplorer.app.events.autoplay;

import com.leexplorer.app.models.Gallery;

public class AutoPlayAudioFinishedEvent {
  private Gallery gallery;

  public AutoPlayAudioFinishedEvent(Gallery gallery) {
    this.gallery = gallery;
  }

  public Gallery getGallery() {
    return gallery;
  }
}
