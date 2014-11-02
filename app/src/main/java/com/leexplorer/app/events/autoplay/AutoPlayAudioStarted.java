package com.leexplorer.app.events.autoplay;

import com.leexplorer.app.models.Artwork;

public class AutoPlayAudioStarted {
  private Artwork artwork;

  public AutoPlayAudioStarted(Artwork artwork) {
    this.artwork = artwork;
  }

  public Artwork getArtwork() {
    return artwork;
  }

}
