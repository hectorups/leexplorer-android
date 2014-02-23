package com.leexplorer.app.api.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hectormonserrate on 20/02/14.
 */
public class Artwork {

    public String mac;

    public String description;

    @SerializedName("image_url")
    public String imageUrl;

    @SerializedName("gallery_id")
    public String galleryId;

    public String name;

    @SerializedName("published_at")
    public String publishedAt;

    public String author;

    @SerializedName("likes_count")
    public int likesCount;

    @SerializedName("audio_url")
    public String audioUrl;

}   
