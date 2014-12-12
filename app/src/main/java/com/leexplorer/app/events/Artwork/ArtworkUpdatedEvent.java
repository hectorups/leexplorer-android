package com.leexplorer.app.events.artwork;

import com.leexplorer.app.models.Artwork;

/**
 * Created by hectormonserrate on 08/06/14.
 */
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
