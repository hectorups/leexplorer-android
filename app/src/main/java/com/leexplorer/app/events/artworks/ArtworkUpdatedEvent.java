package com.leexplorer.app.events.artworks;

import com.leexplorer.app.models.Artwork;

public class ArtworkUpdatedEvent {
  private Artwork artwork;

  public ArtworkUpdatedEvent(Artwork artwork) {
    this.artwork = artwork;
  }

  public Artwork getArtwork() {
    return artwork;
  }

  public void setArtwork(Artwork artwork) {
    this.artwork = artwork;
  }
}
