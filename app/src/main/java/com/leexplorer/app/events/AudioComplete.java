package com.leexplorer.app.events;

import com.leexplorer.app.models.Artwork;

/**
 * Created by hectormonserrate on 26/07/14.
 */
public class AudioComplete {
  Artwork artwork;

  public AudioComplete(Artwork artwork) {
    this.artwork = artwork;
  }

  public Artwork getArtwork() {
    return artwork;
  }

  public void setArtwork(Artwork artwork) {
    this.artwork = artwork;
  }
}
