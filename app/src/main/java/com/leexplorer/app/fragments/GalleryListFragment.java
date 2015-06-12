package com.leexplorer.app.fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.leexplorer.app.R;
import com.leexplorer.app.adapters.GalleryAdapter;
import com.leexplorer.app.api.Client;
import com.leexplorer.app.core.AppConstants;
import com.leexplorer.app.core.ApplicationComponent;
import com.leexplorer.app.core.LeexplorerApplication;
import com.leexplorer.app.events.MainLoadingIndicator;
import com.leexplorer.app.models.Gallery;
import com.leexplorer.app.util.GalleryComparator;
import com.leexplorer.app.util.PreferenceUtils;
import com.squareup.otto.Bus;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class GalleryListFragment extends BaseFragment {
  private static final String TAG = "com.leexplorer.galleryListFragment";
  private static final String GALLERIES_KEY = "galleries";
  private static final String GALLERIES_LOADING_KEY = "galleries_loading";
  public Callbacks callbacks;
  @Inject Client client;
  @Inject Bus bus;
  @InjectView(R.id.lvGalleries) ListView lvGalleries;
  @InjectView(R.id.srGalleries) SwipeRefreshLayout swipeView;
  private List<Gallery> galleries;
  private GalleryAdapter galleryAdapter;
  private boolean galleriesLoading;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (savedInstanceState != null) {
      galleries = savedInstanceState.getParcelableArrayList(GALLERIES_KEY);
      galleriesLoading = savedInstanceState.getBoolean(GALLERIES_LOADING_KEY, false);
    } else {
      galleries = new ArrayList<>();
      galleriesLoading = false;
    }

    galleryAdapter = new GalleryAdapter(this, galleries);
  }

  @Override protected void injectComponent(ApplicationComponent component) {
    component.inject(this);
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB) @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.fragment_gallery_list, container, false);
    ButterKnife.inject(this, view);

    lvGalleries.setAdapter(galleryAdapter);
    setupSwipe();

    return view;
  }

  private void setupSwipe() {
    swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override public void onRefresh() {
        loadGalleries();
      }
    });

    swipeView.setColorSchemeResources(R.color.refresh_progress_1, R.color.refresh_progress_2,
        R.color.refresh_progress_3);
  }

  @Override public void onResume() {
    super.onResume();
    if (galleriesLoading) {
      bus.post(new MainLoadingIndicator(true));
    } else if (galleries.size() == 0) {
      loadGalleries();
    }
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putParcelableArrayList(GALLERIES_KEY, new ArrayList<Parcelable>(galleries));
  }

  @Override public void onPause() {
    bus.post(new MainLoadingIndicator(false));
    super.onPause();
  }

  @Override public void onAttach(Activity activity) {
    super.onAttach(activity);

    super.onAttach(activity);
    if (activity instanceof Callbacks) {
      callbacks = (Callbacks) activity;
    } else {
      throw new ClassCastException(
          activity.toString() + " must implement GalleryListFragment.Callbacks");
    }
  }

  @Override public void onDetach() {
    super.onDetach();
    callbacks = null;
  }

  private void loadGalleries() {
    //Get data from Api or DB
    if (galleriesLoading) {
      return;
    }

    galleriesLoading = true;
    if (((LeexplorerApplication) getActivity().getApplicationContext()).isOnline()) {
      loadGalleryListFromApi();
    } else {
      loadGalleryListFromDB();
    }
  }

  private void loadGalleryListFromApi() {
    if (!swipeView.isRefreshing()) {
      bus.post(new MainLoadingIndicator(true));
    }

    addSubscription(client.getGalleriesData()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<ArrayList<Gallery>>() {
          @Override public void onCompleted() {
            bus.post(new MainLoadingIndicator(false));
            if (swipeView != null) {
              swipeView.setRefreshing(false);
            }
            galleriesLoading = false;
          }

          @Override public void onError(Throwable throwable) {
            eventReporter.logException(throwable);
            onCompleted();
            loadGalleryListFromDB();
          }

          @Override public void onNext(ArrayList<Gallery> galleries) {
            updateAdapterDataset(galleries);
          }
        }));
  }

  private void updateAdapterDataset(List<Gallery> galleries) {
    this.galleries.clear();
    Location currentLocation = null;
    Double latitude =
        PreferenceUtils.getDouble(getActivity(), AppConstants.KEY_LAST_KNOWN_LATITUDE, null);
    Double longitude =
        PreferenceUtils.getDouble(getActivity(), AppConstants.KEY_LAST_KNOWN_LONGITUDE, null);
    if (latitude != null && longitude != null) {
      currentLocation = new Location("local");
      currentLocation.setLongitude(longitude);
      currentLocation.setLatitude(latitude);
    }

    for (Gallery gallery : galleries) {
      if (currentLocation != null) {
        float[] results = new float[4];
        Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(),
            gallery.getLatitude(), gallery.getLongitude(), results);
        gallery.setDistanceFromCurrentLocation(results[0]);
      }
      this.galleries.add(gallery);
    }
    Collections.sort(this.galleries, new GalleryComparator());
    galleryAdapter.notifyDataSetChanged();

    if (callbacks != null) {
      callbacks.galleriesLoaded(galleries);
    }
  }

  private void loadGalleryListFromDB() {
    addSubscription(Observable.create(new Observable.OnSubscribe<List<Gallery>>() {
      @Override public void call(Subscriber<? super List<Gallery>> subscriber) {
        subscriber.onNext(Gallery.getAll());
        subscriber.onCompleted();
      }
    })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<List<Gallery>>() {
          @Override public void onCompleted() {
            swipeView.setRefreshing(false);
            galleriesLoading = false;
          }

          @Override public void onError(Throwable throwable) {
            onCompleted();
          }

          @Override public void onNext(List<Gallery> galleries) {
            for (Iterator<Gallery> it = galleries.iterator(); it.hasNext(); ) {
              if (!it.next().isGalleryDownloaded()) {
                it.remove();
              }
            }

            updateAdapterDataset(galleries);
          }
        }));
  }

  public ArrayList<Gallery> getGalleries() {
    return new ArrayList<>(galleries);
  }

  public interface Callbacks {
    void loadGalleryDetails(Gallery gallery);

    void galleriesLoaded(List<Gallery> galleries);
  }

  @Override public String getScreenName() {
    return TAG;
  }
}
