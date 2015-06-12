package com.leexplorer.app.events.autoplay;

import com.leexplorer.app.models.Artwork;

public class AutoPlayAudioStartedEvent {
  private Artwork artwork;

  public AutoPlayAudioStartedEvent(Artwork artwork) {
    this.artwork = artwork;
  }

  public Artwork getArtwork() {
    return artwork;
  }
}
