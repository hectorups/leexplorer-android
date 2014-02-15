package com.leexplorer.app.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by hectormonserrate on 12/02/14.
 */
@Table(name = "artworks")
public class Artwork extends Model implements Parcelable {

    @Column(name="name")
    private String name;

    @Column(name="mac", unique=true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private String mac;

    @Column(name="description")
    private String description;

    @Column(name="published_at")
    private Date publishedAt;

    @Column(name="author")
    private String author;

    @Column(name="image_url")
    private String imageUrl;

    @Column(name="likes_count")
    private int likesCount;

    // if user has seen this beacon on his phone
    @Column(name="known")
    private boolean known;

    public enum Distance {
        CLOSE, MEDIUM, FAR, OUT_OF_RANGE
    }
    private Distance distance;

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
        return publishedAt;
    }

    public void setPublishedAt(Date publishedAt) {
        this.publishedAt = publishedAt;
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

    public int getLikesCount(){
        return likesCount;
    }

    public void setLikesCount(int likesCount){
        this.likesCount = likesCount;
    }

    public Artwork(){
        super();
        distance = Distance.OUT_OF_RANGE;
    }

    public static Artwork fromJson(JSONObject json){
        Artwork aw = null;

        try{
            String mac = json.getString("mac");

            // aw = findByMac(mac);

            if(aw == null) aw = new Artwork();
            aw.name = json.getString("name");
            aw.mac = mac;
            aw.description = json.getString("description");
            aw.imageUrl = json.getString("image_url");
            aw.author = json.getString("author");
            aw.likesCount = json.getInt("likes_count");
            aw.publishedAt = setDateFromString(json.getString("published_at"));
        } catch(Exception e){
            e.printStackTrace();
        }

        return aw;
    }

    private static Date setDateFromString(String date) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        sf.setLenient(true);
        try{
            return sf.parse(date);
        } catch (ParseException e) { e.printStackTrace(); }
        return null;
    }


    /*
     *  Parcelable Overrides
     */

    protected Artwork(Parcel in) {
        name = in.readString();
        mac = in.readString();
        description = in.readString();
        imageUrl = in.readString();
        author = in.readString();
        likesCount = in.readInt();
        publishedAt = new Date(in.readLong());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(mac);
        dest.writeString(description);
        dest.writeString(imageUrl);
        dest.writeString(author);
        dest.writeInt(likesCount);
        dest.writeLong(publishedAt.getTime());
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
}