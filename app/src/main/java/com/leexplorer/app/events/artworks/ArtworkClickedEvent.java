package com.leexplorer.app.events.artworks;

import com.leexplorer.app.models.Artwork;
import java.util.List;

public class ArtworkClickedEvent {
  Artwork artwork;
  List<Artwork> artworks;

  public ArtworkClickedEvent(Artwork artwork, List<Artwork> artworks) {
    this.artwork = artwork;
    this.artworks = artworks;
  }

  public Artwork getArtwork() {
    return artwork;
  }

  public List<Artwork> getArtworks() {
    return artworks;
  }
}
