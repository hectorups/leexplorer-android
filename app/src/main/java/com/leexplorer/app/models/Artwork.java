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

/**
 * Created by hectormonserrate on 12/02/14.
 */
@Table(name = "artworks")
public class Artwork extends Model implements Parcelable {

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
    @Column(name = "name")
    private String name;
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
    private int distance;

    public Artwork() {
        super();
    }

    protected Artwork(Parcel in) {
        name = in.readString();
        mac = in.readString();
        description = in.readString();
        imageUrl = in.readString();
        author = in.readString();
        likesCount = in.readInt();
        publishedAt = new Date(in.readLong());
        iLiked = in.readInt() == 1 ? true : false;
        audioUrl = in.readString();
        distance = in.readInt();
        galleryId = in.readString();
    }

    public static Artwork fromJsonModel(com.leexplorer.app.api.models.Artwork jaw) {
        Artwork aw = null;

        String mac = jaw.mac;

        aw = findByMac(mac);

        if (aw == null) {
            aw = new Artwork();
        }

        aw.name = jaw.name;
        aw.mac = mac;
        aw.description = jaw.description;
        aw.imageUrl = jaw.imageUrl;
        aw.author = jaw.author;
        aw.likesCount = jaw.likesCount;
        aw.publishedAt = setDateFromString(jaw.publishedAt);
        aw.audioUrl = jaw.audioUrl;
        aw.galleryId = jaw.galleryId;

        return aw;
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
    public static ArrayList<Artwork> galleryArtworks() {
        List<Artwork> aws = new Select().from(Artwork.class).execute();
        return new ArrayList<>(aws);
    }

    public static Artwork findByMac(String mac) {
        return new Select()
                .from(Artwork.class)
                .where("mac = ?", mac)
                .executeSingle();
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

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public Distance getDistance() {
        if (distance == 0) {
            return Distance.OUT_OF_RANGE;
        }

        if (distance > -65) {
            return Distance.IMMEDIATE;
        } else if (distance > -80) {
            return Distance.CLOSE;
        } else {
            return Distance.FAR;
        }
    }

    public void setDistance(int distance) {
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
        if (!super.equals(o)) {
            return false;
        }

        Artwork artwork = (Artwork) o;

        if (!mac.equals(artwork.mac)) {
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
        //this.save();
    }

    public void unlike() {
        this.iLiked = false;
        this.likesCount -= 1;
        //this.save();
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

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(mac);
        dest.writeString(description);
        dest.writeString(imageUrl);
        dest.writeString(author);
        dest.writeInt(likesCount);
        dest.writeLong(publishedAt.getTime());
        dest.writeInt(iLiked ? 1 : 0);
        dest.writeString(audioUrl);
        dest.writeInt(distance);
        dest.writeString(galleryId);
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
            int distance1 = Math.abs(aw1.distance == 0 ? -999 : aw1.distance);
            int distance2 = Math.abs(aw2.distance == 0 ? -999 : aw2.distance);
            return distance1 - distance2;
        }
    }
}