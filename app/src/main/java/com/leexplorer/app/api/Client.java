package com.leexplorer.app.api;

import com.leexplorer.app.BuildConfig;
import com.leexplorer.app.api.models.Artwork;
import com.leexplorer.app.models.Gallery;
import com.leexplorer.app.util.AppConstants;
import com.leexplorer.app.util.FakeData;
import com.squareup.okhttp.OkHttpClient;
import java.util.ArrayList;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.subscriptions.Subscriptions;

/**
 * Created by hectormonserrate on 20/02/14.
 */
public class Client {

  private LeexplorerService service;

  public Client(OkHttpClient client) {
    RestAdapter restAdapter = new RestAdapter.Builder().setClient(new OkClient(client))
        .setEndpoint(AppConstants.API_URL)
        .build();

    service = restAdapter.create(LeexplorerService.class);
  }

  public LeexplorerService getService() {
    return service;
  }

  public Observable<ArrayList<com.leexplorer.app.models.Artwork>> getArtworksData(
      final String galleryId) {
    return Observable.create(
        new Observable.OnSubscribeFunc<ArrayList<com.leexplorer.app.models.Artwork>>() {
          @Override
          public Subscription onSubscribe(
              Observer<? super ArrayList<com.leexplorer.app.models.Artwork>> observer) {
            try {
              ArrayList<com.leexplorer.app.models.Artwork> artworks = new ArrayList<>();

              if (BuildConfig.FAKE_DATA) {
                artworks = FakeData.getArtworks();
              } else {
                for (Artwork aaw : service.getArtworks(galleryId)) {
                  artworks.add(com.leexplorer.app.models.Artwork.fromJsonModel(aaw));
                }
              }

              observer.onNext(artworks);

              try {
                // Persist Artworks...
                for (com.leexplorer.app.models.Artwork aw : artworks) {
                  aw.save();
                }
              } catch (Exception e) {
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

  public Observable<ArrayList<Gallery>> getGalleriesData() {
    return Observable.create(
        new Observable.OnSubscribeFunc<ArrayList<com.leexplorer.app.models.Gallery>>() {
          @Override
          public Subscription onSubscribe(
              Observer<? super ArrayList<com.leexplorer.app.models.Gallery>> observer) {
            try {
              ArrayList<Gallery> galleries = new ArrayList<>();

              for (com.leexplorer.app.api.models.Gallery gallery : service.getGalleries()) {
                galleries.add(Gallery.fromApiModel(gallery));
              }

              observer.onNext(galleries);

              try {
                // Persist Galleries...
                for (Gallery gallery : galleries) {
                  gallery.save();
                }
              } catch (Exception e) {
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
