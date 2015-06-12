package com.leexplorer.app.events.audio;

import com.leexplorer.app.models.Artwork;
import com.leexplorer.app.services.MediaPlayerService.Status;

public class AudioProgressEvent {
  private Artwork artwork;
  private long totalDuration;
  private long currentDuration;
  private Status status;

  public AudioProgressEvent(Artwork artwork, long totalDuration, long currentDuration,
      Status status) {
    this.artwork = artwork;
    this.totalDuration = totalDuration;
    this.currentDuration = currentDuration;
    this.status = status;
  }

  public Status getStatus() {
    return status;
  }

  public Artwork getArtwork() {
    return artwork;
  }

  public void setArtwork(Artwork artwork) {
    this.artwork = artwork;
  }

  public long getTotalDuration() {
    return totalDuration;
  }

  public void setTotalDuration(long totalDuration) {
    this.totalDuration = totalDuration;
  }

  public long getCurrentDuration() {
    return currentDuration;
  }

  public void setCurrentDuration(long currentDuration) {
    this.currentDuration = currentDuration;
  }
}
