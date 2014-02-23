package com.leexplorer.app.api;

import com.leexplorer.app.BuildConfig;
import com.leexplorer.app.api.models.Artwork;
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
    private static final String API_URL = "http://10.0.0.4:1321"; // Hector 2
    //private static final String API_URL = "http://121.0.0.1:1321"; // Deepak

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


    public static Observable<ArrayList<com.leexplorer.app.models.Artwork>> getArtworksData(){
        return Observable.create(new Observable.OnSubscribeFunc<ArrayList<com.leexplorer.app.models.Artwork>>() {
            @Override
            public Subscription onSubscribe(Observer<? super ArrayList<com.leexplorer.app.models.Artwork>> observer) {
                try {
                    ArrayList<com.leexplorer.app.models.Artwork> artworks = new ArrayList<>();

                    if( BuildConfig.FAKE_DATA ){
                        artworks = FakeData.getArtworks();
                    } else {
                        for(Artwork aaw: getService().getArtworks()){
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


}
