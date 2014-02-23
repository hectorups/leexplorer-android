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

    public Gallery(){
        super();
    }

    public Gallery(String name, String imageUrl, String address, String type, String price) {
        this.name = name;

        this.imageUrl = imageUrl;
        this.address = address;
        this.type = type;
        this.price = price;
    }

    protected Gallery(Parcel parcel){
        this.galleryId = parcel.readLong();
        this.name= parcel.readString();
        this.imageUrl = parcel.readString();
        this.address = parcel.readString();
        this.type = parcel.readString();
        this.price = parcel.readString();
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
}
