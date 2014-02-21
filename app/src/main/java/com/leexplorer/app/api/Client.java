package com.leexplorer.app.api;

import com.leexplorer.app.api.models.Artwork;

import java.util.ArrayList;
import java.util.List;

import retrofit.RestAdapter;
import retrofit.http.GET;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.subscriptions.Subscriptions;

/**
 * Created by hectormonserrate on 20/02/14.
 */
public class Client {
    private static final String API_URL = "http://10.12.34.193:1321"; // Hector Office Phone
    //private static final String API_URL = "http://121.0.0.1:1321"; // Deepak

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


    public static Observable<ArrayList<com.leexplorer.app.models.Artwork>> getArtworksData(){
        return Observable.create(new Observable.OnSubscribeFunc<ArrayList<com.leexplorer.app.models.Artwork>>() {
            @Override
            public Subscription onSubscribe(Observer<? super ArrayList<com.leexplorer.app.models.Artwork>> observer) {
                try {

                    ArrayList<com.leexplorer.app.models.Artwork> artworks = new ArrayList<>();

                    for(Artwork aaw: getService().getArtworks()){
                        artworks.add(com.leexplorer.app.models.Artwork.fromJsonModel(aaw));
                    }

                    observer.onNext(artworks);

                    // @todo: Persist Artworks...

                    observer.onCompleted();
                } catch (Exception e) {
                    observer.onError(e);
                }

                return Subscriptions.empty();
            }
        });
    }


}
