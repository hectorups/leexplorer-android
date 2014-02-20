package com.leexplorer.app.api;

import com.leexplorer.app.api.models.Artwork;

import java.util.List;

import retrofit.RestAdapter;
import retrofit.http.GET;

/**
 * Created by hectormonserrate on 20/02/14.
 */
public class Client {
    private static final String API_URL = "http://10.12.34.193:1321";

    private static LeexplorerService service;


    public static interface LeexplorerService {
        @GET("/artwork")
        List<Artwork> getArtworks();
    }

    public static LeexplorerService getService(){

        if(service == null){
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(API_URL)
                    .build();

            service = restAdapter.create(LeexplorerService.class);
        }

        return service;
    }


}
