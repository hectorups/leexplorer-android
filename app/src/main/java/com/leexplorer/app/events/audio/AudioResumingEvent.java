package com.leexplorer.app.events.audio;

import com.leexplorer.app.models.Artwork;

public class AudioResumingEvent {
  private Artwork artwork;

  public AudioResumingEvent(Artwork artwork) {
    this.artwork = artwork;
  }

  public Artwork getArtwork() {
    return artwork;
  }
}
