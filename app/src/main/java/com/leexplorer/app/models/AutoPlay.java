package com.leexplorer.app.models;

import java.util.ArrayList;
import java.util.List;

public class AutoPlay {
  private Gallery gallery;
  private List<Artwork> artworksPlayList;
  private List<Artwork> playedArtworks;
  private Artwork currentlyPlaying;
  private State state;

  public enum State {
    Idle, WaitingConfirmation, Playing, Paused
  }

  public AutoPlay(Gallery gallery, List<Artwork> artworksPlayList) {
    this.gallery = gallery;
    this.artworksPlayList = artworksPlayList;
    playedArtworks = new ArrayList<>();
    state = State.Idle;
  }

  public void setAsPlayingArtwork(Artwork artwork) {
    resetPlayingArtwork();
    if (!belongsToPlaylist(artwork)) {
      return;
    }

    if (!wasArtworkPlayed(artwork)) {
      playedArtworks.add(artwork);
    }

    currentlyPlaying = artwork;
    state = State.WaitingConfirmation;
  }

  public void confirmedPlaying() {
    if (currentlyPlaying != null) {
      state = State.Playing;
    }
  }

  public boolean isFinished() {
    return artworksPlayList.size() == playedArtworks.size();
  }

  public void resetPlayingArtwork() {
    currentlyPlaying = null;
    state = State.Idle;
  }

  public boolean isOnPause() {
    return state == State.Paused;
  }

  public boolean isWaitingForConfirmation() {
    return state == State.WaitingConfirmation;
  }

  public void setOnPause(boolean onPause) {
    if (currentlyPlaying != null) {
      state = onPause ? State.Paused : State.Playing;
    }
  }

  public boolean wasArtworkPlayed(Artwork artwork) {
    return playedArtworks.contains(artwork);
  }

  public boolean belongsToPlaylist(Artwork artwork) {
    return artworksPlayList.contains(artwork);
  }

  public Artwork getCurrentlyPlaying() {
    return currentlyPlaying;
  }

  public List<Artwork> getArtworksPlayList() {
    return artworksPlayList;
  }

  public Gallery getGallery() {
    return gallery;
  }
}
