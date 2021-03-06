package com.leexplorer.app.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.leexplorer.app.api.models.ImageFile;
import com.leexplorer.app.util.offline.FilePathGenerator;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Table(name = "galleries") public class Gallery extends Model implements Parcelable {

  @Column(name = "gallery_id", unique = true) private String galleryId;
  @Column(name = "name") private String name;
  @Column(name = "address") private String address;
  @Column(name = "type") private String type;
  @Column(name = "price") private float price;
  @Column(name = "languages") private String languages;
  @Column(name = "hours") private String hours;
  @Column(name = "detailed_price") private String detailedPrice;
  @Column(name = "facilities") private String facilities;
  @Column(name = "description") private String description;
  @Column(name = "latitude") private float latitude;
  @Column(name = "longitude") private float longitude;
  @Column(name = "was_seen") private boolean wasSeen;
  private double distanceFromCurrentLocation;

  private List<String> artworkImageIds;

  public Gallery() {
    super();
  }

  public Gallery(String galleryId, String name, String address, String type, float price,
      ArrayList<String> languages, String hours, String detailedPrice, ArrayList<String> facilities,
      String description) {
    this.galleryId = galleryId;
    this.name = name;
    this.address = address;
    this.type = type;
    this.price = price;
    this.setLanguages(languages);
    this.hours = hours;
    this.detailedPrice = detailedPrice;
    this.setFacilities(facilities);
    this.description = description;
  }

  public static Gallery fromApiModel(com.leexplorer.app.api.models.Gallery apiGallery) {

    Gallery gallery = findById(apiGallery.id);

    if (gallery == null) {
      gallery = new Gallery();
    }

    gallery.galleryId = apiGallery.id;
    gallery.name = apiGallery.name;
    gallery.address = apiGallery.address;
    gallery.type = apiGallery.type;
    gallery.price = apiGallery.priceReference;
    gallery.hours = apiGallery.hours;
    gallery.detailedPrice = apiGallery.priceDescription;
    gallery.setFacilities(new ArrayList<>(apiGallery.facilities));
    gallery.description = apiGallery.description;
    gallery.setLanguages(new ArrayList<>(apiGallery.languages));
    gallery.setLatitude(apiGallery.latitude);
    gallery.setLongitude(apiGallery.longitude);
    gallery.artworkImageIds = new ArrayList<>();
    for (ImageFile image : apiGallery.images) {
      gallery.artworkImageIds.add(image.publicId);
    }
    return gallery;
  }

  public static Gallery findById(String galleryId) {
    Gallery gallery =
        new Select().from(Gallery.class).where("gallery_id = ?", galleryId).executeSingle();

    if (gallery == null) {
      return null;
    }

    return gallery.fillWithImages();
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public boolean equals(Object o) {
    if (!(o instanceof Gallery)) {
      return false;
    }

    Gallery gallery = (Gallery) o;

    if (!galleryId.contentEquals(gallery.galleryId)) {
      return false;
    }

    return true;
  }

  @Override public int hashCode() {
    return galleryId.hashCode();
  }

  public List<String> getArtworkImageIds() {
    return artworkImageIds;
  }

  public void setArtworkImageIds(List<String> artworkImageIds) {
    this.artworkImageIds = artworkImageIds;
  }

  public String getGalleryId() {
    return galleryId;
  }

  public void setGalleryId(String galleryId) {
    this.galleryId = galleryId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public float getPrice() {
    return price;
  }

  public void setPrice(float price) {
    this.price = price;
  }

  public String getHours() {
    return hours;
  }

  public void setHours(String hours) {
    this.hours = hours;
  }

  public String getDetailedPrice() {
    return detailedPrice;
  }

  public void setDetailedPrice(String detailedPrice) {
    this.detailedPrice = detailedPrice;
  }

  public ArrayList<String> getFacilities() {
    Gson gson = new Gson();
    Type collectionType = new TypeToken<ArrayList<String>>() {
    }.getType();
    return gson.fromJson(this.facilities, collectionType);
  }

  public void setFacilities(ArrayList<String> facilities) {
    Gson gson = new Gson();
    this.facilities = gson.toJson(facilities);
  }

  public ArrayList<String> getLanguages() {
    Gson gson = new Gson();
    Type collectionType = new TypeToken<ArrayList<String>>() {
    }.getType();
    return gson.fromJson(this.languages, collectionType);
  }

  public void setLanguages(ArrayList<String> languages) {
    Gson gson = new Gson();
    this.languages = gson.toJson(languages);
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public float getLatitude() {
    return latitude;
  }

  public void setLatitude(float latitude) {
    this.latitude = latitude;
  }

  public float getLongitude() {
    return longitude;
  }

  public void setLongitude(float longitude) {
    this.longitude = longitude;
  }

  public boolean isWasSeen() {
    return wasSeen;
  }

  public void setWasSeen(boolean wasSeen) {
    this.wasSeen = wasSeen;
  }

  public double getDistanceFromCurrentLocation() {
    return distanceFromCurrentLocation;
  }

  public void setDistanceFromCurrentLocation(double distanceFromCurrentLocation) {
    this.distanceFromCurrentLocation = distanceFromCurrentLocation;
  }

  public String getMainImage() {
    return getArtworkImageIds().get(0);
  }

  public static List<Gallery> getAll() {
    List<Gallery> galleries = new Select().from(Gallery.class).execute();
    for (Gallery gallery : galleries) {
      gallery.fillWithImages();
    }
    return galleries;
  }

  private Gallery fillWithImages() {
    List<String> imageIds = new ArrayList<>();
    List<Artwork> artworks = Artwork.galleryArtworks(this.getGalleryId());
    for (Artwork artwork : artworks) {
      if (!TextUtils.isEmpty(artwork.getImageId())) {
        imageIds.add(artwork.getImageId());
      }
    }
    this.setArtworkImageIds(imageIds);

    return this;
  }

  public boolean isGalleryDownloaded() {
    return FilePathGenerator.isGalleryDownloaded(this.getGalleryId());
  }

  public static final Parcelable.Creator<Gallery> CREATOR = new Parcelable.Creator<Gallery>() {
    @Override public Gallery createFromParcel(Parcel in) {
      return new Gallery(in);
    }

    @Override public Gallery[] newArray(int size) {
      return new Gallery[size];
    }
  };

  @Override public void writeToParcel(Parcel parcel, int i) {
    parcel.writeString(galleryId);
    parcel.writeString(name);
    parcel.writeString(address);
    parcel.writeString(type);
    parcel.writeFloat(price);
    parcel.writeString(languages);
    parcel.writeString(hours);
    parcel.writeString(detailedPrice);
    parcel.writeString(facilities);
    parcel.writeString(description);
    parcel.writeFloat(latitude);
    parcel.writeFloat(longitude);
    parcel.writeList(artworkImageIds);
    parcel.writeInt(wasSeen ? 1 : 0);
  }

  protected Gallery(Parcel parcel) {
    this.galleryId = parcel.readString();
    this.name = parcel.readString();
    this.address = parcel.readString();
    this.type = parcel.readString();
    this.price = parcel.readFloat();
    this.languages = parcel.readString();
    this.hours = parcel.readString();
    this.detailedPrice = parcel.readString();
    this.facilities = parcel.readString();
    this.description = parcel.readString();
    this.latitude = parcel.readFloat();
    this.longitude = parcel.readFloat();
    this.artworkImageIds = parcel.readArrayList(null);
    this.wasSeen = parcel.readInt() == 1;
  }
}
