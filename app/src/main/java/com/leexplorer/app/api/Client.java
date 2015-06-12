package com.leexplorer.app.api;

import com.leexplorer.app.BuildConfig;
import com.leexplorer.app.api.models.Artwork;
import com.leexplorer.app.core.LeexplorerApplication;
import com.leexplorer.app.models.Gallery;
import com.leexplorer.app.util.offline.FakeData;
import java.util.ArrayList;
import javax.inject.Inject;
import retrofit.RestAdapter;
import rx.Observable;
import rx.Subscriber;

public class Client {

  private LeexplorerService service;
  @Inject RestAdapter restAdapter;

  public Client(final LeexplorerApplication application) {
    application.getComponent().inject(this);
    service = restAdapter.create(LeexplorerService.class);
  }

  public LeexplorerService getService() {
    return service;
  }

  public Observable<ArrayList<com.leexplorer.app.models.Artwork>> getArtworksData(
      final String galleryId) {
    return Observable.create(
        new Observable.OnSubscribe<ArrayList<com.leexplorer.app.models.Artwork>>() {
          @Override public void call(
              Subscriber<? super ArrayList<com.leexplorer.app.models.Artwork>> subscriber) {
            try {
              ArrayList<com.leexplorer.app.models.Artwork> artworks = new ArrayList<>();

              if (BuildConfig.FAKE_DATA) {
                artworks = FakeData.getArtworks();
              } else {
                for (Artwork apiArtwork : service.getArtworks(galleryId)) {
                  artworks.add(com.leexplorer.app.models.Artwork.fromJsonModel(apiArtwork));
                }
              }

              subscriber.onNext(artworks);

              try {
                // Persist Artworks...
                for (com.leexplorer.app.models.Artwork artwork : artworks) {
                  artwork.save();
                }
              } catch (Exception e) {
                e.printStackTrace();
              }

              subscriber.onCompleted();
            } catch (Exception e) {
              subscriber.onError(e);
            }

            return;
          }
        });
  }

  public Observable<ArrayList<Gallery>> getGalleriesData() {
    return Observable.create(
        new Observable.OnSubscribe<ArrayList<com.leexplorer.app.models.Gallery>>() {
          @Override public void call(
              Subscriber<? super ArrayList<com.leexplorer.app.models.Gallery>> subscriber) {
            try {
              ArrayList<Gallery> galleries = new ArrayList<>();

              for (com.leexplorer.app.api.models.Gallery gallery : service.getGalleries()) {
                galleries.add(Gallery.fromApiModel(gallery));
              }

              subscriber.onNext(galleries);

              try {
                // Persist Galleries...
                for (Gallery gallery : galleries) {
                  gallery.save();
                }
              } catch (Exception e) {
                e.printStackTrace();
              }

              subscriber.onCompleted();
            } catch (Exception e) {
              subscriber.onError(e);
            }

            return;
          }
        });
  }
}
