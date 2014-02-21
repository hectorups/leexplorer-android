package com.leexplorer.app.api;

import com.leexplorer.app.api.models.Artwork;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by hectormonserrate on 20/02/14.
 */
public interface LeexplorerService {
    @GET("/artwork")
    List<Artwork> getArtworks();

    @POST("/artwork/like/{value}")
    void likeArtwork(@Path("value") int value, Callback<Void> cb);

}
