package com.leexplorer.app.fragments;

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
import butterknife.OnClick;
import com.etsy.android.grid.StaggeredGridView;
import com.leexplorer.app.R;
import com.leexplorer.app.adapters.ArtworkAdapter;
import com.leexplorer.app.api.Client;
import com.leexplorer.app.core.AppConstants;
import com.leexplorer.app.core.ApplicationComponent;
import com.leexplorer.app.core.LeexplorerApplication;
import com.leexplorer.app.core.RepeatableRunnable;
import com.leexplorer.app.events.LoadingEvent;
import com.leexplorer.app.events.MainLoadingIndicator;
import com.leexplorer.app.events.artworks.ArtworkClickedEvent;
import com.leexplorer.app.events.audio.AudioProgressEvent;
import com.leexplorer.app.events.autoplay.AutoPlayAudioFinishedEvent;
import com.leexplorer.app.events.autoplay.AutoPlayStatusEvent;
import com.leexplorer.app.events.beacon.AltBeaconsScanResultEvent;
import com.leexplorer.app.models.Artwork;
import com.leexplorer.app.models.Gallery;
import com.leexplorer.app.services.AutoPlayService;
import com.leexplorer.app.services.BeaconScanService;
import com.leexplorer.app.services.MediaPlayerService;
import com.leexplorer.app.util.ble.BeaconArtworkUpdater;
import com.melnykov.fab.FloatingActionButton;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import org.altbeacon.beacon.Beacon;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ArtworkListFragment extends BaseFragment {

  private static final String ARTWORK_LIST_KEY = "arwork_list";
  private static final String ARTWORKS_LOADING_KEY = "artworks_loading";
  private static final String BEACONS_KEY = "beacons";
  private static final String GALLERY_KEY = "gallery";
  private static final String TAG = "ArtworkListFragment";
  private static final String EXTRA_GALLERY = "extra_gallery";

  @Inject Client client;
  @Inject Bus bus;
  @InjectView(R.id.sgvArtworks) StaggeredGridView sgvArtworks;
  @InjectView(R.id.btnAutoplay) FloatingActionButton btnAutoplay;

  protected ArtworkAdapter artworkAdapter;
  private List<Artwork> artworks;
  private List<Beacon> beacons;
  private Artwork currentlyPlaying;
  private boolean artworksLoading;
  private boolean scaningBeacons;
  private MenuItem menuReresh;
  private Gallery gallery;
  private final Handler handler = new Handler();

  RepeatableRunnable statusChecker = new RepeatableRunnable() {
    @Override public void run() {
      if (artworks.size() > 0) {
        scanBeacons();
      }
      handler.postDelayed(this, AppConstants.MILSEC_ARTWORK_REFRESH);
    }

    @Override public void stop() {
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

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
    scaningBeacons = false;

    if (savedInstanceState != null) {
      artworks = savedInstanceState.getParcelableArrayList(ARTWORK_LIST_KEY);
      beacons = savedInstanceState.getParcelableArrayList(BEACONS_KEY);
      gallery = savedInstanceState.getParcelable(GALLERY_KEY);
      artworksLoading = savedInstanceState.getBoolean(ARTWORKS_LOADING_KEY, false);
    } else {
      artworks = new ArrayList<>();
      beacons = new ArrayList<>();
      gallery = getArguments().getParcelable(EXTRA_GALLERY);
      artworksLoading = false;
    }
  }

  @Override protected void injectComponent(ApplicationComponent component) {
    component.inject(this);
  }

  @Override public void onResume() {
    super.onResume();
    bus.register(this);
    if (artworksLoading) {
      bus.post(new MainLoadingIndicator(true));
    } else if (artworks.size() == 0) {
      loadArtworkList();
    }

    statusChecker.run();
    AutoPlayService.checkAutoplayStatus(getActivity());
  }

  @Override public void onPause() {
    bus.unregister(this);
    statusChecker.stop();
    currentlyPlaying = null;
    super.onPause();
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_artwork_list_responsive, container, false);

    ButterKnife.inject(this, rootView);

    artworkAdapter = new ArtworkAdapter(this, artworks);
    sgvArtworks.setAdapter(artworkAdapter);
    btnAutoplay.attachToListView(sgvArtworks);

    return rootView;
  }

  @Override public void onSaveInstanceState(Bundle savedInstanceState) {
    super.onSaveInstanceState(savedInstanceState);
    savedInstanceState.putParcelableArrayList(ARTWORK_LIST_KEY, new ArrayList<>(artworks));
    savedInstanceState.putParcelableArrayList(BEACONS_KEY, new ArrayList<Parcelable>(beacons));
    savedInstanceState.putParcelable(GALLERY_KEY, gallery);
    savedInstanceState.putBoolean(ARTWORKS_LOADING_KEY, artworksLoading);
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.artwork_list, menu);
    menuReresh = menu.findItem(R.id.menuRefresh);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menuRefresh:
        scanBeacons();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @OnClick(R.id.btnAutoplay) public void onAutoplay(View view) {
    startAutoplay();
    btnAutoplay.hide();

    Handler hideHandler = new Handler();
    Runnable hideRunnable = new Runnable() {
      @Override public void run() {
        btnAutoplay.setVisibility(View.GONE);
      }
    };
    hideHandler.postDelayed(hideRunnable, 200);
  }

  private void loadArtworkList() {
    if (artworksLoading) {
      return;
    }

    // Get data from Api or DB
    if (((LeexplorerApplication) getActivity().getApplicationContext()).isOnline()) {
      loadArtworkListFromApi();
    } else {
      loadArtworkListFromDB();
    }
  }

  private void loadArtworkListFromApi() {
    bus.post(new MainLoadingIndicator(true));

    addSubscription(client.getArtworksData(gallery.getGalleryId())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<ArrayList<Artwork>>() {
          @Override public void onCompleted() {
            artworksLoading = false;
            bus.post(new MainLoadingIndicator(false));
          }

          @Override public void onError(Throwable throwable) {
            eventReporter.logException(throwable);
            if (artworks == null || artworks.size() == 0) {
              loadArtworkListFromDB();
            }
            onCompleted();
          }

          @Override public void onNext(ArrayList<Artwork> artworks) {
            updateAdapterDataset(artworks);
          }
        }));
  }

  private void loadArtworkListFromDB() {
    addSubscription(Observable.create(new Observable.OnSubscribe<ArrayList<Artwork>>() {
      @Override public void call(Subscriber<? super ArrayList<Artwork>> subscriber) {
        subscriber.onNext(Artwork.galleryArtworks(gallery.getGalleryId()));
        subscriber.onCompleted();
      }
    })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<ArrayList<Artwork>>() {
          @Override public void onCompleted() {
          }

          @Override public void onError(Throwable throwable) {
          }

          @Override public void onNext(ArrayList<Artwork> artworks) {
            updateAdapterDataset(artworks);
          }
        }));
  }

  private void refreshArtworks() {
    if (!artworksLoading) {
      refreshArtworkAdapter();
    }
  }

  @Override public String getScreenName() {
    return TAG;
  }

  public void updateAdapterDataset(List<Artwork> artworks) {
    Log.d("BeaconArtworkUpdater", "RESET ARTWORKS!!!");
    this.artworks.clear();
    for (Artwork artwork : artworks) {
      this.artworks.add(artwork);
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

    bus.post(new LoadingEvent(true));
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

  public void startAutoplay() {
    Log.d(TAG, "start autoplay");
    Intent i = new Intent(getActivity(), AutoPlayService.class);
    i.putExtra(AutoPlayService.EXTRA_ACTION, AutoPlayService.ACTION_START);
    i.putExtra(AutoPlayService.EXTRA_GALLERY, gallery);
    i.putExtra(AutoPlayService.EXTRA_ARTWORKS, (ArrayList<Artwork>) artworks);
    getActivity().startService(i);
  }

  @Subscribe public void audioProgressReceiver(AudioProgressEvent event) {
    Artwork playingArtwork = event.getArtwork();

    // Exit conditions
    if (artworks == null) {
      return;
    }

    if (playingArtwork.equals(currentlyPlaying)
        && event.getStatus() == currentlyPlaying.getStatus()) {
      return;
    }

    if (currentlyPlaying == null && !gallery.getGalleryId()
        .contentEquals(playingArtwork.getGalleryId())) {
      return;
    }

    // Update needed if we are still here
    currentlyPlaying = null;
    for (Artwork artwork : artworks) {
      artwork.setStatus(MediaPlayerService.Status.Idle);
      if (artwork.equals(playingArtwork)) {
        artwork.setStatus(event.getStatus());
        currentlyPlaying = artwork;
      }
    }

    artworkAdapter.notifyDataSetChanged();
  }

  @Subscribe public void onCheckAutoplayStatusEvent(AutoPlayStatusEvent event) {
    if (event.getStatus() != AutoPlayService.Status.OFF && gallery.equals(event.getGallery())) {
      btnAutoplay.setVisibility(View.GONE);
    } else {
      btnAutoplay.setVisibility(View.VISIBLE);
    }
  }

  @Subscribe public void onAutoplayAudioFinished(AutoPlayAudioFinishedEvent event) {
    if (gallery.equals(event.getGallery())) {
      btnAutoplay.setVisibility(View.VISIBLE);
      btnAutoplay.show();
    }
  }

  @Subscribe public void onAltBeaconsScanResult(AltBeaconsScanResultEvent event) {

    beacons = new ArrayList(event.getBeacons());

    // If this is true, it means we are able to satisfy the
    // scanBeacons call
    if (scaningBeacons) {
      scaningBeacons = false;
      bus.post(new LoadingEvent(false));
      if (menuReresh != null) {
        menuReresh.setVisible(true);
      }
      refreshArtworks();
      return;
    }
  }
}



