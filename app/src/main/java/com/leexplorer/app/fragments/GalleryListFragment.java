package com.leexplorer.app.fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.leexplorer.app.R;
import com.leexplorer.app.adapters.GalleryAdapter;
import com.leexplorer.app.api.Client;
import com.leexplorer.app.core.LeexplorerApplication;
import com.leexplorer.app.events.LoadingEvent;
import com.leexplorer.app.models.Gallery;
import com.leexplorer.app.services.LocationService;
import com.leexplorer.app.util.GalleryComparator;
import com.squareup.otto.Bus;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class GalleryListFragment extends BaseFragment {
  private static final String TAG = "com.leexplorer.galleryListFragment";
  public Callbacks callbacks;
  @Inject Client client;
  @Inject Bus bus;
  @InjectView(R.id.lvGalleries) ListView lvGalleries;
  private List<Gallery> galleries;
  private GalleryAdapter galleryAdapter;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    galleries = new ArrayList<>();
    galleryAdapter = new GalleryAdapter(this, galleries);
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB) @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_gallery_list, container, false);
    ButterKnife.inject(this, view);
    lvGalleries.setAdapter(galleryAdapter);
    loadArtworkList();
    return view;
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);

    super.onAttach(activity);
    if (activity instanceof Callbacks) {
      callbacks = (Callbacks) activity;
    } else {
      throw new ClassCastException(
          activity.toString() + " must implement GalleryListFragment.Callbacks");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    callbacks = null;
  }

  private void loadArtworkList() {
    //Get data from Api or DB
    if (((LeexplorerApplication) getActivity().getApplicationContext()).isOnline()) {
      loadGalleryListFromApi();
    } else {
      loadGalleryListFromDB();
    }
  }

  private void loadGalleryListFromApi() {
    if (callbacks != null) {
      bus.post(new LoadingEvent(true));
    }

    addSubscription(client.getGalleriesData()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<ArrayList<Gallery>>() {
          @Override
          public void onCompleted() {
          }

          @Override
          public void onError(Throwable throwable) {
            eventReporter.logException(throwable);
            if (callbacks != null) {
              bus.post(new LoadingEvent(false));
            }
          }

          @Override
          public void onNext(ArrayList<Gallery> galleries) {
            updateAdapterDataset(galleries);
            if (callbacks != null) {
              bus.post(new LoadingEvent(false));
            }
          }
        }));
  }

  private void updateAdapterDataset(List<Gallery> galleries) {
    this.galleries.clear();
    LocationService service = new LocationService(getActivity());
    Location currentLocation = null;
    if (service.isLocationAvailable()) {
      currentLocation = service.getLocation();
    }

    for (Gallery gallery : galleries) {
      if (currentLocation != null) {
        float[] results = new float[4];
        Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(),
            gallery.getLatitude(), gallery.getLongitude(), results);
        //float distanceInMiles = /1609.344f;
        gallery.setDistanceFromCurrentLocation(results[0]);
      }
      this.galleries.add(gallery);
    }
    Collections.sort(this.galleries, new GalleryComparator());
    galleryAdapter.notifyDataSetChanged();
    if (callbacks.isTabletMode()) {
      callbacks.loadGalleryDetails(this.galleries.get(0));
    }
  }

  private void loadGalleryListFromDB() {
    if (callbacks != null) {
      bus.post(new LoadingEvent(true));
    }

    addSubscription(Observable.create(new Observable.OnSubscribe<List<Gallery>>() {
      @Override
      public void call(Subscriber<? super List<Gallery>> subscriber) {
        subscriber.onNext(Gallery.getAll());
        subscriber.onCompleted();
      }
    })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<List<Gallery>>() {
          @Override public void onCompleted() {
            bus.post(new LoadingEvent(false));
          }

          @Override public void onError(Throwable throwable) {
          }

          @Override public void onNext(List<Gallery> galleries) {
            updateAdapterDataset(galleries);
          }
        }));

    if (callbacks != null) {
      bus.post(new LoadingEvent(false));
    }
  }

  public ArrayList<Gallery> getGalleries() {
    return new ArrayList<>(galleries);
  }

  public interface Callbacks {
    void loadGalleryDetails(Gallery gallery);

    boolean isTabletMode();
  }

  @Override public String getScreenName() {
    return TAG;
  }
}
