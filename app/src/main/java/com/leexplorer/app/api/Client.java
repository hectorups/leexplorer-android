package com.leexplorer.app.api;

import com.leexplorer.app.BuildConfig;
import com.leexplorer.app.api.models.Artwork;
import com.leexplorer.app.models.Gallery;
import com.leexplorer.app.util.FakeData;

import java.util.ArrayList;

import retrofit.RestAdapter;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.subscriptions.Subscriptions;

/**
 * Created by hectormonserrate on 20/02/14.
 */
public class Client {
    //private static final String API_URL = "http://10.12.34.255:1321"; // Hector 1
    //private static final String API_URL = "http://10.0.0.4:1321"; // Hector 2
    private static final String API_URL = "http://107.170.66.79:1337"; // Digital Ocean

    private static LeexplorerService service;



    public static LeexplorerService getService(){

        if(service == null){
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(API_URL)
                    .build();

            service = restAdapter.create(LeexplorerService.class);
        }

        return service;
    }


    public static Observable<ArrayList<com.leexplorer.app.models.Artwork>> getArtworksData(final String galleryId){
        return Observable.create(new Observable.OnSubscribeFunc<ArrayList<com.leexplorer.app.models.Artwork>>() {
            @Override
            public Subscription onSubscribe(Observer<? super ArrayList<com.leexplorer.app.models.Artwork>> observer) {
                try {
                    ArrayList<com.leexplorer.app.models.Artwork> artworks = new ArrayList<>();

                    if( BuildConfig.FAKE_DATA ){
                        artworks = FakeData.getArtworks();
                    } else {
                        for(Artwork aaw: getService().getArtworks(galleryId)){
                            artworks.add(com.leexplorer.app.models.Artwork.fromJsonModel(aaw));
                        }
                    }

                    observer.onNext(artworks);

                    try{
                        // Persist Artworks...
                        for(com.leexplorer.app.models.Artwork aw: artworks){
                            aw.save();
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }

                    observer.onCompleted();
                } catch (Exception e) {
                    observer.onError(e);
                }

                return Subscriptions.empty();
            }
        });
    }

    public static Observable<ArrayList<Gallery>> getGalleriesData(){
        return Observable.create(new Observable.OnSubscribeFunc<ArrayList<com.leexplorer.app.models.Gallery>>() {
            @Override
            public Subscription onSubscribe(Observer<? super ArrayList<com.leexplorer.app.models.Gallery>> observer) {
                try {
                    ArrayList<Gallery> galleries = new ArrayList<>();


                    for(com.leexplorer.app.api.models.Gallery gallery: getService().getGalleries()){
                        galleries.add(Gallery.fromApiModel(gallery));
                    }

                    observer.onNext(galleries);

                    try{
                        // Persist Galleries...
                        for(Gallery gallery: galleries){
//                            gallery.save();
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }

                    observer.onCompleted();
                } catch (Exception e) {
                    observer.onError(e);
                }

                return Subscriptions.empty();
            }
        });
    }


}
