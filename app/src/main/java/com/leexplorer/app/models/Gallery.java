package com.leexplorer.app.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by deepakdhiman on 2/18/14.
 */
@Table(name = "galleries")
public class Gallery extends Model implements Parcelable, Comparable<Gallery> {

    @Column(name="gallery_id")
    private Long galleryId;
    @Column(name="name")
    private String name;
    @Column(name="address")
    private String address;
    @Column(name="type")
    private String type;
    @Column(name="price")
    private float price;
    @Column(name="languages")
    private String languages;
    @Column(name="hours")
    private String hours;
    @Column(name="detailed_price")
    private String detailedPrice;
    @Column(name="facilities")
    private String facilities;
    @Column(name="description")
    private String description;

    @Column(name="latitude")
    private long latitude;

    @Column(name="longitude")
    private long longitude;


    public Gallery(){

        super();
    }

    public Gallery(Long galleryId, String name, String imageUrl, String address,
                    String type, float price, String languages, String hours,
                    String detailedPrice, ArrayList<String> facilities, String description) {
        this.galleryId = galleryId;
        this.name = name;
        this.address = address;
        this.type = type;
        this.price = price;
        this.languages = languages;
        this.hours = hours;
        this.detailedPrice = detailedPrice;
        this.setFacilities(facilities);
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
        this.address = parcel.readString();
        this.type = parcel.readString();
        this.price = parcel.readFloat();
        this.languages = parcel.readString();
        this.hours = parcel.readString();
        this.detailedPrice = parcel.readString();
        this.facilities = parcel.readString();
        this.description = parcel.readString();
        this.latitude = parcel.readLong();
        this.longitude = parcel.readLong();
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

    public String getLanguages() {
        return languages;
    }

    public void setLanguages(String languages) {
        this.languages = languages;
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
        Type collectionType = new TypeToken<ArrayList<String>>(){}.getType();
        return gson.fromJson(this.facilities, collectionType);
    }

    public void setFacilities(ArrayList<String> facilities) {
        Gson gson = new Gson();
        this.facilities  = gson.toJson(facilities);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getLatitude() {
        return latitude;
    }

    public void setLatitude(long latitude) {
        this.latitude = latitude;
    }

    public long getLongitude() {
        return longitude;
    }

    public void setLongitude(long longitude) {
        this.longitude = longitude;
    }
}
