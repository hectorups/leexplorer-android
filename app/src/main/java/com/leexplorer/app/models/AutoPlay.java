package com.leexplorer.app.models;

import java.util.ArrayList;
import java.util.List;

public class AutoPlay {
  private Gallery gallery;
  private List<Artwork> artworksPlayList;
  private List<Artwork> playedArtworks;
  private Artwork currentlyPlaying;

  public AutoPlay(Gallery gallery, List<Artwork> artworksPlayList) {
    this.gallery = gallery;
    this.artworksPlayList = artworksPlayList;
    playedArtworks = new ArrayList<>();
  }

  public void setAsPlayingArtwork(Artwork artwork) {
    if (!artworksPlayList.contains(artwork)) {
      resetPlayingArtwork();
    } else {
      if (!wasArtworkPlayed(artwork)) {
        playedArtworks.add(artwork);
      }
      currentlyPlaying = artwork;
    }
  }

  public boolean isFinished() {
    return artworksPlayList.size() == playedArtworks.size();
  }

  public void resetPlayingArtwork() {
    this.currentlyPlaying = null;
  }

  public boolean wasArtworkPlayed(Artwork artwork) {
    return playedArtworks.contains(artwork);
  }

  public boolean artworkBelongsToPlaylist(Artwork artwork) {
    return artworksPlayList.contains(artwork);
  }

  public Artwork getCurrentlyPlaying() {
    return currentlyPlaying;
  }

  public List<Artwork> getArtworksPlayList() {
    return artworksPlayList;
  }

  public void setArtworksPlayList(List<Artwork> artworksPlayList) {
    this.artworksPlayList = artworksPlayList;
  }

  public Gallery getGallery() {
    return gallery;
  }
}
