package com.leexplorer.app.events;

import com.leexplorer.app.models.Artwork;

public class AudioStartedEvent {
  private boolean success;
  private Artwork artwork;

  public AudioStartedEvent(Artwork artwork, boolean success) {
    this.success = success;
    this.artwork = artwork;
  }

  public boolean isSuccess() {
    return success;
  }

  public Artwork getArtwork() {
    return artwork;
  }
}
