package com.leexplorer.app.events.artworks;

import com.leexplorer.app.models.Gallery;

public class LoadArtworksEvent {
  private Gallery gallery;

  public LoadArtworksEvent(Gallery gallery) {
    this.gallery = gallery;
  }

  public Gallery getGallery() {
    return gallery;
  }
}
