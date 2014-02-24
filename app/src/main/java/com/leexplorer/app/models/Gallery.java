package com.leexplorer.app.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by deepakdhiman on 2/18/14.
 */
@Table(name = "galleries")
public class Gallery extends Model implements Parcelable, Comparable<Gallery> {

    @Column(name="galleryId")
    private Long galleryId;
    @Column(name="name")
    private String name;
    @Column(name="image_url")
    private String imageUrl;
    @Column(name="address")
    private String address;
    @Column(name="type")
    private String type;
    @Column(name="price")
    private String price;
    @Column(name="language")
    private String language;
    @Column(name="hours")
    String hours;
    @Column(name="detailedPrice")
    String detailedPrice;
    @Column(name="facilities")
    String facilities;
    @Column(name="description")
    String description;

    public Gallery(){
        super();
    }

    public Gallery(Long galleryId, String name, String imageUrl, String address,
                    String type, String price, String language, String hours,
                    String detailedPrice, String facilities, String description) {
        this.galleryId = galleryId;
        this.name = name;
        this.imageUrl = imageUrl;
        this.address = address;
        this.type = type;
        this.price = price;
        this.language = language;
        this.hours = hours;
        this.detailedPrice = detailedPrice;
        this.facilities = facilities;
        this.description = description;
    }

    public static final Parcelable.Creator<Gallery> CREATOR = new Parcelable.Creator<Gallery>() {
        @Override
        public Gallery createFromParcel(Parcel in) {
            return new Gallery(in);
        }

        @Override
        public Gallery[] newArray(int size) {
            return new Gallery[size];
        }
    };

    protected Gallery(Parcel parcel){
        this.galleryId = parcel.readLong();
        this.name= parcel.readString();
        this.imageUrl = parcel.readString();
        this.address = parcel.readString();
        this.type = parcel.readString();
        this.price = parcel.readString();
        this.language = parcel.readString();
        this.hours = parcel.readString();
        this.detailedPrice = parcel.readString();
        this.facilities = parcel.readString();
        this.description = parcel.readString();
    }

    @Override
    public int compareTo(Gallery gallery) {
        return 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(galleryId);
        parcel.writeString(name);
        parcel.writeString(imageUrl);
        parcel.writeString(address);
        parcel.writeString(type);
        parcel.writeString(price);
        parcel.writeString(language);
        parcel.writeString(hours);
        parcel.writeString(detailedPrice);
        parcel.writeString(facilities);
        parcel.writeString(description);
    }

    public Long getGalleryId() {
        return galleryId;
    }

    public void setGalleryId(Long galleryId) {
        this.galleryId = galleryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
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

    public String getFacilities() {
        return facilities;
    }

    public void setFacilities(String facilities) {
        this.facilities = facilities;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
