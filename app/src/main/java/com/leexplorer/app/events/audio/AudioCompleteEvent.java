package com.leexplorer.app.events.audio;

import com.leexplorer.app.models.Artwork;

public class AudioCompleteEvent {
  Artwork artwork;

  public AudioCompleteEvent(Artwork artwork) {
    this.artwork = artwork;
  }

  public Artwork getArtwork() {
    return artwork;
  }

  public void setArtwork(Artwork artwork) {
    this.artwork = artwork;
  }
}
