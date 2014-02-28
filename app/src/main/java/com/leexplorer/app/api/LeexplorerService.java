package com.leexplorer.app.api;

import com.leexplorer.app.api.models.Artwork;
import com.leexplorer.app.api.models.Gallery;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by hectormonserrate on 20/02/14.
 */
public interface LeexplorerService {
    @GET("/artwork/:id")
    Artwork getArtwork();

    @GET("/gallery/{id}/artworks")
    List<Artwork> getArtworks(@Path("id") String galleryId);

    @GET("/gallery")
    List<Gallery> getGalleries();

    @POST("/artwork/like/{value}")
    void likeArtwork(@Path("value") int value, Callback<Void> cb);

}
