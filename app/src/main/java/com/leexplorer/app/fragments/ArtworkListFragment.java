package com.leexplorer.app.fragments;

/**
 * Created by hectormonserrate on 10/02/14.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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
import com.leexplorer.app.core.LeexplorerApplication;
import com.leexplorer.app.events.ArtworkClickedEvent;
import com.leexplorer.app.events.BeaconsScanResultEvent;
import com.leexplorer.app.models.Artwork;
import com.leexplorer.app.models.Gallery;
import com.leexplorer.app.services.BeaconScanService;
import com.leexplorer.app.util.ble.Beacon;
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

  private static final String ARTWORK_LIST = "arwork_list";
  private static final String TAG = "com.leexplorer.artworklistfragement";

  private static final String EXTRA_GALLERY = "extra_gallery";
  public Callbacks callbacks;
  protected ArtworkAdapter artworkAdapter;
  @Inject Client client;
  @Inject Bus bus;
  @InjectView(R.id.sgvArtworks) StaggeredGridView sgvArtworks;
  private List<Artwork> artworks = new ArrayList<>();
  private List<Beacon> beacons = new ArrayList<>();
  private boolean newBeaconInfo;
  private boolean scaningBeacons;
  private MenuItem menuReresh;
  private Gallery gallery;

  @Subscribe public void onBeaconsScanResult(BeaconsScanResultEvent event) {
    List<Beacon> newBeacons = event.getBeacons();
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

    distancesChangesCheck(beacons);
  }

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
    super.onDetach();
    callbacks = null;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
    newBeaconInfo = false;
    scaningBeacons = false;
    gallery = getArguments().getParcelable(EXTRA_GALLERY);
  }

  @Override
  public void onResume() {
    super.onResume();
    bus.register(this);
  }

  @Override
  public void onPause() {
    bus.unregister(this);
    super.onPause();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_artwork_list_responsive, container, false);

    ButterKnife.inject(this, rootView);

    if (savedInstanceState != null) {
      artworks = savedInstanceState.getParcelableArrayList(ARTWORK_LIST);
    } else {
      loadArtworkList();
    }

    artworkAdapter = new ArtworkAdapter(this, artworks);
    sgvArtworks.setAdapter(artworkAdapter);

    return rootView;
  }

  @Override
  public void onSaveInstanceState(Bundle savedInstanceState) {
    super.onSaveInstanceState(savedInstanceState);
    savedInstanceState.putParcelableArrayList(ARTWORK_LIST, new ArrayList<>(artworks));
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.artwork_list, menu);
    menuReresh = menu.findItem(R.id.menuRefresh);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menuRefresh:
        scanBeacons();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  private void loadArtworkList() {
    // First explicitly call scan beacons to get info asap
    scanBeacons();

    // Get data from Api or DB
    if (LeexplorerApplication.isOnline(getActivity())) {
      loadArtworkListFromApi();
    } else {
      loadArtworkListFromDB();
    }
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
            throwable.printStackTrace();
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
    if (artworks.size() > 0) {
      refreshArtworkAdapter();
    } else {
      loadArtworkList();
    }
  }

  @Override public String getScreenName() {
    return TAG;
  }

  public void updateAdapterDataset(List<Artwork> aws) {
    artworks.clear();
    for (Artwork aw : aws) {
      artworks.add(aw);
    }
    refreshArtworkAdapter();
  }

  private void refreshArtworkAdapter() {
    BeaconArtworkUpdater.updateDistances(artworks, beacons);

    Collections.sort(artworks, new Artwork.ArtworkComparable());

    artworkAdapter.notifyDataSetChanged();
    newBeaconInfo = false;
  }

  /**
   * Called by the host activity to get the fragment artworks
   */
  public List<Artwork> getArtworks() {
    return artworks;
  }

  private void scanBeacons() {
    if (getActivity() == null) {
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

    scaningBeacons = true;

    Intent i = new Intent(getActivity(), BeaconScanService.class);
    getActivity().startService(i);
  }

  private void distancesChangesCheck(List<Beacon> beacons) {
    if (newBeaconInfo) {
      return;
    }

    BeaconArtworkUpdater.updateDistances(artworks, beacons);
    ArrayList<String> currentOrderedMacs = new ArrayList<>();
    ArrayList<String> newOrderedMacs = new ArrayList<>();
    for (Artwork aw : artworks) {
      currentOrderedMacs.add(aw.getMac());
    }
    Collections.sort(artworks, new Artwork.ArtworkComparable());
    for (Artwork aw : artworks) {
      newOrderedMacs.add(aw.getMac());
    }

    for (int i = 0; i < currentOrderedMacs.size(); i++) {
      if (!currentOrderedMacs.get(i).equals(newOrderedMacs.get(i))) {
        newBeaconInfo = true;
        break;
      }
    }
  }

  public void onArtworkClicked(Artwork artwork) {
    bus.post(new ArtworkClickedEvent(artwork, artworks));
  }

  public interface Callbacks {
    void onLoading(boolean loading);
  }
}



