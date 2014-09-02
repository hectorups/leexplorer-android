package com.leexplorer.app.models;

import android.os.Parcel;
import android.os.Parcelable;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Table(name = "artworks")
public class Artwork extends Model implements Parcelable {

  private static final double OUT_OF_RANGE_VALUE = 9999.0;

  @Column(name = "name")
  private String name;
  @Column(name = "artworkId")
  private String artworkId;
  @Column(name = "mac", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
  private String mac;
  @Column(name = "description")
  private String description;
  @Column(name = "published_at")
  private Date publishedAt;
  @Column(name = "author")
  private String author;
  @Column(name = "image_url")
  private String imageUrl;
  @Column(name = "likes_count")
  private int likesCount;
  // if user has seen this beacon on his phone
  @Column(name = "known")
  private boolean known;
  @Column(name = "i_liked")
  private boolean iLiked;
  @Column(name = "audio_url")
  private String audioUrl;
  @Column(name = "gallery_id")
  private String galleryId;

  private double distance;

  public Artwork() {
    super();
  }

  public static Artwork fromJsonModel(com.leexplorer.app.api.models.Artwork apiArtwork) {
    Artwork artwork;

    String mac = apiArtwork.mac;

    artwork = findByMac(mac);

    if (artwork == null) {
      artwork = new Artwork();
    }

    artwork.distance = OUT_OF_RANGE_VALUE;
    artwork.artworkId = apiArtwork.artworkId;
    artwork.name = apiArtwork.name;
    artwork.mac = mac;
    artwork.description = apiArtwork.description;
    artwork.imageUrl = apiArtwork.imageUrl;
    artwork.author = apiArtwork.author;
    artwork.likesCount = apiArtwork.likesCount;
    artwork.publishedAt = setDateFromString(apiArtwork.publishedAt);
    artwork.audioUrl = apiArtwork.audioUrl;
    artwork.galleryId = apiArtwork.galleryId;

    return artwork;
  }

  private static Date setDateFromString(String date) {
    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
    sf.setLenient(true);
    try {
      return sf.parse(date);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return null;
  }

  // @todo: move this to gallery model
  public static ArrayList<Artwork> galleryArtworks(String galleryId) {
    List<Artwork> aws =
        new Select().from(Artwork.class).where("gallery_id = ?", galleryId).execute();
    return new ArrayList<>(aws);
  }

  public static Artwork findByMac(String mac) {
    return new Select().from(Artwork.class).where("mac = ?", mac).executeSingle();
  }

  public String getAudioUrl() {
    return audioUrl;
  }

  public void setAudioUrl(String audioUrl) {
    this.audioUrl = audioUrl;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getMac() {
    return mac;
  }

  public void setMac(String mac) {
    this.mac = mac;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Date getPublishedAt() {
    return (Date) publishedAt.clone();
  }

  public void setPublishedAt(Date publishedAt) {
    this.publishedAt = (Date) publishedAt.clone();
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public boolean isKnown() {
    return known;
  }

  public void setKnown(boolean known) {
    this.known = known;
  }

  public int getLikesCount() {
    return likesCount;
  }

  public void setiLiked(boolean iLiked) {
    this.iLiked = iLiked;
  }

  public void setLikesCount(int likesCount) {
    this.likesCount = likesCount;
  }

  public Distance getNormalizedDistance() {

    if (distance == OUT_OF_RANGE_VALUE) {
      return Distance.OUT_OF_RANGE;
    }

    switch (IBeacon.calculateProximity(distance)) {
      case IBeacon.PROXIMITY_UNKNOWN:
        return Distance.OUT_OF_RANGE;
      case IBeacon.PROXIMITY_IMMEDIATE:
        return Distance.IMMEDIATE;
      case IBeacon.PROXIMITY_NEAR:
        return Distance.CLOSE;
      default:
        return Distance.FAR;
    }
  }

  public double getDistance() {
    return distance;
  }

  public void setDistance(double distance) {
    this.distance = distance;
  }

  public boolean isiLiked() {
    return this.iLiked;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Artwork)) {
      return false;
    }

    Artwork artwork = (Artwork) o;

    if (!mac.contentEquals(artwork.mac)) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return mac.hashCode();
  }

  public void like() {
    this.iLiked = true;
    this.likesCount += 1;
    this.save();
  }

  public void unlike() {
    this.iLiked = false;
    this.likesCount -= 1;
    this.save();
  }

  public void resetDistance() {
    distance = OUT_OF_RANGE_VALUE;
  }

  public String getArtworkId() {
    return artworkId;
  }

  public void setArtworkId(String artworkId) {
    this.artworkId = artworkId;
  }

  public String getGalleryId() {
    return galleryId;
  }

    /*
     *  Parcelable Overrides
     */

  public void setGalleryId(String galleryId) {
    this.galleryId = galleryId;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @SuppressWarnings("unused")
  public static final Parcelable.Creator<Artwork> CREATOR = new Parcelable.Creator<Artwork>() {
    @Override
    public Artwork createFromParcel(Parcel in) {
      return new Artwork(in);
    }

    @Override
    public Artwork[] newArray(int size) {
      return new Artwork[size];
    }
  };

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeLong(getId());
    dest.writeString(name);
    dest.writeString(mac);
    dest.writeString(description);
    dest.writeString(imageUrl);
    dest.writeString(author);
    dest.writeInt(likesCount);
    dest.writeLong(publishedAt.getTime());
    dest.writeInt(iLiked ? 1 : 0);
    dest.writeInt(known ? 1 : 0);
    dest.writeString(audioUrl);
    dest.writeDouble(distance);
    dest.writeString(galleryId);
    dest.writeString(artworkId);
  }

  protected Artwork(Parcel in) {
    setId(in.readLong());
    name = in.readString();
    mac = in.readString();
    description = in.readString();
    imageUrl = in.readString();
    author = in.readString();
    likesCount = in.readInt();
    publishedAt = new Date(in.readLong());
    iLiked = in.readInt() == 1;
    known = in.readInt() == 1;
    audioUrl = in.readString();
    distance = in.readDouble();
    galleryId = in.readString();
    artworkId = in.readString();
  }

  public static enum Distance {
    IMMEDIATE,
    CLOSE,
    FAR,
    OUT_OF_RANGE
  }

  public static class ArtworkComparable implements Comparator<Artwork>, Serializable {
    @Override
    public int compare(Artwork aw1, Artwork aw2) {
      double distance1 = aw1.getDistance();
      double distance2 = aw2.getDistance();

      if (distance1 == distance2) {
        return 0;
      } else if (distance1 > distance2) {
        return 1;
      } else {
        return -1;
      }
    }
  }
}