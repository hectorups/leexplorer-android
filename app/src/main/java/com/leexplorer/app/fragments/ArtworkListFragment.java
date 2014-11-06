package com.leexplorer.app.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.etsy.android.grid.StaggeredGridView;
import com.leexplorer.app.R;
import com.leexplorer.app.adapters.ArtworkAdapter;
import com.leexplorer.app.api.Client;
import com.leexplorer.app.core.AppConstants;
import com.leexplorer.app.core.LeexplorerApplication;
import com.leexplorer.app.core.RepeatableRunnable;
import com.leexplorer.app.events.ArtworkClickedEvent;
import com.leexplorer.app.events.BeaconsScanResultEvent;
import com.leexplorer.app.events.autoplay.AutoPlayAudioFinishedEvent;
import com.leexplorer.app.events.autoplay.AutoPlayStatusEvent;
import com.leexplorer.app.models.Artwork;
import com.leexplorer.app.models.FilteredIBeacon;
import com.leexplorer.app.models.Gallery;
import com.leexplorer.app.services.AutoPlayService;
import com.leexplorer.app.services.BeaconScanService;
import com.leexplorer.app.util.ble.BeaconArtworkUpdater;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ArtworkListFragment extends BaseFragment {

  private static final String ARTWORK_LIST_KEY = "arwork_list";
  private static final String BEACONS_KEY = "beacons";
  private static final String GALLERY_KEY = "gallery";
  private static final String TAG = "com.leexplorer.artworklistfragement";

  private static final String EXTRA_GALLERY = "extra_gallery";
  public Callbacks callbacks;
  protected ArtworkAdapter artworkAdapter;
  @Inject Client client;
  @Inject Bus bus;
  @InjectView(R.id.sgvArtworks) StaggeredGridView sgvArtworks;
  private List<Artwork> artworks;
  private List<FilteredIBeacon> beacons;
  private boolean artworksLoaded;
  private boolean scaningBeacons;
  private MenuItem menuReresh;
  private MenuItem menuAutoplay;
  private Gallery gallery;
  private final Handler handler = new Handler();

  RepeatableRunnable statusChecker = new RepeatableRunnable() {
    @Override
    public void run() {
      scanBeacons();
      handler.postDelayed(this, AppConstants.MILSEC_ARTWORK_REFRESH);
    }

    @Override
    public void stop() {
      handler.removeCallbacks(this);
    }
  };

  public static ArtworkListFragment newInstance(Gallery gallery) {
    Bundle args = new Bundle();
    args.putParcelable(EXTRA_GALLERY, gallery);

    ArtworkListFragment fragment = new ArtworkListFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);

    if (activity instanceof Callbacks) {
      callbacks = (Callbacks) activity;
    } else {
      throw new ClassCastException(
          activity.toString() + " must implement ArtworkListFragment.Callbacks");
    }
  }

  @Override
  public void onDetach() {
    callbacks = null;
    super.onDetach();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
    scaningBeacons = false;

    if (savedInstanceState != null) {
      artworks = savedInstanceState.getParcelableArrayList(ARTWORK_LIST_KEY);
      beacons = savedInstanceState.getParcelableArrayList(BEACONS_KEY);
      gallery = savedInstanceState.getParcelable(GALLERY_KEY);
      artworksLoaded = true;
    } else {
      artworks = new ArrayList<>();
      beacons = new ArrayList<>();
      gallery = getArguments().getParcelable(EXTRA_GALLERY);
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    bus.register(this);
    statusChecker.run();
  }

  @Override
  public void onPause() {
    bus.unregister(this);
    statusChecker.stop();
    super.onPause();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_artwork_list_responsive, container, false);

    ButterKnife.inject(this, rootView);

    artworkAdapter = new ArtworkAdapter(this, artworks);
    sgvArtworks.setAdapter(artworkAdapter);

    refreshArtworks();

    return rootView;
  }

  @Override
  public void onSaveInstanceState(Bundle savedInstanceState) {
    super.onSaveInstanceState(savedInstanceState);
    savedInstanceState.putParcelableArrayList(ARTWORK_LIST_KEY, new ArrayList<>(artworks));
    savedInstanceState.putParcelableArrayList(BEACONS_KEY, new ArrayList<Parcelable>(beacons));
    savedInstanceState.putParcelable(GALLERY_KEY, gallery);
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.artwork_list, menu);
    menuReresh = menu.findItem(R.id.menuRefresh);
    menuAutoplay = menu.findItem(R.id.menuAutoplay);
    AutoPlayService.checkAutoplayStatus(getActivity());
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menuRefresh:
        scanBeacons();
        return true;
      case R.id.menuAutoplay:
        startAutoplay();
        menuAutoplay.setVisible(false);
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  private void loadArtworkList() {
    // Get data from Api or DB
    if (((LeexplorerApplication) getActivity().getApplicationContext()).isOnline()) {
      loadArtworkListFromApi();
    } else {
      loadArtworkListFromDB();
    }

    artworksLoaded = true;
  }

  private void loadArtworkListFromApi() {
    if (callbacks != null) {
      callbacks.onLoading(true);
    }

    addSubscription(client.getArtworksData(gallery.getGalleryId())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<ArrayList<Artwork>>() {
          @Override
          public void onCompleted() {
          }

          @Override
          public void onError(Throwable throwable) {
            eventReporter.logException(throwable);
            if (callbacks != null) {
              callbacks.onLoading(false);
            }
            if (artworks == null || artworks.size() == 0) {
              loadArtworkListFromDB();
            }
          }

          @Override
          public void onNext(ArrayList<Artwork> aws) {
            updateAdapterDataset(aws);
            if (callbacks != null) {
              callbacks.onLoading(false);
            }
          }
        }));
  }

  private void loadArtworkListFromDB() {
    if (callbacks != null) {
      callbacks.onLoading(true);
    }

    addSubscription(Observable.create(new Observable.OnSubscribe<ArrayList<Artwork>>() {
      @Override public void call(Subscriber<? super ArrayList<Artwork>> subscriber) {
        subscriber.onNext(Artwork.galleryArtworks(gallery.getGalleryId()));
        subscriber.onCompleted();
      }
    })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<ArrayList<Artwork>>() {
          @Override
          public void onCompleted() {
            callbacks.onLoading(false);
          }

          @Override
          public void onError(Throwable throwable) {
          }

          @Override
          public void onNext(ArrayList<Artwork> aws) {
            updateAdapterDataset(aws);
          }
        }));

    if (callbacks != null) {
      callbacks.onLoading(false);
    }
  }

  private void refreshArtworks() {
    if (artworksLoaded) {
      refreshArtworkAdapter();
    } else {
      loadArtworkList();
    }
  }

  @Override public String getScreenName() {
    return TAG;
  }

  public void updateAdapterDataset(List<Artwork> aws) {
    Log.d("BeaconArtworkUpdater", "RESET ARTWORKS!!!");
    artworks.clear();
    for (Artwork aw : aws) {
      artworks.add(aw);
    }
    refreshArtworkAdapter();
    scanBeacons();
  }

  private void refreshArtworkAdapter() {
    BeaconArtworkUpdater.updateDistances(artworks, beacons);
    Collections.sort(artworks, new Artwork.ArtworkComparable());
    artworkAdapter.notifyDataSetChanged();
  }

  /**
   * Called by the host activity to get the fragment images
   */
  public List<Artwork> getArtworks() {
    return artworks;
  }

  private void scanBeacons() {
    if (getActivity() == null || scaningBeacons) {
      return;
    }

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
      return;
    }

    if (callbacks != null) {
      callbacks.onLoading(true);
    }
    if (menuReresh != null) {
      menuReresh.setVisible(false);
    }

    Log.d(TAG, "scan for beacons");

    scaningBeacons = true;

    Intent i = new Intent(getActivity(), BeaconScanService.class);
    getActivity().startService(i);
  }

  public void onArtworkClicked(Artwork artwork) {
    bus.post(new ArtworkClickedEvent(artwork, artworks));
  }

  public interface Callbacks {
    void onLoading(boolean loading);
  }

  public void startAutoplay() {
    Log.d(TAG, "start autoplay");
    Intent i = new Intent(getActivity(), AutoPlayService.class);
    i.putExtra(AutoPlayService.EXTRA_ACTION, AutoPlayService.ACTION_START);
    i.putExtra(AutoPlayService.EXTRA_GALLERY, gallery);
    i.putExtra(AutoPlayService.EXTRA_ARTWORKS, (ArrayList<Artwork>) artworks);
    getActivity().startService(i);
  }

  @Subscribe public void onCheckAutoplayStatusEvent(AutoPlayStatusEvent event) {
    if (menuAutoplay == null) {
      return;
    }

    if (event.getStatus() != AutoPlayService.Status.OFF && gallery.equals(event.getGallery())) {
      menuAutoplay.setVisible(false);
    } else {
      menuAutoplay.setVisible(true);
    }
  }

  @Subscribe public void onAutoplayAudioFinished(AutoPlayAudioFinishedEvent event) {
    if (gallery.equals(event.getGallery())) {
      menuAutoplay.setVisible(true);
    }
  }

  @Subscribe public void onBeaconsScanResult(BeaconsScanResultEvent event) {
    List<FilteredIBeacon> newBeacons = event.getBeacons();
    Log.d(TAG, "Beacons detected: " + newBeacons.size());

    beacons = newBeacons;

    // If this is true, it means we are able to satisfy the
    // scanBeacons call
    if (scaningBeacons) {
      scaningBeacons = false;
      if (callbacks != null) {
        callbacks.onLoading(false);
      }
      if (menuReresh != null) {
        menuReresh.setVisible(true);
      }
      refreshArtworks();
      return;
    }
  }
}



