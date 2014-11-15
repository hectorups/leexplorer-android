package com.leexplorer.app.models;

import android.os.Parcel;
import android.os.Parcelable;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.leexplorer.app.util.ble.Majorminor;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Table(name = "images")
public class Artwork extends Model implements Parcelable {

  private static final double OUT_OF_RANGE_VALUE = 9999.0;

  @Column(name = "name")
  private String name;
  @Column(name = "artworkId")
  private String artworkId;
  @Column(name = "majorminor", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
  private String majorminor;
  @Column(name = "description")
  private String description;
  @Column(name = "published_at")
  private Date publishedAt;
  @Column(name = "author")
  private String author;
  @Column(name = "image_id")
  private String imageId;
  @Column(name = "image_width")
  private int imageWidth;
  @Column(name = "image_height")
  private int imageHeight;
  @Column(name = "likes_count")
  private int likesCount;
  // if user has seen this beacon on his phone
  @Column(name = "known")
  private boolean known;
  @Column(name = "i_liked")
  private boolean iLiked;
  @Column(name = "audio_id")
  private String audioId;
  @Column(name = "gallery_id")
  private String galleryId;

  private double distance;

  public Artwork() {
    super();
  }

  public static Artwork fromJsonModel(com.leexplorer.app.api.models.Artwork apiArtwork) {
    Artwork artwork;

    String majorminor =
        String.valueOf(Majorminor.longFromMajorminor(apiArtwork.major, apiArtwork.minor));
    artwork = findByMajorminor(majorminor);

    if (artwork == null) {
      artwork = new Artwork();
    }

    artwork.distance = OUT_OF_RANGE_VALUE;
    artwork.artworkId = apiArtwork.artworkId;
    artwork.name = apiArtwork.name;
    artwork.majorminor = majorminor;
    artwork.description = apiArtwork.description;
    artwork.imageId = apiArtwork.image.publicId;
    artwork.author = apiArtwork.author;
    artwork.likesCount = apiArtwork.likesCount;
    artwork.publishedAt =
        apiArtwork.publishedAt != null ? setDateFromString(apiArtwork.publishedAt) : null;
    artwork.audioId = apiArtwork.audio != null ? apiArtwork.audio.publicId : null;
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

  public static Artwork findByMajorminor(String majorminor) {
    return new Select().from(Artwork.class).where("majorminor = ?", majorminor).executeSingle();
  }

  public String getAudioId() {
    return audioId;
  }

  public void setAudioId(String audioId) {
    this.audioId = audioId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getMajorminor() {
    return majorminor;
  }

  public void setMajorminor(String majorminor) {
    this.majorminor = majorminor;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Date getPublishedAt() {
    if (publishedAt == null) {
      return null;
    }
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

  public String getImageId() {
    return imageId;
  }

  public void setImageId(String imageId) {
    this.imageId = imageId;
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

  public int getImageWidth() {
    return imageWidth;
  }

  public void setImageWidth(int imageWidth) {
    this.imageWidth = imageWidth;
  }

  public int getImageHeight() {
    return imageHeight;
  }

  public void setImageHeight(int imageHeight) {
    this.imageHeight = imageHeight;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Artwork)) {
      return false;
    }

    Artwork artwork = (Artwork) o;

    if (!majorminor.contentEquals(artwork.majorminor)) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return majorminor.hashCode();
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
    dest.writeString(majorminor);
    dest.writeString(description);
    dest.writeString(imageId);
    dest.writeString(author);
    dest.writeInt(likesCount);
    dest.writeLong(publishedAt != null ? publishedAt.getTime() : 0);
    dest.writeInt(iLiked ? 1 : 0);
    dest.writeInt(known ? 1 : 0);
    dest.writeString(audioId);
    dest.writeDouble(distance);
    dest.writeString(galleryId);
    dest.writeString(artworkId);
  }

  protected Artwork(Parcel in) {
    setId(in.readLong());
    name = in.readString();
    majorminor = in.readString();
    description = in.readString();
    imageId = in.readString();
    author = in.readString();
    likesCount = in.readInt();
    long publishedAtRead = in.readLong();
    publishedAt = publishedAtRead == 0 ? null : new Date(publishedAtRead);
    iLiked = in.readInt() == 1;
    known = in.readInt() == 1;
    audioId = in.readString();
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