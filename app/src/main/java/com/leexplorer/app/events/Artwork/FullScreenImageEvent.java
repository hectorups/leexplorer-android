package com.leexplorer.app.events.artwork;

import com.leexplorer.app.models.Artwork;

public class FullScreenImageEvent {
  private Artwork artwork;

  public FullScreenImageEvent(Artwork artwork) {
    this.artwork = artwork;
  }

  public Artwork getArtwork() {
    return artwork;
  }
}
