package com.leexplorer.app.events.autoplay;

import com.leexplorer.app.models.Artwork;

public class AutoPlayReadyToPlayEvent {
  private Artwork artwork;

  public AutoPlayReadyToPlayEvent(Artwork artwork) {
    this.artwork = artwork;
  }

  public Artwork getArtwork() {
    return artwork;
  }
}
