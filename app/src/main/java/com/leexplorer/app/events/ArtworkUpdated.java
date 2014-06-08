package com.leexplorer.app.events;

import com.leexplorer.app.models.Artwork;

/**
 * Created by hectormonserrate on 08/06/14.
 */
public class ArtworkUpdated {
  private Artwork artwork;

  public ArtworkUpdated(Artwork artwork) {
    this.artwork = artwork;
  }

  public Artwork getArtwork() {
    return artwork;
  }

  public void setArtwork(Artwork artwork) {
    this.artwork = artwork;
  }
}
